package com.example.smartflashcard.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.smartflashcard.models.Flashcard;
import com.example.smartflashcard.models.Quiz;
import com.example.smartflashcard.models.Topic;

@Database(entities = {Topic.class, Flashcard.class, Quiz.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract TopicDao topicDao();
    public abstract FlashcardDao flashcardDao();
    public abstract QuizDao quizDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "flashcard_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
