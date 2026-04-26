package com.example.smartflashcard.utils;

import com.example.smartflashcard.models.Topic;
import com.example.smartflashcard.models.Flashcard;
import com.example.smartflashcard.models.Quiz;
import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Topic> getTopics() {
        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic("1", "Biology - Chapter 3", 24, 3, "2 days ago", true, 75));
        topics.add(new Topic("2", "Mathematics - Algebra", 18, 2, "5 days ago", false, 45));
        topics.add(new Topic("3", "History - World War II", 32, 4, "1 week ago", true, 90));
        topics.add(new Topic("4", "Physics - Motion", 15, 2, "3 days ago", false, 30));
        return topics;
    }

    public static List<Flashcard> getFlashcards() {
        List<Flashcard> flashcards = new ArrayList<>();
        flashcards.add(new Flashcard("1",
                "What is photosynthesis?",
                "The process by which plants convert light energy into chemical energy using chlorophyll, water, and carbon dioxide to produce glucose and oxygen."));
        flashcards.add(new Flashcard("2",
                "What are the main parts of a cell?",
                "The nucleus (contains genetic material), cytoplasm (gel-like substance), cell membrane (protective barrier), and various organelles like mitochondria and ribosomes."));
        flashcards.add(new Flashcard("3",
                "What is DNA?",
                "Deoxyribonucleic acid - a molecule that carries genetic information and instructions for growth, development, and reproduction in all living organisms."));
        flashcards.add(new Flashcard("4",
                "What is the function of mitochondria?",
                "Mitochondria are the powerhouse of the cell, responsible for producing ATP (energy) through cellular respiration."));
        return flashcards;
    }

    public static List<Quiz> getQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        quizzes.add(new Quiz("1", "Quick Quiz - Basics", 10, true, 85));
        quizzes.add(new Quiz("2", "Mid-term Practice", 20, false, 0));
        quizzes.add(new Quiz("3", "Final Review", 30, false, 0));
        return quizzes;
    }

    public static List<Quiz> getQuizQuestions() {
        List<Quiz> questions = new ArrayList<>();

        questions.add(new Quiz("1",
                "What is the powerhouse of the cell?",
                new String[]{"Nucleus", "Mitochondria", "Ribosome", "Chloroplast"},
                1));

        questions.add(new Quiz("2",
                "Which molecule carries genetic information?",
                new String[]{"RNA", "Protein", "DNA", "Lipid"},
                2));

        questions.add(new Quiz("3",
                "What is the process of plants making food called?",
                new String[]{"Respiration", "Photosynthesis", "Digestion", "Fermentation"},
                1));

        return questions;
    }
}