package com.example.smartflashcard.models;

public class Topic {
    private String id;
    private String title;
    private int flashcards;
    private int quizzes;
    private String lastUpdated;
    private boolean isFavorite;
    private int progress;

    public Topic(String id, String title, int flashcards, int quizzes,
                 String lastUpdated, boolean isFavorite, int progress) {
        this.id = id;
        this.title = title;
        this.flashcards = flashcards;
        this.quizzes = quizzes;
        this.lastUpdated = lastUpdated;
        this.isFavorite = isFavorite;
        this.progress = progress;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getFlashcards() { return flashcards; }
    public void setFlashcards(int flashcards) { this.flashcards = flashcards; }

    public int getQuizzes() { return quizzes; }
    public void setQuizzes(int quizzes) { this.quizzes = quizzes; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}