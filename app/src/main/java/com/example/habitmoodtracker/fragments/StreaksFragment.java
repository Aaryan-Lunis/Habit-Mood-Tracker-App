package com.example.habitmoodtracker.fragments;

import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.habitmoodtracker.MainActivity;
import com.example.habitmoodtracker.R;
import com.example.habitmoodtracker.HabitEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class StreaksFragment extends Fragment {

    private TextView textViewCurrentStreak, textViewStreakQuote, textViewStats, textViewStatsTitle;
    private TextView textViewWeekStreak, textViewMonthStreak, textViewBestDay;
    private LinearLayout streakChain, achievementRow;
    private GridLayout heatmapCalendar;
    private CardView cardStreakStats, cardWeeklyProgress, cardInsights;
    private ProgressBar progressWeekly, progressMonthly;
    private TextView textViewWeeklyProgress, textViewMonthlyProgress;
    private Handler handler;
    private Runnable updateRunnable;

    private final String[] motivationalQuotes = {
            "Consistency is the key to success!",
            "Every day is a new opportunity!",
            "Your dedication is inspiring!",
            "Keep pushing forward!",
            "Small steps lead to big changes!",
            "You're building greatness daily!",
            "Progress over perfection!",
            "Your future self will thank you!"
    };

    public StreaksFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_streaks, container, false);

        initViews(view);
        setupRealtimeUpdates();
        updateStreakInfo();

        return view;
    }

    private void initViews(View view) {
        textViewCurrentStreak = view.findViewById(R.id.textViewCurrentStreak);
        textViewStreakQuote = view.findViewById(R.id.textViewStreakQuote);
        streakChain = view.findViewById(R.id.streakChain);
        achievementRow = view.findViewById(R.id.achievementRow);
        heatmapCalendar = view.findViewById(R.id.heatmapCalendar);
        textViewStats = view.findViewById(R.id.textViewStats);
        textViewStatsTitle = view.findViewById(R.id.textViewStatsTitle);
        cardStreakStats = view.findViewById(R.id.cardStreakStats);

        textViewWeekStreak = view.findViewById(R.id.textViewWeekStreak);
        textViewMonthStreak = view.findViewById(R.id.textViewMonthStreak);
        textViewBestDay = view.findViewById(R.id.textViewBestDay);
        cardWeeklyProgress = view.findViewById(R.id.cardWeeklyProgress);
        cardInsights = view.findViewById(R.id.cardInsights);
        progressWeekly = view.findViewById(R.id.progressWeekly);
        progressMonthly = view.findViewById(R.id.progressMonthly);
        textViewWeeklyProgress = view.findViewById(R.id.textViewWeeklyProgress);
        textViewMonthlyProgress = view.findViewById(R.id.textViewMonthlyProgress);
    }

    private void setupRealtimeUpdates() {
        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateStreakInfo();
                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStreakInfo();
        handler.postDelayed(updateRunnable, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable);
    }

    private void updateStreakInfo() {
        // Get current user ID
        com.example.habitmoodtracker.AuthManager authManager =
                com.example.habitmoodtracker.AuthManager.getInstance(requireContext());
        String userId = authManager.getCurrentUserId();

        if (userId == null) {
            setEmptyState();
            return;
        }

        // Fetch data from Firebase for this user only
        com.example.habitmoodtracker.FirebaseHelper.getEntriesOnce(userId,
                new com.example.habitmoodtracker.FirebaseHelper.OnDataLoadListener() {
                    @Override
                    public void onDataLoaded(List<HabitEntry> entries) {
                        if (getActivity() == null) return;

                        getActivity().runOnUiThread(() -> {
                            if (entries == null || entries.isEmpty()) {
                                setEmptyState();
                                return;
                            }

                            Collections.sort(entries, (a, b) -> a.getDate().compareTo(b.getDate()));

                            int currentStreak = calculateCurrentStreak(entries);
                            int longestStreak = calculateLongestStreak(entries);

                            animateStreakUpdate(currentStreak);
                            setupStreakChain(currentStreak);
                            setupEnhancedHeatmap(entries);
                            setupAchievementBadges(currentStreak, longestStreak);
                            setupProgressCards(entries);
                            setupInsightsCard(entries);
                            updateStatsCard(currentStreak, longestStreak);
                            setRandomMotivationalQuote();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() == null) return;

                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error loading data: " + error,
                                    Toast.LENGTH_SHORT).show();
                            setEmptyState();
                        });
                    }
                });
    }

    private void setEmptyState() {
        textViewCurrentStreak.setText("Start your journey today!");
        setupStreakChain(0);
        setupEnhancedHeatmap(null);
        setupAchievementBadges(0, 0);
        setRandomMotivationalQuote();
    }

    private void animateStreakUpdate(int streak) {
        String streakText = streak + " Day" + (streak != 1 ? "s" : "") + " Streak!";
        textViewCurrentStreak.setText(streakText);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textViewCurrentStreak, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textViewCurrentStreak, "scaleY", 1.0f, 1.1f, 1.0f);
        scaleX.setDuration(500);
        scaleY.setDuration(500);
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());
        scaleX.start();
        scaleY.start();
    }

    private void setupStreakChain(int streak) {
        streakChain.removeAllViews();
        int maxDays = 7;

        for (int i = 0; i < maxDays; i++) {
            View circle = new View(getContext());
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                    getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMarginEnd(8);
            circle.setLayoutParams(params);

            if (i < streak) {
                circle.setBackgroundResource(R.drawable.streak_circle_filled);
                circle.setAlpha(0f);
                circle.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .setStartDelay(i * 50)
                        .start();
            } else {
                circle.setBackgroundResource(R.drawable.streak_circle_empty);
            }

            streakChain.addView(circle);
        }
    }

    private void setupEnhancedHeatmap(List<HabitEntry> entries) {
        heatmapCalendar.removeAllViews();

        Map<String, HabitEntry> entryMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        if (entries != null) {
            for (HabitEntry entry : entries) {
                entryMap.put(sdf.format(entry.getDate()), entry);
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -41); // Show last 6 weeks

        for (int i = 0; i < 42; i++) {
            View cell = new View(getContext());
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                    getResources().getDisplayMetrics());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = size;
            params.height = size;
            params.setMargins(3, 3, 3, 3);
            cell.setLayoutParams(params);

            String dateStr = sdf.format(cal.getTime());
            HabitEntry entry = entryMap.get(dateStr);

            int color;
            if (entry != null) {
                int combinedScore = (entry.getMoodScore() + entry.getEnergyLevel()) / 2;
                if (combinedScore >= 8) {
                    color = Color.parseColor("#10B981"); // Green
                } else if (combinedScore >= 6) {
                    color = Color.parseColor("#34D399"); // Light green
                } else if (combinedScore >= 4) {
                    color = Color.parseColor("#FBBF24"); // Yellow
                } else {
                    color = Color.parseColor("#F87171"); // Red
                }
            } else {
                color = Color.parseColor("#E5E7EB"); // Gray
            }

            cell.setBackgroundColor(color);
            cell.setElevation(2f);
            heatmapCalendar.addView(cell);

            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void setupAchievementBadges(int currentStreak, int longestStreak) {
        achievementRow.removeAllViews();

        int[] milestones = {7, 14, 30, 50, 100};
        String[] titles = {"Week\nWarrior", "2-Week\nChamp", "Monthly\nMaster", "50-Day\nLegend", "Century\nKing"};

        int badgeSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64,
                getResources().getDisplayMetrics());

        for (int i = 0; i < milestones.length; i++) {
            LinearLayout badgeLayout = new LinearLayout(getContext());
            badgeLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    badgeSize + 20, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8, 0, 8, 0);
            badgeLayout.setLayoutParams(layoutParams);
            badgeLayout.setGravity(android.view.Gravity.CENTER);

            ImageView badge = new ImageView(getContext());
            LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(badgeSize, badgeSize);
            badge.setLayoutParams(badgeParams);
            badge.setImageResource(R.drawable.ic_medal);

            boolean achieved = longestStreak >= milestones[i];
            badge.setAlpha(achieved ? 1f : 0.3f);

            if (achieved) {
                badge.setColorFilter(Color.parseColor("#F59E0B"));
            }

            TextView badgeText = new TextView(getContext());
            badgeText.setText(titles[i]);
            badgeText.setTextSize(10);
            badgeText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            badgeText.setTextColor(achieved ? Color.parseColor("#374151") : Color.parseColor("#9CA3AF"));

            badgeLayout.addView(badge);
            badgeLayout.addView(badgeText);
            achievementRow.addView(badgeLayout);
        }
    }

    private void setupProgressCards(List<HabitEntry> entries) {
        Calendar cal = Calendar.getInstance();
        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int currentMonth = cal.get(Calendar.MONTH);

        int weekEntries = 0;
        int monthEntries = 0;

        for (HabitEntry entry : entries) {
            cal.setTime(entry.getDate());
            if (cal.get(Calendar.WEEK_OF_YEAR) == currentWeek) weekEntries++;
            if (cal.get(Calendar.MONTH) == currentMonth) monthEntries++;
        }

        int weekProgress = (weekEntries * 100) / 7;
        int monthProgress = (monthEntries * 100) / 30;

        progressWeekly.setMax(100);
        progressWeekly.setProgress(weekProgress);
        textViewWeeklyProgress.setText(weekEntries + "/7 days this week");

        progressMonthly.setMax(100);
        progressMonthly.setProgress(monthProgress);
        textViewMonthlyProgress.setText(monthEntries + "/30 days this month");
    }

    private void setupInsightsCard(List<HabitEntry> entries) {
        Map<Integer, Integer> dayCount = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        for (HabitEntry entry : entries) {
            cal.setTime(entry.getDate());
            int day = cal.get(Calendar.DAY_OF_WEEK);
            dayCount.put(day, dayCount.getOrDefault(day, 0) + 1);
        }

        int maxDay = Calendar.SUNDAY;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : dayCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxDay = entry.getKey();
            }
        }

        String[] dayNames = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        textViewBestDay.setText("Most active: " + dayNames[maxDay] + " (" + maxCount + " entries)");

        int weekStreak = calculateWeekStreak(entries);
        int monthStreak = calculateMonthStreak(entries);

        textViewWeekStreak.setText(weekStreak + " weeks in a row");
        textViewMonthStreak.setText(monthStreak + " months logged");
    }

    private int calculateWeekStreak(List<HabitEntry> entries) {
        Calendar cal = Calendar.getInstance();
        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int streak = 0;

        for (int i = 0; i < 52; i++) {
            int targetWeek = currentWeek - i;
            boolean hasEntry = false;

            for (HabitEntry entry : entries) {
                cal.setTime(entry.getDate());
                if (cal.get(Calendar.WEEK_OF_YEAR) == targetWeek) {
                    hasEntry = true;
                    break;
                }
            }

            if (hasEntry) streak++;
            else break;
        }

        return streak;
    }

    private int calculateMonthStreak(List<HabitEntry> entries) {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);
        int streak = 0;

        for (int i = 0; i < 24; i++) {
            int targetMonth = (currentMonth - i + 12) % 12;
            int targetYear = currentYear - (currentMonth - i < 0 ? 1 : 0);
            boolean hasEntry = false;

            for (HabitEntry entry : entries) {
                cal.setTime(entry.getDate());
                if (cal.get(Calendar.MONTH) == targetMonth && cal.get(Calendar.YEAR) == targetYear) {
                    hasEntry = true;
                    break;
                }
            }

            if (hasEntry) streak++;
            else break;
        }

        return streak;
    }

    private void updateStatsCard(int currentStreak, int longestStreak) {
        textViewStats.setText("Current: " + currentStreak + " | Best: " + longestStreak + " days");
        textViewStatsTitle.setText("Streak Statistics");
    }

    private void setRandomMotivationalQuote() {
        Random random = new Random();
        int idx = random.nextInt(motivationalQuotes.length);
        textViewStreakQuote.setText(motivationalQuotes[idx]);
    }

    private int calculateCurrentStreak(List<HabitEntry> entries) {
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