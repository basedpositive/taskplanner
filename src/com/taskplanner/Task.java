package com.taskplanner;

import java.sql.Timestamp;

public class Task {
    private final int id;
    private final String title;
    private final String description;
    private final String status;
    private final Timestamp createdAt;
    private final Timestamp dueDate;

    public Task(int id, String title, String description, String status, Timestamp createdAt, Timestamp dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String isStatus() {
        return status;
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getDueDate() { return dueDate; }
}
