package com.example.smartflashcard.utils;

import com.example.smartflashcard.models.Topic;
import java.util.ArrayList;
import java.util.List;

public class MockData {
    public static List<Topic> getMockTopics() {
        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic("1", "History"));
        topics.add(new Topic("2", "Science"));
        return topics;
    }
}
