package com.example.habitmoodtracker.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.habitmoodtracker.MainActivity;
import com.example.habitmoodtracker.R;
import com.example.habitmoodtracker.HabitEntry;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogFragment extends Fragment {

    private TextInputEditText editTextDate, editTextSleep, editTextWater;
    private Slider sliderMood, sliderEnergy;
    private MaterialCheckBox checkBoxExercise, checkBoxMeditation, checkBoxSocial;
    private ExtendedFloatingActionButton buttonSave;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

    public LogFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);

        initViews(view);
        setupListeners();
        setCurrentDate();

        return view;
    }

    private void initViews(View view) {
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextSleep = view.findViewById(R.id.editTextSleep);
        editTextWater = view.findViewById(R.id.editTextWater);
        sliderMood = view.findViewById(R.id.sliderMood);
        sliderEnergy = view.findViewById(R.id.sliderEnergy);
        checkBoxExercise = view.findViewById(R.id.checkBoxExercise);
        checkBoxMeditation = view.findViewById(R.id.checkBoxMeditation);
        checkBoxSocial = view.findViewById(R.id.checkBoxSocial);
        buttonSave = view.findViewById(R.id.buttonSave);
    }

    private void setupListeners() {
        buttonSave.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
                        saveEntry();
                    })
                    .start();
        });

        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextDate.setFocusable(false);
        editTextDate.setClickable(true);

        sliderMood.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) showToastFeedback("Mood", (int) value);
        });

        sliderEnergy.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) showToastFeedback("Energy", (int) value);
        });
    }

    private void setCurrentDate() {
        String currentDate = dateFormat.format(new Date());
        editTextDate.setText(currentDate);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String selectedDate = dateFormat.format(calendar.getTime());
                    editTextDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showToastFeedback(String type, int value) {
        String message = "";
        if (type.equals("Mood")) {
            if (value <= 3) message = "😔 Take care of yourself";
            else if (value <= 6) message = "🙂 You're doing okay";
            else message = "😊 Great mood!";
        } else if (type.equals("Energy")) {
            if (value <= 3) message = "😴 Rest well tonight";
            else if (value <= 6) message = "⚡ Moderate energy";
            else message = "🚀 High energy!";
        }
        if (!message.isEmpty()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEntry() {
        if (!validateInputs()) return;

        try {
            String dateStr = editTextDate.getText().toString().trim();
            Date date = dateFormat.parse(dateStr);

            int moodScore = (int) sliderMood.getValue();
            int energyLevel = (int) sliderEnergy.getValue();
            float sleepHours = parseSleepHours();
            float waterIntake = parseWaterIntake();
            boolean exercise = checkBoxExercise.isChecked();
            boolean meditation = checkBoxMeditation.isChecked();
            boolean socialTime = checkBoxSocial.isChecked();

            HabitEntry habitEntry = new HabitEntry(date, moodScore, energyLevel, sleepHours,
                    exercise, waterIntake, meditation, socialTime);

            // Get current user ID
            com.example.habitmoodtracker.AuthManager authManager =
                    com.example.habitmoodtracker.AuthManager.getInstance(requireContext());
            String userId = authManager.getCurrentUserId();

            if (userId == null) {
                Toast.makeText(getContext(), "❌ Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to Firebase with user ID
            com.example.habitmoodtracker.FirebaseHelper.saveEntry(userId, habitEntry,
                    new com.example.habitmoodtracker.FirebaseHelper.OnCompleteListener() {
                        @Override
                        public void onSuccess() {
                            // Also save to Room for offline backup
                            new Thread(() -> {
                                MainActivity.database.habitEntryDao().insert(habitEntry);
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "✅ Entry saved successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    clearForm();
                                });
                            }).start();
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(getContext(), "❌ Error: " + error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(getContext(), "❌ Error saving entry", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        String dateStr = editTextDate.getText() != null ? editTextDate.getText().toString().trim() : "";

        if (TextUtils.isEmpty(dateStr)) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Date date = dateFormat.parse(dateStr);
            if (date == null) {
                Toast.makeText(getContext(), "Invalid date", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
            return false;
        }

        String sleepStr = editTextSleep.getText() != null ? editTextSleep.getText().toString().trim() : "";
        if (!sleepStr.isEmpty()) {
            try {
                float sleep = Float.parseFloat(sleepStr);
                if (sleep < 0 || sleep > 24) {
                    Toast.makeText(getContext(), "Sleep hours: 0-24", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid sleep hours", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        String waterStr = editTextWater.getText() != null ? editTextWater.getText().toString().trim() : "";
        if (!waterStr.isEmpty()) {
            try {
                float water = Float.parseFloat(waterStr);
                if (water < 0 || water > 20) {
                    Toast.makeText(getContext(), "Water intake: 0-20 liters", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid water amount", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private float parseSleepHours() {
        String sleepStr = editTextSleep.getText() != null ? editTextSleep.getText().toString().trim() : "";
        return sleepStr.isEmpty() ? 0f : Float.parseFloat(sleepStr);
    }

    private float parseWaterIntake() {
        String waterStr = editTextWater.getText() != null ? editTextWater.getText().toString().trim() : "";
        return waterStr.isEmpty() ? 0f : Float.parseFloat(waterStr);
    }

    private void clearForm() {
        setCurrentDate();
        editTextSleep.setText("");
        editTextWater.setText("");
        sliderMood.setValue(5);
        sliderEnergy.setValue(5);
        checkBoxExercise.setChecked(false);
        checkBoxMeditation.setChecked(false);
        checkBoxSocial.setChecked(false);
    }

}
