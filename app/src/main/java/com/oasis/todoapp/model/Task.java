package com.oasis.todoapp.model;

public class Task {
    private int id;
    private int userId;
    private String title;
    private String notes;
    private boolean isCompleted;
    private String createdAt;

    public Task(int id, int userId, String title, String notes, boolean isCompleted, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.notes = notes;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getNotes() { return notes; }
    public boolean isCompleted() { return isCompleted; }
    public String getCreatedAt() { return createdAt; }

    public void setCompleted(boolean completed) { isCompleted = completed; }
}
