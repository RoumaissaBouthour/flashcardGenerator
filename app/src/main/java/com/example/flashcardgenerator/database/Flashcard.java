package com.example.flashcardgenerator.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "flashcards")
public class Flashcard {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String question;
    public String answer;
    public String category;
    public String userId;
    public int reviewCount;
    public int difficulty; // 1-5 scale
    public boolean isFavorite;
    public long createdAt;
    public long updatedAt;
    public long lastReviewedAt;
    
    public Flashcard(String question, String answer, String category, String userId) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.userId = userId;
        this.reviewCount = 0;
        this.difficulty = 3;
        this.isFavorite = false;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.lastReviewedAt = 0;
    }
    
    public Flashcard() {}
}
