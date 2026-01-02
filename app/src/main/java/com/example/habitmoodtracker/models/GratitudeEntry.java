package com.example.habitmoodtracker.models;

import java.util.Date;

public class GratitudeEntry {
    private String id;
    private String text;
    private long timestamp;
    private Date date;

    // Required empty constructor for Firebase
    public GratitudeEntry() {
        this.timestamp = System.currentTimeMillis();
        this.date = new Date(timestamp);
    }

    public GratitudeEntry(String text) {
        this.text = text;
        this.timestamp = System.currentTimeMillis();
        this.date = new Date(timestamp);
        this.id = String.valueOf(timestamp);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        this.date = new Date(timestamp);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}