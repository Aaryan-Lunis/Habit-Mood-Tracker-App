package com.example.habitmoodtracker.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitmoodtracker.AuthManager;
import com.example.habitmoodtracker.FirebaseHelper;
import com.example.habitmoodtracker.R;
import com.example.habitmoodtracker.HabitEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private LineChart moodLineChart, correlationChart;
    private BarChart habitCompletionChart, weeklyBarChart;
    private PieChart sleepPieChart;
    private RadarChart weeklyRadarChart;
    private LinearLayout dashboardStatsRow;
    private TextView textViewAverageMood, textViewAverageEnergy, textViewBestHabit, textViewInsight;
    private CardView cardInsight;
    private Handler handler;
    private Runnable updateRunnable;

    public DashboardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews(view);
        setupRealtimeUpdates();
        updateDashboard();

        return view;
    }

    private void initViews(View view) {
        moodLineChart = view.findViewById(R.id.moodLineChart);
        correlationChart = view.findViewById(R.id.correlationChart);
        habitCompletionChart = view.findViewById(R.id.habitCompletionChart);
        weeklyBarChart = view.findViewById(R.id.weeklyBarChart);
        sleepPieChart = view.findViewById(R.id.sleepPieChart);
        weeklyRadarChart = view.findViewById(R.id.weeklyRadarChart);
        dashboardStatsRow = view.findViewById(R.id.dashboardStatsRow);
        textViewAverageMood = view.findViewById(R.id.textViewAverageMood);
        textViewAverageEnergy = view.findViewById(R.id.textViewAverageEnergy);
        textViewBestHabit = view.findViewById(R.id.textViewBestHabit);
        textViewInsight = view.findViewById(R.id.textViewInsight);
        cardInsight = view.findViewById(R.id.cardInsight);
    }

    private void setupRealtimeUpdates() {
        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateDashboard();
                handler.postDelayed(this, 10000); // Update every 10 seconds
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDashboard();
        handler.postDelayed(updateRunnable, 10000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable);
    }

    private void updateDashboard() {
        // Get current user ID
        AuthManager authManager = AuthManager.getInstance(requireContext());
        String userId = authManager.getCurrentUserId();

        if (userId == null) {
            setEmptyState();
            Toast.makeText(getContext(), "Please log in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch from Firebase
        FirebaseHelper.getEntriesOnce(userId, new FirebaseHelper.OnDataLoadListener() {
            @Override
            public void onDataLoaded(List<HabitEntry> entries) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    if (entries == null || entries.isEmpty()) {
                        setEmptyState();
                        return;
                    }

                    Collections.sort(entries, (a, b) -> a.getDate().compareTo(b.getDate()));

                    setupStatCards(entries);
                    setupMoodTrendChart(entries);
                    setupCorrelationChart(entries);
                    setupHabitCompletionChart(entries);
                    setupWeeklyPerformanceChart(entries);
                    setupSleepDistributionChart(entries);
                    setupWeeklyRadarChart(entries);
                    generateInsight(entries);
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    setEmptyState();
                    Toast.makeText(getContext(),
                            "Error loading data: " + error,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setEmptyState() {
        textViewAverageMood.setText("No data yet");
        textViewAverageEnergy.setText("Start logging!");
        textViewBestHabit.setText("N/A");
        textViewInsight.setText("Log your first entry to see insights!");
    }

    private void setupStatCards(List<HabitEntry> entries) {
        dashboardStatsRow.removeAllViews();

        int currentStreak = calculateStreak(entries);
        int longestStreak = calculateLongestStreak(entries);
        int totalEntries = entries.size();

        addStatCard("Current Streak", currentStreak + " days", "#8B5CF6");
        addStatCard("Longest Streak", longestStreak + " days", "#6366F1");
        addStatCard("Total Entries", String.valueOf(totalEntries), "#EC4899");

        float avgMood = calculateAverage(entries, true);
        float avgEnergy = calculateAverage(entries, false);

        textViewAverageMood.setText(String.format("Avg Mood: %.1f/10", avgMood));
        textViewAverageEnergy.setText(String.format("Avg Energy: %.1f/10", avgEnergy));
    }

    private void addStatCard(String title, String value, String colorHex) {
        View card = getLayoutInflater().inflate(R.layout.stat_card, dashboardStatsRow, false);
        TextView titleView = card.findViewById(R.id.textViewStatTitle);
        TextView valueView = card.findViewById(R.id.textViewStatValue);

        titleView.setText(title);
        valueView.setText(value);
        card.setBackgroundColor(Color.parseColor(colorHex));

        dashboardStatsRow.addView(card);
    }

    private void setupMoodTrendChart(List<HabitEntry> entries) {
        if (entries.isEmpty()) {
            moodLineChart.clear();
            return;
        }

        List<Entry> moodEntries = new ArrayList<>();
        List<Entry> energyEntries = new ArrayList<>();

        int dataPoints = Math.min(entries.size(), 30); // Last 30 entries
        if (dataPoints < 2) {
            moodLineChart.clear();
            return;
        }

        for (int i = 0; i < dataPoints; i++) {
            HabitEntry entry = entries.get(entries.size() - dataPoints + i);
            moodEntries.add(new Entry(i, entry.getMoodScore()));
            energyEntries.add(new Entry(i, entry.getEnergyLevel()));
        }

        LineDataSet moodDataSet = new LineDataSet(moodEntries, "Mood");
        moodDataSet.setColor(Color.parseColor("#F59E0B"));
        moodDataSet.setLineWidth(3f);
        moodDataSet.setCircleColor(Color.parseColor("#F59E0B"));
        moodDataSet.setCircleRadius(4f);
        moodDataSet.setDrawCircleHole(false);
        moodDataSet.setDrawValues(false);
        moodDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineDataSet energyDataSet = new LineDataSet(energyEntries, "Energy");
        energyDataSet.setColor(Color.parseColor("#10B981"));
        energyDataSet.setLineWidth(3f);
        energyDataSet.setCircleColor(Color.parseColor("#10B981"));
        energyDataSet.setCircleRadius(4f);
        energyDataSet.setDrawCircleHole(false);
        energyDataSet.setDrawValues(false);
        energyDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(moodDataSet, energyDataSet);
        moodLineChart.setData(lineData);
        moodLineChart.getDescription().setEnabled(false);
        moodLineChart.setDrawGridBackground(false);

        XAxis xAxis = moodLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#64748B"));

        YAxis leftAxis = moodLineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E5E7EB"));
        leftAxis.setTextColor(Color.parseColor("#64748B"));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(10f);

        moodLineChart.getAxisRight().setEnabled(false);
        moodLineChart.getLegend().setTextColor(Color.parseColor("#374151"));
        moodLineChart.animateX(1000);
        moodLineChart.invalidate();
    }

    private void setupCorrelationChart(List<HabitEntry> entries) {
        if (entries.isEmpty()) {
            correlationChart.clear();
            return;
        }

        List<Entry> correlationEntries = new ArrayList<>();
        int dataPoints = Math.min(entries.size(), 20);

        if (dataPoints < 2) {
            correlationChart.clear();
            return;
        }

        for (int i = 0; i < dataPoints; i++) {
            HabitEntry entry = entries.get(entries.size() - 1 - i);
            if (entry.getSleepHours() > 0) {
                correlationEntries.add(new Entry(entry.getSleepHours(), entry.getMoodScore()));
            }
        }

        if (correlationEntries.size() < 2) {
            correlationChart.clear();
            return;
        }

        LineDataSet dataSet = new LineDataSet(correlationEntries, "Sleep vs Mood");
        dataSet.setColor(Color.parseColor("#6366F1"));
        dataSet.setCircleColor(Color.parseColor("#8B5CF6"));
        dataSet.setCircleRadius(6f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);
        correlationChart.setData(lineData);
        correlationChart.getDescription().setEnabled(false);
        correlationChart.setDrawGridBackground(false);

        XAxis xAxis = correlationChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.parseColor("#E5E7EB"));
        xAxis.setTextColor(Color.parseColor("#64748B"));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1fh", value);
            }
        });

        YAxis leftAxis = correlationChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E5E7EB"));
        leftAxis.setTextColor(Color.parseColor("#64748B"));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(10f);

        correlationChart.getAxisRight().setEnabled(false);
        correlationChart.getLegend().setTextColor(Color.parseColor("#374151"));
        correlationChart.animateXY(1000, 1000);
        correlationChart.invalidate();
    }

    private void setupHabitCompletionChart(List<HabitEntry> entries) {
        if (entries.isEmpty()) {
            habitCompletionChart.clear();
            textViewBestHabit.setText("Best Habit: N/A");
            return;
        }

        int exerciseCount = 0, meditationCount = 0, socialCount = 0;

        for (HabitEntry entry : entries) {
            if (entry.isExercise()) exerciseCount++;
            if (entry.isMeditation()) meditationCount++;
            if (entry.isSocialTime()) socialCount++;
        }

        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, exerciseCount));
        barEntries.add(new BarEntry(1, meditationCount));
        barEntries.add(new BarEntry(2, socialCount));

        BarDataSet dataSet = new BarDataSet(barEntries, "Habit Completion");
        dataSet.setColors(
                Color.parseColor("#EF4444"),
                Color.parseColor("#8B5CF6"),
                Color.parseColor("#EC4899")
        );
        dataSet.setValueTextColor(Color.parseColor("#374151"));
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        habitCompletionChart.setData(barData);
        habitCompletionChart.getDescription().setEnabled(false);
        habitCompletionChart.setDrawGridBackground(false);
        habitCompletionChart.setFitBars(true);

        XAxis xAxis = habitCompletionChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#64748B"));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String[] labels = {"Exercise", "Meditation", "Social"};
                return labels[(int) value];
            }
        });

        habitCompletionChart.getAxisLeft().setDrawGridLines(true);
        habitCompletionChart.getAxisLeft().setGridColor(Color.parseColor("#E5E7EB"));
        habitCompletionChart.getAxisLeft().setTextColor(Color.parseColor("#64748B"));
        habitCompletionChart.getAxisRight().setEnabled(false);
        habitCompletionChart.getLegend().setEnabled(false);
        habitCompletionChart.animateY(1000);
        habitCompletionChart.invalidate();

        String bestHabit = "Exercise";
        int maxCount = exerciseCount;
        if (meditationCount > maxCount) {
            bestHabit = "Meditation";
            maxCount = meditationCount;
        }
        if (socialCount > maxCount) {
            bestHabit = "Social Time";
        }
        textViewBestHabit.setText("Best Habit: " + bestHabit);
    }

    private void setupWeeklyPerformanceChart(List<HabitEntry> entries) {
        Map<Integer, Float> weeklyData = new HashMap<>();
        Map<Integer, Integer> weeklyCount = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        for (HabitEntry entry : entries) {
            cal.setTime(entry.getDate());
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            float combined = (entry.getMoodScore() + entry.getEnergyLevel()) / 2f;
            weeklyData.put(dayOfWeek, weeklyData.getOrDefault(dayOfWeek, 0f) + combined);
            weeklyCount.put(dayOfWeek, weeklyCount.getOrDefault(dayOfWeek, 0) + 1);
        }

        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            float avg = weeklyCount.containsKey(i) ?
                    weeklyData.get(i) / weeklyCount.get(i) : 0;
            barEntries.add(new BarEntry(i - 1, avg));
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Weekly Performance");
        dataSet.setGradientColor(Color.parseColor("#6366F1"), Color.parseColor("#8B5CF6"));
        dataSet.setValueTextColor(Color.parseColor("#374151"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);

        weeklyBarChart.setData(barData);
        weeklyBarChart.getDescription().setEnabled(false);
        weeklyBarChart.setDrawGridBackground(false);

        XAxis xAxis = weeklyBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#64748B"));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String[] days = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                int index = (int) value + 1;
                if (index >= 0 && index < days.length) {
                    return days[index];
                }
                return "";
            }
        });

        weeklyBarChart.getAxisLeft().setDrawGridLines(true);
        weeklyBarChart.getAxisLeft().setGridColor(Color.parseColor("#E5E7EB"));
        weeklyBarChart.getAxisLeft().setTextColor(Color.parseColor("#64748B"));
        weeklyBarChart.getAxisLeft().setAxisMinimum(0f);
        weeklyBarChart.getAxisLeft().setAxisMaximum(10f);
        weeklyBarChart.getAxisRight().setEnabled(false);
        weeklyBarChart.getLegend().setEnabled(false);
        weeklyBarChart.animateY(1000);
        weeklyBarChart.invalidate();
    }

    private void setupSleepDistributionChart(List<HabitEntry> entries) {
        int under6 = 0, between6and8 = 0, over8 = 0;

        for (HabitEntry entry : entries) {
            float sleep = entry.getSleepHours();
            if (sleep < 6) under6++;
            else if (sleep <= 8) between6and8++;
            else over8++;
        }

        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(under6, "< 6h"));
        pieEntries.add(new PieEntry(between6and8, "6-8h"));
        pieEntries.add(new PieEntry(over8, "> 8h"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "Sleep Distribution");
        dataSet.setColors(
                Color.parseColor("#EF4444"),
                Color.parseColor("#10B981"),
                Color.parseColor("#3B82F6")
        );
        dataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        dataSet.setValueTextSize(14f);
        dataSet.setSliceSpace(3f);

        PieData pieData = new PieData(dataSet);

        sleepPieChart.setData(pieData);
        sleepPieChart.getDescription().setEnabled(false);
        sleepPieChart.setDrawHoleEnabled(true);
        sleepPieChart.setHoleColor(Color.parseColor("#F3F4F6"));
        sleepPieChart.setHoleRadius(40f);
        sleepPieChart.setTransparentCircleRadius(45f);
        sleepPieChart.setCenterText("Sleep\nDistribution");
        sleepPieChart.setCenterTextSize(14f);
        sleepPieChart.setCenterTextColor(Color.parseColor("#374151"));

        Legend legend = sleepPieChart.getLegend();
        legend.setTextColor(Color.parseColor("#374151"));
        legend.setTextSize(12f);

        sleepPieChart.animateY(1000);
        sleepPieChart.invalidate();
    }

    private void setupWeeklyRadarChart(List<HabitEntry> entries) {
        Map<Integer, Float> weeklyScores = new HashMap<>();
        Map<Integer, Integer> weeklyCount = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        for (HabitEntry entry : entries) {
            cal.setTime(entry.getDate());
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            float score = (entry.getMoodScore() + entry.getEnergyLevel()) / 2f;
            weeklyScores.put(dayOfWeek, weeklyScores.getOrDefault(dayOfWeek, 0f) + score);
            weeklyCount.put(dayOfWeek, weeklyCount.getOrDefault(dayOfWeek, 0) + 1);
        }

        List<RadarEntry> radarEntries = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            float avg = weeklyCount.containsKey(i) ?
                    weeklyScores.get(i) / weeklyCount.get(i) : 0;
            radarEntries.add(new RadarEntry(avg));
        }

        RadarDataSet dataSet = new RadarDataSet(radarEntries, "Weekly Pattern");
        dataSet.setColor(Color.parseColor("#8B5CF6"));
        dataSet.setFillColor(Color.parseColor("#8B5CF6"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(80);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);

        RadarData radarData = new RadarData(dataSet);

        weeklyRadarChart.setData(radarData);
        weeklyRadarChart.getDescription().setEnabled(false);
        weeklyRadarChart.setWebLineWidth(1f);
        weeklyRadarChart.setWebColor(Color.parseColor("#E5E7EB"));
        weeklyRadarChart.setWebLineWidthInner(1f);
        weeklyRadarChart.setWebColorInner(Color.parseColor("#E5E7EB"));
        weeklyRadarChart.setWebAlpha(100);

        XAxis xAxis = weeklyRadarChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#64748B"));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                int index = (int) value;
                if (index >= 0 && index < days.length) {
                    return days[index];
                }
                return "";
            }
        });

        YAxis yAxis = weeklyRadarChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextColor(Color.parseColor("#64748B"));
        yAxis.setDrawLabels(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(10f);

        weeklyRadarChart.getLegend().setEnabled(false);
        weeklyRadarChart.animateXY(1000, 1000);
        weeklyRadarChart.invalidate();
    }

    private void generateInsight(List<HabitEntry> entries) {
        float avgMood = calculateAverage(entries, true);
        float avgEnergy = calculateAverage(entries, false);

        String insight = "";

        if (avgMood >= 7 && avgEnergy >= 7) {
            insight = "You're doing great! Keep up the excellent work!";
        } else if (avgMood < 5 || avgEnergy < 5) {
            insight = "Consider focusing on sleep and self-care activities.";
        } else {
            insight = "You're making steady progress. Stay consistent!";
        }

        float sleepMoodCorrelation = calculateSleepMoodCorrelation(entries);
        if (sleepMoodCorrelation > 0.5) {
            insight += " Better sleep seems to improve your mood!";
        }

        textViewInsight.setText(insight);
    }

    private float calculateSleepMoodCorrelation(List<HabitEntry> entries) {
        if (entries.size() < 3) return 0;

        float avgSleep = 0, avgMood = 0;
        for (HabitEntry entry : entries) {
            avgSleep += entry.getSleepHours();
            avgMood += entry.getMoodScore();
        }
        avgSleep /= entries.size();
        avgMood /= entries.size();

        float numerator = 0, denomSleep = 0, denomMood = 0;
        for (HabitEntry entry : entries) {
            float sleepDiff = entry.getSleepHours() - avgSleep;
            float moodDiff = entry.getMoodScore() - avgMood;
            numerator += sleepDiff * moodDiff;
            denomSleep += sleepDiff * sleepDiff;
            denomMood += moodDiff * moodDiff;
        }

        if (denomSleep == 0 || denomMood == 0) return 0;

        return (float) (numerator / Math.sqrt(denomSleep * denomMood));
    }

    private float calculateAverage(List<HabitEntry> entries, boolean isMood) {
        if (entries.isEmpty()) return 0;

        float sum = 0;
        for (HabitEntry entry : entries) {
            sum += isMood ? entry.getMoodScore() : entry.getEnergyLevel();
        }
        return sum / entries.size();
    }

    private int calculateStreak(List<HabitEntry> entries) {
        if (entries.isEmpty()) return 0;

        int streak = 1;
        for (int i = entries.size() - 1; i > 0; i--) {
            long diff = (entries.get(i).getDate().getTime() - entries.get(i - 1).getDate().getTime())
                    / (1000 * 60 * 60 * 24);
            if (diff == 1) streak++;
            else if (diff > 1) break;
        }
        return streak;
    }

    private int calculateLongestStreak(List<HabitEntry> entries) {
        if (entries.isEmpty()) return 0;

        int longestStreak = 1;
        int tempStreak = 1;

        for (int i = 1; i < entries.size(); i++) {
            long diff = (entries.get(i).getDate().getTime() - entries.get(i - 1).getDate().getTime())
                    / (1000 * 60 * 60 * 24);
            if (diff == 1) {
                tempStreak++;
            } else if (diff > 1) {
                longestStreak = Math.max(longestStreak, tempStreak);
                tempStreak = 1;
            }
        }

        return Math.max(longestStreak, tempStreak);
    }
}