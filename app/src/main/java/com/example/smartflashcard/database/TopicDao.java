package com.example.smartflashcard.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.smartflashcard.models.Topic;
import java.util.List;

@Dao
public interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY lastUpdated DESC")
    List<Topic> getAllTopics();

    @Insert
    void insert(Topic topic);

    @Update
    void update(Topic topic);

    @Delete
    void delete(Topic topic);
}