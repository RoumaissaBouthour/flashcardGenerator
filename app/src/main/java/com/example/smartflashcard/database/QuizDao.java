package com.example.smartflashcard.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.smartflashcard.models.Quiz;
import java.util.List;

@Dao
public interface QuizDao {
    @Query("SELECT * FROM quizzes WHERE topicId = :topicId")
    List<Quiz> getQuizzesForTopic(String topicId);

    @Insert
    void insertAll(List<Quiz> quizzes);
}