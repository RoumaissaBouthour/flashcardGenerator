package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartflashcard.R;
import com.example.smartflashcard.models.Flashcard;
import com.example.smartflashcard.utils.MockData;
import java.util.List;

public class TopicDetailActivity extends AppCompatActivity {

    private String topicId;
    private RecyclerView flashcardsRecyclerView;
    private Button studyBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        backBtn = findViewById(R.id.back_btn);
        studyBtn = findViewById(R.id.study_btn);
        flashcardsRecyclerView = findViewById(R.id.flashcards_recycler_view);

        topicId = getIntent().getStringExtra("topicId");

        backBtn.setOnClickListener(v -> finish());

        studyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TopicDetailActivity.this, FlashcardStudyActivity.class);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
        });

        List<Flashcard> flashcards = MockData.getFlashcards();
        flashcardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}