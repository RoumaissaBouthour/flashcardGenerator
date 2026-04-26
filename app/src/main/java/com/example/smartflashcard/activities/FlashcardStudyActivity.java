package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartflashcard.R;
import com.example.smartflashcard.models.Flashcard;
import com.example.smartflashcard.utils.MockData;
import java.util.List;

public class FlashcardStudyActivity extends AppCompatActivity {

    private String topicId;
    private List<Flashcard> flashcards;
    private int currentIndex = 0;
    private boolean isFlipped = false;

    private TextView cardContentTv, progressTv;
    private Button prevBtn, nextBtn, completeBtn;
    private ImageButton backBtn, resetBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_study);

        topicId = getIntent().getStringExtra("topicId");
        flashcards = MockData.getFlashcards();

        cardContentTv = findViewById(R.id.card_content);
        progressTv = findViewById(R.id.progress_tv);
        prevBtn = findViewById(R.id.prev_btn);
        nextBtn = findViewById(R.id.next_btn);
        completeBtn = findViewById(R.id.complete_btn);
        backBtn = findViewById(R.id.back_btn);
        resetBtn = findViewById(R.id.reset_btn);
        progressBar = findViewById(R.id.progress_bar);

        displayCard();

        cardContentTv.setOnClickListener(v -> flipCard());
        prevBtn.setOnClickListener(v -> previousCard());
        nextBtn.setOnClickListener(v -> nextCard());
        backBtn.setOnClickListener(v -> finish());
        resetBtn.setOnClickListener(v -> resetStudy());

        completeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FlashcardStudyActivity.this, TopicDetailActivity.class);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
            finish();
        });
    }

    private void displayCard() {
        Flashcard card = flashcards.get(currentIndex);
        cardContentTv.setText(isFlipped ? card.getAnswer() : card.getQuestion());
        progressTv.setText((currentIndex + 1) + " / " + flashcards.size());
        progressBar.setProgress((int) (((currentIndex + 1) / (float) flashcards.size()) * 100));

        prevBtn.setEnabled(currentIndex > 0);
        nextBtn.setEnabled(currentIndex < flashcards.size() - 1);
        completeBtn.setVisibility(currentIndex == flashcards.size() - 1 ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void flipCard() {
        isFlipped = !isFlipped;
        displayCard();
    }

    private void nextCard() {
        if (currentIndex < flashcards.size() - 1) {
            currentIndex++;
            isFlipped = false;
            displayCard();
        }
    }

    private void previousCard() {
        if (currentIndex > 0) {
            currentIndex--;
            isFlipped = false;
            displayCard();
        }
    }

    private void resetStudy() {
        currentIndex = 0;
        isFlipped = false;
        displayCard();
    }
}