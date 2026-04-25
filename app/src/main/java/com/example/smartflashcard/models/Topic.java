package com.example.smartflashcard.models;

public class Topic {
    private String id;
    private String title;

    public Topic(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
}
