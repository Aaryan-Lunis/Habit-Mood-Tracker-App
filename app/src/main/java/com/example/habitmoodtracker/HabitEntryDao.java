package com.example.habitmoodtracker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface HabitEntryDao {

    @Insert
    void insert(HabitEntry habitEntry);

    @Update
    void update(HabitEntry habitEntry);

    @Delete
    void delete(HabitEntry habitEntry);

    @Query("SELECT * FROM habit_entries ORDER BY date DESC")
    List<HabitEntry> getAllEntries();

    @Query("SELECT * FROM habit_entries WHERE date = :date LIMIT 1")
    HabitEntry getEntryByDate(String date);

    @Query("DELETE FROM habit_entries")
    void deleteAll();

    // ✅ NEW: Clear all local data
    @Query("DELETE FROM habit_entries")
    void clearAllData();
}
