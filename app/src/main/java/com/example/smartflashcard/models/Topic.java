package com.example.smartflashcard.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "topics")
public class Topic {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private int flashcards;
    private int quizzes;
    private String lastUpdated;
    private boolean isFavorite;
    private int progress;
    private String imagePath; // Path to the attached image/file

    public Topic(@NonNull String id, String title, int flashcards, int quizzes,
                 String lastUpdated, boolean isFavorite, int progress, String imagePath) {
        this.id = id;
        this.title = title;
        this.flashcards = flashcards;
        this.quizzes = quizzes;
        this.lastUpdated = lastUpdated;
        this.isFavorite = isFavorite;
        this.progress = progress;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

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

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}