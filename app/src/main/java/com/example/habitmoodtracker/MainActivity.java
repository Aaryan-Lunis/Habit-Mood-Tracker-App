package com.example.habitmoodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import com.example.habitmoodtracker.api.ApiService;
import com.example.habitmoodtracker.api.QuoteResponse;
import com.example.habitmoodtracker.api.RetrofitClient;
import com.example.habitmoodtracker.fragments.ChatbotFragment;
import com.example.habitmoodtracker.fragments.DashboardFragment;
import com.example.habitmoodtracker.fragments.GratitudeFragment;
import com.example.habitmoodtracker.fragments.LogFragment;
import com.example.habitmoodtracker.fragments.SettingsFragment;
import com.example.habitmoodtracker.fragments.StreaksFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static AppDatabase database;
    private static final String TAG = "MainActivity";
    private AuthManager authManager;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate()
        themeManager = ThemeManager.getInstance(this);
        themeManager.applyTheme();

        super.onCreate(savedInstanceState);

        // Check authentication
        authManager = AuthManager.getInstance(this);
        if (!authManager.isUserLoggedIn()) {
            navigateToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialize Room Database (for offline backup)
        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "habit_mood_db")
                .fallbackToDestructiveMigration()
                .build();

        // ✅ CRITICAL FIX: Clear local database to prevent data leakage between users
        clearLocalDatabaseForNewUser();

        // Fetch motivational quote from API
        fetchMotivationalQuote();

        setupBottomNavigation();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LogFragment())
                    .commit();
        }
    }

    // ✅ NEW METHOD: Clear local Room database
    private void clearLocalDatabaseForNewUser() {
        new Thread(() -> {
            try {
                database.habitEntryDao().deleteAll();
                Log.d(TAG, "✅ Local database cleared for new user session");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing database: " + e.getMessage());
            }
        }).start();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_log) {
                    selectedFragment = new LogFragment();
                } else if (id == R.id.nav_dashboard) {
                    selectedFragment = new DashboardFragment();
                } else if (id == R.id.nav_streaks) {
                    selectedFragment = new StreaksFragment();
                } else if (id == R.id.nav_chatbot) {
                    selectedFragment = new ChatbotFragment();
                } else if (id == R.id.nav_gratitude) {
                    selectedFragment = new GratitudeFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out)
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // ✅ Navigate to Settings Fragment
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .commit();
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        } else if (id == R.id.action_profile) {
            showProfileDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authManager.signOut();
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showProfileDialog() {
        String email = authManager.getCurrentUserEmail();
        String userId = authManager.getCurrentUserId();

        new AlertDialog.Builder(this)
                .setTitle("👤 Profile")
                .setMessage("Email: " + email + "\n\nUser ID: " + userId)
                .setPositiveButton("OK", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void fetchMotivationalQuote() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<QuoteResponse> call = apiService.getRandomQuote();

        call.enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QuoteResponse quote = response.body();
                    Log.d(TAG, "Quote: " + quote.getQuote());
                    Log.d(TAG, "Author: " + quote.getAuthor());
                }
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Do you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                    finishAffinity();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
