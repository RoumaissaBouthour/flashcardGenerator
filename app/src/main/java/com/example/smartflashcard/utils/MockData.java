package com.example.smartflashcard.utils;

import com.example.smartflashcard.models.Topic;
import com.example.smartflashcard.models.Flashcard;
import com.example.smartflashcard.models.Quiz;
import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Topic> getTopics() {
        return new ArrayList<>();
    }

    public static List<Flashcard> getFlashcards() {
        return new ArrayList<>();
    }

    public static List<Quiz> getQuizzes() {
        return new ArrayList<>();
    }

    public static List<Quiz> getQuizQuestions() {
        return new ArrayList<>();
    }
}
