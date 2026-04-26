package com.example.smartflashcard.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "flashcards")
public class Flashcard {
    @PrimaryKey
    @NonNull
    private String id;
    private String topicId;
    private String question;
    private String answer;

    public Flashcard(@NonNull String id, String topicId, String question, String answer) {
        this.id = id;
        this.topicId = topicId;
        this.question = question;
        this.answer = answer;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getTopicId() { return topicId; }
    public void setTopicId(String topicId) { this.topicId = topicId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}