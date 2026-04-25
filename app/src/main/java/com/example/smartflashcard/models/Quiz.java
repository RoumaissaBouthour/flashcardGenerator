package com.example.smartflashcard.models;

import java.util.List;

public class Quiz {
    private String title;
    private List<String> questions;

    public Quiz(String title, List<String> questions) {
        this.title = title;
        this.questions = questions;
    }

    public String getTitle() { return title; }
    public List<String> getQuestions() { return questions; }
}
