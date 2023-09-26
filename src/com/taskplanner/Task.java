package com.taskplanner;

public class Task {
    private final int id;
    private final String title;
    private final String description;
    private final boolean status;

    public Task(int id, String title, String description, boolean status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
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

    public boolean isStatus() {
        return status;
    }
}
