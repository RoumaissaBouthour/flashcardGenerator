package com.example.flashcardgenerator.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface FlashcardDao {
    @Insert
    long insertFlashcard(Flashcard flashcard);
    
    @Update
    void updateFlashcard(Flashcard flashcard);
    
    @Delete
    void deleteFlashcard(Flashcard flashcard);
    
    @Query("SELECT * FROM flashcards WHERE id = :id")
    Flashcard getFlashcardById(int id);
    
    @Query("SELECT * FROM flashcards WHERE userId = :userId ORDER BY createdAt DESC")
    List<Flashcard> getAllFlashcardsForUser(String userId);
    
    @Query("SELECT * FROM flashcards WHERE userId = :userId AND category = :category")
    List<Flashcard> getFlashcardsByCategory(String userId, String category);
    
    @Query("SELECT * FROM flashcards WHERE userId = :userId AND difficulty >= 4 ORDER BY reviewCount DESC")
    List<Flashcard> getDifficultFlashcards(String userId);
    
    @Query("SELECT * FROM flashcards WHERE userId = :userId AND isFavorite = 1")
    List<Flashcard> getFavoriteFlashcards(String userId);
    
    @Query("SELECT COUNT(*) FROM flashcards WHERE userId = :userId")
    int getTotalFlashcardsCount(String userId);
    
    @Query("SELECT AVG(reviewCount) FROM flashcards WHERE userId = :userId")
    double getAverageReviewCount(String userId);
}
