package com.example.habitmoodtracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.habitmoodtracker.AuthManager;
import com.example.habitmoodtracker.LoginActivity;
import com.example.habitmoodtracker.R;
import com.example.habitmoodtracker.ThemeManager;
import com.google.android.material.card.MaterialCardView;

public class SettingsFragment extends Fragment {

    private TextView textViewUserEmail, textViewUserId;
    private RadioGroup radioGroupTheme, radioGroupColor;
    private Button buttonLogout, buttonThemeDialog;
    private MaterialCardView cardTheme, cardColors;
    private ThemeManager themeManager;
    private AuthManager authManager;

    public SettingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        themeManager = ThemeManager.getInstance(requireContext());
        authManager = AuthManager.getInstance(requireContext());

        initViews(view);
        loadUserInfo();
        loadCurrentTheme();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        textViewUserEmail = view.findViewById(R.id.textViewUserEmail);
        textViewUserId = view.findViewById(R.id.textViewUserId);
        radioGroupTheme = view.findViewById(R.id.radioGroupTheme);
        radioGroupColor = view.findViewById(R.id.radioGroupColor);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonThemeDialog = view.findViewById(R.id.buttonThemeDialog);
        cardTheme = view.findViewById(R.id.cardTheme);
        cardColors = view.findViewById(R.id.cardColors);
    }

    private void loadUserInfo() {
        String email = authManager.getCurrentUserEmail();
        String userId = authManager.getCurrentUserId();

        textViewUserEmail.setText(email != null ? email : "Not available");
        textViewUserId.setText(userId != null ? userId : "Not available");
    }

    private void loadCurrentTheme() {
        // Load theme mode
        int themeMode = themeManager.getThemeMode();
        switch (themeMode) {
            case ThemeManager.MODE_LIGHT:
                radioGroupTheme.check(R.id.radioLight);
                break;
            case ThemeManager.MODE_DARK:
                radioGroupTheme.check(R.id.radioDark);
                break;
            case ThemeManager.MODE_SYSTEM:
            default:
                radioGroupTheme.check(R.id.radioSystem);
                break;
        }

        // Load theme color
        String color = themeManager.getThemeColor();
        switch (color) {
            case ThemeManager.COLOR_BLUE:
                radioGroupColor.check(R.id.radioBlue);
                break;
            case ThemeManager.COLOR_GREEN:
                radioGroupColor.check(R.id.radioGreen);
                break;
            case ThemeManager.COLOR_PINK:
                radioGroupColor.check(R.id.radioPink);
                break;
            case ThemeManager.COLOR_PURPLE:
            default:
                radioGroupColor.check(R.id.radioPurple);
                break;
        }
    }

    private void setupListeners() {
        // Theme mode change
        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int mode = ThemeManager.MODE_SYSTEM;
            if (checkedId == R.id.radioLight) {
                mode = ThemeManager.MODE_LIGHT;
            } else if (checkedId == R.id.radioDark) {
                mode = ThemeManager.MODE_DARK;
            } else if (checkedId == R.id.radioSystem) {
                mode = ThemeManager.MODE_SYSTEM;
            }
            themeManager.setThemeMode(mode);
            Toast.makeText(getContext(), "Theme updated! 🎨", Toast.LENGTH_SHORT).show();
        });

        // Theme color change
        radioGroupColor.setOnCheckedChangeListener((group, checkedId) -> {
            String color = ThemeManager.COLOR_PURPLE;
            if (checkedId == R.id.radioBlue) {
                color = ThemeManager.COLOR_BLUE;
            } else if (checkedId == R.id.radioGreen) {
                color = ThemeManager.COLOR_GREEN;
            } else if (checkedId == R.id.radioPink) {
                color = ThemeManager.COLOR_PINK;
            } else if (checkedId == R.id.radioPurple) {
                color = ThemeManager.COLOR_PURPLE;
            }
            themeManager.setThemeColor(color);
            Toast.makeText(getContext(), "Color theme saved! Restart app to see changes.", Toast.LENGTH_LONG).show();
        });

        // Theme dialog button
        buttonThemeDialog.setOnClickListener(v -> showThemeDialog());

        // Logout button
        buttonLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showThemeDialog() {
        String[] themes = {"Light Mode", "Dark Mode", "System Default"};
        int currentMode = themeManager.getThemeMode();

        new AlertDialog.Builder(requireContext())
                .setTitle("Choose Theme")
                .setSingleChoiceItems(themes, currentMode, (dialog, which) -> {
                    themeManager.setThemeMode(which);
                    loadCurrentTheme();
                    Toast.makeText(getContext(), "Theme updated! 🎨", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authManager.signOut();
                    Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}