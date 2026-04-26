package com.example.smartflashcard.models;

public class Quiz {
    private String id;
    private String title;
    private String question;
    private String[] options;
    private int correctAnswer;
    private int questions;
    private boolean completed;
    private int score;

    public Quiz(String id, String title, int questions, boolean completed, int score) {
        this.id = id;
        this.title = title;
        this.questions = questions;
        this.completed = completed;
        this.score = score;
    }

    public Quiz(String id, String question, String[] options, int correctAnswer) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getQuestion() { return question; }
    public String[] getOptions() { return options; }
    public int getCorrectAnswer() { return correctAnswer; }
    public int getQuestions() { return questions; }
    public boolean isCompleted() { return completed; }
    public int getScore() { return score; }
}