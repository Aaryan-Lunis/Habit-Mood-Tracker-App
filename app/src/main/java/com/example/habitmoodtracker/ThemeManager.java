package com.example.habitmoodtracker;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_THEME_COLOR = "theme_color";

    public static final int MODE_LIGHT = 0;
    public static final int MODE_DARK = 1;
    public static final int MODE_SYSTEM = 2;

    public static final String COLOR_BLUE = "blue";
    public static final String COLOR_GREEN = "green";
    public static final String COLOR_PURPLE = "purple";
    public static final String COLOR_PINK = "pink";

    private static ThemeManager instance;
    private SharedPreferences prefs;

    private ThemeManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context);
        }
        return instance;
    }

    public void setThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
        applyThemeMode(mode);
    }

    public int getThemeMode() {
        return prefs.getInt(KEY_THEME_MODE, MODE_SYSTEM);
    }

    public void setThemeColor(String color) {
        prefs.edit().putString(KEY_THEME_COLOR, color).apply();
    }

    public String getThemeColor() {
        return prefs.getString(KEY_THEME_COLOR, COLOR_PURPLE);
    }

    public void applyTheme() {
        int mode = getThemeMode();
        applyThemeMode(mode);
    }

    private void applyThemeMode(int mode) {
        switch (mode) {
            case MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case MODE_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public int getPrimaryColor() {
        String color = getThemeColor();
        switch (color) {
            case COLOR_BLUE:
                return android.graphics.Color.parseColor("#3B82F6");
            case COLOR_GREEN:
                return android.graphics.Color.parseColor("#10B981");
            case COLOR_PINK:
                return android.graphics.Color.parseColor("#EC4899");
            case COLOR_PURPLE:
            default:
                return android.graphics.Color.parseColor("#8B5CF6");
        }
    }
}