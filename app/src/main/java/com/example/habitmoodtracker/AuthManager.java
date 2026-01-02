package com.example.habitmoodtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {

    private static final String TAG = "AuthManager";
    private static final String PREFS_NAME = "HabitMoodPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    // ✅ REMOVED: KEY_IS_LOGGED_IN to disable auto-login

    private static AuthManager instance;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    private AuthManager(Context context) {
        mAuth = FirebaseAuth.getInstance();
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public boolean isUserLoggedIn() {
        // ✅ CHANGED: Only check Firebase (no SharedPreferences auto-login)
        FirebaseUser user = mAuth.getCurrentUser();

        Log.d(TAG, "Firebase user: " + (user != null ? user.getUid() : "null"));

        return user != null;
    }

    public String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getCurrentUserEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.getEmail();
        }
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public void saveUserSession(String userId, String email) {
        Log.d(TAG, "Saving user session - UserID: " + userId + ", Email: " + email);

        prefs.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_EMAIL, email)
                // ✅ REMOVED: .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();

        Log.d(TAG, "✅ User session saved successfully");
    }

    public void clearUserSession() {
        Log.d(TAG, "Clearing user session");

        prefs.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_USER_EMAIL)
                // ✅ REMOVED: .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();

        Log.d(TAG, "✅ User session cleared");
    }

    public void signOut() {
        Log.d(TAG, "Signing out user");
        mAuth.signOut();
        clearUserSession();
        Log.d(TAG, "✅ User signed out successfully");
    }

    public interface AuthCallback {
        void onSuccess(String userId);
        void onFailure(String error);
    }
}