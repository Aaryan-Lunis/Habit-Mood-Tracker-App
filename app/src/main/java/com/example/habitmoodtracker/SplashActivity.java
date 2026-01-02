package com.example.habitmoodtracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // ✅ CRITICAL: Sign out user on app restart to disable auto-login
        AuthManager authManager = AuthManager.getInstance(this);
        authManager.signOut();
        Log.d(TAG, "User signed out on app start - Auto-login disabled");

        ImageView logo = findViewById(R.id.splashLogo);
        TextView appName = findViewById(R.id.splashAppName);
        TextView tagline = findViewById(R.id.splashTagline);

        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);

        logo.startAnimation(fadeIn);
        appName.startAnimation(slideUp);
        tagline.startAnimation(fadeIn);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToLogin();
        }, SPLASH_DURATION);
    }

    private void navigateToLogin() {
        // ✅ Always navigate to Login (no auto-login)
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        Log.d(TAG, "Navigating to LoginActivity");

        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        // Disable back button on splash screen
    }
}