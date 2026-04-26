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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartflashcard.R;
import com.example.smartflashcard.database.AppDatabase;
import com.example.smartflashcard.models.Quiz;
import java.util.List;
import java.util.stream.Collectors;

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
        if (topicId == null) {
            Toast.makeText(this, "Topic ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        questionTv = findViewById(R.id.question_tv);
        progressTv = findViewById(R.id.progress_tv);
        nextBtn = findViewById(R.id.next_btn);
        tryAgainBtn = findViewById(R.id.try_again_btn);
        backBtn = findViewById(R.id.back_btn);
        backHeaderBtn = findViewById(R.id.back_header_btn);
        progressBar = findViewById(R.id.progress_bar);
        optionsContainer = findViewById(R.id.options_container);
        resultContainer = findViewById(R.id.result_container);

        nextBtn.setOnClickListener(v -> nextQuestion());
        backHeaderBtn.setOnClickListener(v -> finish());
        tryAgainBtn.setOnClickListener(v -> retakeQuiz());
        backBtn.setOnClickListener(v -> finish());

        loadQuizzes();
    }

    private void loadQuizzes() {
        new Thread(() -> {
            List<Quiz> allData = AppDatabase.getInstance(this).quizDao().getQuizzesForTopic(topicId);
            
            // Filter: Only keep items that have actual question text AND non-null options
            questions = allData.stream()
                    .filter(q -> q.getQuestion() != null && !q.getQuestion().isEmpty() && q.getOptions() != null)
                    .collect(Collectors.toList());

            runOnUiThread(() -> {
                if (questions == null || questions.isEmpty()) {
                    Toast.makeText(this, "No quiz questions found", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    displayQuestion();
                }
            });
        }).start();
    }

    private void displayQuestion() {
        if (showResult) {
            showResultScreen();
            return;
        }

        if (questions == null || questions.isEmpty() || currentQuestion >= questions.size()) return;

        Quiz question = questions.get(currentQuestion);
        questionTv.setText(question.getQuestion());
        progressTv.setText(getString(R.string.quiz_progress, currentQuestion + 1, questions.size()));
        progressBar.setProgress((int) (((currentQuestion + 1) / (float) questions.size()) * 100));

        optionsContainer.removeAllViews();
        selectedAnswer = -1;

        String[] options = question.getOptions();
        if (options != null) {
            for (int i = 0; i < options.length; i++) {
                // Manually create the button to avoid resource-based ClassCastException
                Button optionBtn = new Button(this);
                optionBtn.setText(options[i]);
                optionBtn.setAllCaps(false);
                
                // Set layout params for margin
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 20);
                optionBtn.setLayoutParams(params);
                
                // Apply background safely
                optionBtn.setBackgroundResource(R.drawable.rounded_input_bg);

                int finalI = i;
                optionBtn.setOnClickListener(v -> {
                    selectedAnswer = finalI;
                    // Update visual state of all buttons in container
                    for (int j = 0; j < optionsContainer.getChildCount(); j++) {
                        View child = optionsContainer.getChildAt(j);
                        child.setAlpha(j == finalI ? 1.0f : 0.6f);
                        child.setElevation(j == finalI ? 8.0f : 2.0f);
                    }
                });
                optionsContainer.addView(optionBtn);
            }
        }

        nextBtn.setText(currentQuestion == questions.size() - 1 ? R.string.finish_quiz : R.string.next_question);
    }

    private void nextQuestion() {
        if (selectedAnswer == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

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
        questionTv.setVisibility(View.GONE);
        progressTv.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);

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
        questionTv.setVisibility(View.VISIBLE);
        progressTv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        displayQuestion();
    }
}
