package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartflashcard.R;
import com.example.smartflashcard.adapters.TopicAdapter;
import com.example.smartflashcard.models.Topic;
import com.example.smartflashcard.utils.MockData;

public class HomeActivity extends AppCompatActivity implements TopicAdapter.OnTopicClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView rvTopics = findViewById(R.id.topics_recycler_view);
        ImageButton btnAdd = findViewById(R.id.add_btn);
        ImageButton btnSettings = findViewById(R.id.settings_btn);

        if (rvTopics != null) {
            rvTopics.setLayoutManager(new LinearLayoutManager(this));
            rvTopics.setAdapter(new TopicAdapter(MockData.getTopics(), this));
        }

        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ScanUploadActivity.class);
                startActivity(intent);
            });
        }

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onTopicClick(Topic topic) {
        Intent intent = new Intent(this, TopicDetailActivity.class);
        intent.putExtra("topicId", topic.getId());
        startActivity(intent);
    }
}
