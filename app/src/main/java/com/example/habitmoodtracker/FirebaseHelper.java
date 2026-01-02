package com.example.habitmoodtracker;

import android.util.Log;
import com.example.habitmoodtracker.HabitEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";
    private static DatabaseReference databaseReference;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    // Get user-specific database reference
    public static DatabaseReference getUserDatabase(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference("users").child(userId).child("habit_entries");
    }

    // Save entry for specific user
    public static void saveEntry(String userId, HabitEntry entry, OnCompleteListener listener) {
        if (userId == null || userId.isEmpty()) {
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        String dateKey = dateFormat.format(entry.getDate());

        getUserDatabase(userId).child(dateKey).setValue(entry)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Entry saved successfully for user: " + userId);
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save entry", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // Get all entries for specific user
    public static void getAllEntries(String userId, OnDataLoadListener listener) {
        if (userId == null || userId.isEmpty()) {
            if (listener != null) listener.onError("User not logged in");
            return;
        }

        getUserDatabase(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HabitEntry> entries = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HabitEntry entry = dataSnapshot.getValue(HabitEntry.class);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
                Log.d(TAG, "Loaded " + entries.size() + " entries for user: " + userId);
                if (listener != null) listener.onDataLoaded(entries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load entries", error.toException());
                if (listener != null) listener.onError(error.getMessage());
            }
        });
    }

    // Get entries once (no real-time updates)
    public static void getEntriesOnce(String userId, OnDataLoadListener listener) {
        if (userId == null || userId.isEmpty()) {
            if (listener != null) listener.onError("User not logged in");
            return;
        }

        getUserDatabase(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HabitEntry> entries = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HabitEntry entry = dataSnapshot.getValue(HabitEntry.class);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
                Log.d(TAG, "Loaded " + entries.size() + " entries for user: " + userId);
                if (listener != null) listener.onDataLoaded(entries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load entries", error.toException());
                if (listener != null) listener.onError(error.getMessage());
            }
        });
    }

    // Delete specific entry
    public static void deleteEntry(String userId, String dateKey, OnCompleteListener listener) {
        if (userId == null || userId.isEmpty()) {
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        getUserDatabase(userId).child(dateKey).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Entry deleted successfully");
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete entry", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // Clear all user data
    public static void clearAllUserData(String userId, OnCompleteListener listener) {
        if (userId == null || userId.isEmpty()) {
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        getUserDatabase(userId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "All user data cleared successfully");
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to clear user data", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnDataLoadListener {
        void onDataLoaded(List<HabitEntry> entries);
        void onError(String error);
    }
}