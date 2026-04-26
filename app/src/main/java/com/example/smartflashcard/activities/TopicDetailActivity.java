package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartflashcard.R;
import com.example.smartflashcard.adapters.FlashcardAdapter;
import com.example.smartflashcard.adapters.QuizAdapter;
import com.example.smartflashcard.database.AppDatabase;
import com.example.smartflashcard.models.Topic;
import com.example.smartflashcard.utils.MockData;

public class TopicDetailActivity extends AppCompatActivity {

    private String topicId;
    private Topic currentTopic;
    private RecyclerView recyclerView;
    private View studyBtn;
    private ImageButton backBtn;
    private TextView topicTitleTv, topicStatsTv;
    private ImageButton editBtn, deleteBtn;
    
    private LinearLayout tabFlashcards, tabQuizzes;
    private TextView tvFlashcards, tvQuizzes;
    private ImageView ivFlashcards, ivQuizzes;
    
    private boolean isQuizzesTab = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        // Initialize views
        backBtn = findViewById(R.id.back_btn);
        studyBtn = findViewById(R.id.study_btn);
        recyclerView = findViewById(R.id.flashcards_recycler_view);
        topicTitleTv = findViewById(R.id.topic_title_tv);
        topicStatsTv = findViewById(R.id.topic_stats_tv);
        editBtn = findViewById(R.id.edit_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        
        tabFlashcards = findViewById(R.id.tab_flashcards);
        tabQuizzes = findViewById(R.id.tab_quizzes);
        
        // Find views within tabs
        tvFlashcards = (TextView) tabFlashcards.getChildAt(1);
        tvQuizzes = (TextView) tabQuizzes.getChildAt(1);
        ivFlashcards = (ImageView) tabFlashcards.getChildAt(0);
        ivQuizzes = (ImageView) tabQuizzes.getChildAt(0);

        topicId = getIntent().getStringExtra("topicId");

        backBtn.setOnClickListener(v -> finish());

        studyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TopicDetailActivity.this, FlashcardStudyActivity.class);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
        });

        editBtn.setOnClickListener(v -> showEditDialog());
        deleteBtn.setOnClickListener(v -> showDeleteConfirmation());

        tabFlashcards.setOnClickListener(v -> switchTab(false));
        tabQuizzes.setOnClickListener(v -> switchTab(true));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadTopicData();
    }

    private void switchTab(boolean toQuizzes) {
        if (isQuizzesTab == toQuizzes) return;
        isQuizzesTab = toQuizzes;

        // Update Tab UI
        if (isQuizzesTab) {
            tabQuizzes.setBackgroundResource(R.drawable.rounded_topic_card_bg);
            tabQuizzes.setElevation(dpToPx(2));
            tabFlashcards.setBackground(null);
            tabFlashcards.setElevation(0);
            
            tvQuizzes.setTextColor(getResources().getColor(R.color.gray_900));
            ivQuizzes.setColorFilter(getResources().getColor(R.color.indigo_600));
            tvFlashcards.setTextColor(getResources().getColor(R.color.gray_600));
            ivFlashcards.setColorFilter(getResources().getColor(R.color.gray_600));
            
            studyBtn.setVisibility(View.GONE);
            recyclerView.setAdapter(new QuizAdapter(MockData.getQuizzes()));
        } else {
            tabFlashcards.setBackgroundResource(R.drawable.rounded_topic_card_bg);
            tabFlashcards.setElevation(dpToPx(2));
            tabQuizzes.setBackground(null);
            tabQuizzes.setElevation(0);
            
            tvFlashcards.setTextColor(getResources().getColor(R.color.gray_900));
            ivFlashcards.setColorFilter(getResources().getColor(R.color.indigo_600));
            tvQuizzes.setTextColor(getResources().getColor(R.color.gray_600));
            ivQuizzes.setColorFilter(getResources().getColor(R.color.gray_600));
            
            studyBtn.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new FlashcardAdapter(MockData.getFlashcards()));
        }
    }

    private void showEditDialog() {
        if (currentTopic == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Topic");
        
        final EditText input = new EditText(this);
        input.setText(currentTopic.getTitle());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                currentTopic.setTitle(newTitle);
                new Thread(() -> {
                    AppDatabase.getInstance(this).topicDao().update(currentTopic);
                    runOnUiThread(() -> topicTitleTv.setText(newTitle));
                }).start();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Topic")
                .setMessage("Are you sure you want to delete this topic?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase.getInstance(this).topicDao().delete(currentTopic);
                        runOnUiThread(this::finish);
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadTopicData() {
        new Thread(() -> {
            currentTopic = AppDatabase.getInstance(this).topicDao().getAllTopics().stream()
                    .filter(t -> t.getId().equals(topicId))
                    .findFirst()
                    .orElse(null);

            runOnUiThread(() -> {
                if (currentTopic != null) {
                    topicTitleTv.setText(currentTopic.getTitle());
                    topicStatsTv.setText(currentTopic.getFlashcards() + " flashcards • " + currentTopic.getQuizzes() + " quizzes");
                }
                
                // Set initial adapter
                if (isQuizzesTab) {
                    recyclerView.setAdapter(new QuizAdapter(MockData.getQuizzes()));
                } else {
                    recyclerView.setAdapter(new FlashcardAdapter(MockData.getFlashcards()));
                }
            });
        }).start();
    }
    
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}