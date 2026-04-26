package com.example.smartflashcard.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.smartflashcard.models.Topic;

@Database(entities = {Topic.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract TopicDao topicDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "flashcard_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Only for simplicity in this example
                    .build();
        }
        return instance;
    }
}