package com.example.smartflashcard.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.smartflashcard.models.Flashcard;
import java.util.List;

@Dao
public interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE topicId = :topicId")
    List<Flashcard> getFlashcardsForTopic(String topicId);

    @Insert
    void insertAll(List<Flashcard> flashcards);
}