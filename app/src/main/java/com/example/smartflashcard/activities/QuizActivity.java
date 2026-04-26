package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartflashcard.R;
import com.example.smartflashcard.models.Quiz;
import com.example.smartflashcard.utils.MockData;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private String topicId;
    private List<Quiz> questions;
    private int currentQuestion = 0;
    private int selectedAnswer = -1;
    private int score = 0;
    private boolean showResult = false;

    private TextView questionTv, progressTv;
    private Button nextBtn, tryAgainBtn, backBtn;
    private ImageButton backHeaderBtn;
    private ProgressBar progressBar;
    private LinearLayout optionsContainer;
    private FrameLayout resultContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        topicId = getIntent().getStringExtra("topicId");
        questions = MockData.getQuizQuestions();

        questionTv = findViewById(R.id.question_tv);
        progressTv = findViewById(R.id.progress_tv);
        nextBtn = findViewById(R.id.next_btn);
        tryAgainBtn = findViewById(R.id.try_again_btn);
        backBtn = findViewById(R.id.back_btn);
        backHeaderBtn = findViewById(R.id.back_header_btn);
        progressBar = findViewById(R.id.progress_bar);
        optionsContainer = findViewById(R.id.options_container);
        resultContainer = findViewById(R.id.result_container);

        displayQuestion();

        nextBtn.setOnClickListener(v -> nextQuestion());
        backHeaderBtn.setOnClickListener(v -> finish());
        tryAgainBtn.setOnClickListener(v -> retakeQuiz());
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(QuizActivity.this, TopicDetailActivity.class);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
            finish();
        });
    }

    private void displayQuestion() {
        if (showResult) {
            showResultScreen();
            return;
        }

        Quiz question = questions.get(currentQuestion);
        questionTv.setText(question.getQuestion());
        progressTv.setText(getString(R.string.quiz_progress, currentQuestion + 1, questions.size()));
        progressBar.setProgress((int) (((currentQuestion + 1) / (float) questions.size()) * 100));

        optionsContainer.removeAllViews();
        selectedAnswer = -1;

        for (int i = 0; i < question.getOptions().length; i++) {
            Button optionBtn = new Button(this);
            optionBtn.setText(question.getOptions()[i]);
            int finalI = i;
            optionBtn.setOnClickListener(v -> selectedAnswer = finalI);
            optionsContainer.addView(optionBtn);
        }

        nextBtn.setText(currentQuestion == questions.size() - 1 ? R.string.finish_quiz : R.string.next_question);
    }

    private void nextQuestion() {
        if (selectedAnswer == -1) return;

        Quiz question = questions.get(currentQuestion);
        if (selectedAnswer == question.getCorrectAnswer()) {
            score++;
        }

        if (currentQuestion == questions.size() - 1) {
            showResult = true;
            displayQuestion();
        } else {
            currentQuestion++;
            displayQuestion();
        }
    }

    private void showResultScreen() {
        optionsContainer.setVisibility(View.GONE);
        resultContainer.setVisibility(View.VISIBLE);

        int percentage = Math.round((score / (float) questions.size()) * 100);
        TextView scoreTV = findViewById(R.id.score_tv);
        scoreTV.setText(getString(R.string.score_percentage, percentage));

        TextView resultTv = findViewById(R.id.result_tv);
        resultTv.setText(percentage >= 70 ? R.string.great_job : R.string.keep_practicing);
    }

    private void retakeQuiz() {
        currentQuestion = 0;
        selectedAnswer = -1;
        score = 0;
        showResult = false;
        optionsContainer.setVisibility(View.VISIBLE);
        resultContainer.setVisibility(View.GONE);
        displayQuestion();
    }
}
