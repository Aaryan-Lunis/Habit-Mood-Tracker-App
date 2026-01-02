package com.example.habitmoodtracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import java.util.Date;

@Entity(tableName = "habit_entries")
public class HabitEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private Date date;

    private int moodScore;
    private int energyLevel;
    private float sleepHours;
    private boolean exercise;
    private float waterIntake;
    private boolean meditation;
    private boolean socialTime;

    // ✅ NO-ARGUMENT CONSTRUCTOR (Required by Firebase)
    public HabitEntry() {
        // Default constructor for Firebase
        this.date = new Date(); // Initialize with current date to avoid null
    }

    // Parameterized Constructor
    public HabitEntry(@NonNull Date date, int moodScore, int energyLevel, float sleepHours,
                      boolean exercise, float waterIntake, boolean meditation, boolean socialTime) {
        this.date = date;
        this.moodScore = moodScore;
        this.energyLevel = energyLevel;
        this.sleepHours = sleepHours;
        this.exercise = exercise;
        this.waterIntake = waterIntake;
        this.meditation = meditation;
        this.socialTime = socialTime;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public int getMoodScore() {
        return moodScore;
    }

    public void setMoodScore(int moodScore) {
        this.moodScore = moodScore;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(int energyLevel) {
        this.energyLevel = energyLevel;
    }

    public float getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(float sleepHours) {
        this.sleepHours = sleepHours;
    }

    public boolean isExercise() {
        return exercise;
    }

    public void setExercise(boolean exercise) {
        this.exercise = exercise;
    }

    public float getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(float waterIntake) {
        this.waterIntake = waterIntake;
    }

    public boolean isMeditation() {
        return meditation;
    }

    public void setMeditation(boolean meditation) {
        this.meditation = meditation;
    }

    public boolean isSocialTime() {
        return socialTime;
    }

    public void setSocialTime(boolean socialTime) {
        this.socialTime = socialTime;
    }
}