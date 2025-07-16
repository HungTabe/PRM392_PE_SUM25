package com.example.studytracker.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_sessions")
public class StudySession {
    @PrimaryKey
    @NonNull
    public String id;
    public String subject_date;
    public String subject_name;
    public int duration;
    public int level;
    public String notes;

    // Constructor
    public StudySession(@NonNull String id, String subject_date, String subject_name, int duration, int level, String notes) {
        this.id = id;
        this.subject_date = subject_date;
        this.subject_name = subject_name;
        this.duration = duration;
        this.level = level;
        this.notes = notes;
    }

    // Getters và setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSubject_date() {
        return subject_date;
    }

    public void setSubject_date(String subject_date) {
        this.subject_date = subject_date;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}