package com.example.smartflashcard.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.smartflashcard.database.Converters;

@Entity(tableName = "quizzes")
@TypeConverters(Converters.class)
public class Quiz {
    @PrimaryKey
    @NonNull
    private String id;
    private String topicId;
    private String quizId; // Used to group questions under a specific quiz header
    private String title;
    private String question;
    private String[] options;
    private int correctAnswer;
    private int questions;
    private boolean completed;
    private int score;

    public Quiz(@NonNull String id, String topicId, String question, String[] options, int correctAnswer) {
        this.id = id;
        this.topicId = topicId;
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    @Ignore
    public Quiz(String id, String title, int questions, boolean completed, int score) {
        this.id = id;
        this.title = title;
        this.questions = questions;
        this.completed = completed;
        this.score = score;
    }

    @Ignore
    public Quiz(String id, String question, String[] options, int correctAnswer) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getTopicId() { return topicId; }
    public void setTopicId(String topicId) { this.topicId = topicId; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String[] getOptions() { return options; }
    public void setOptions(String[] options) { this.options = options; }

    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }

    public int getQuestions() { return questions; }
    public void setQuestions(int questions) { this.questions = questions; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
