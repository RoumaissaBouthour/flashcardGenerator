package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private RecyclerView optionsRecyclerView;
    private OptionsAdapter optionsAdapter;
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
        resultContainer = findViewById(R.id.result_container);
        optionsRecyclerView = findViewById(R.id.options_recycler_view);

        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        nextBtn.setOnClickListener(v -> nextQuestion());
        backHeaderBtn.setOnClickListener(v -> finish());
        tryAgainBtn.setOnClickListener(v -> retakeQuiz());
        backBtn.setOnClickListener(v -> finish());

        loadQuizzes();
    }

    private void loadQuizzes() {
        new Thread(() -> {
            List<Quiz> allData = AppDatabase.getInstance(this).quizDao().getQuizzesForTopic(topicId);

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

        selectedAnswer = -1;

        String[] options = question.getOptions();
        if (options != null) {
            optionsAdapter = new OptionsAdapter(options, index -> {
                selectedAnswer = index;
            });
            optionsRecyclerView.setAdapter(optionsAdapter);
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
        optionsRecyclerView.setVisibility(View.GONE);
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
        optionsRecyclerView.setVisibility(View.VISIBLE);
        resultContainer.setVisibility(View.GONE);
        questionTv.setVisibility(View.VISIBLE);
        progressTv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        displayQuestion();
    }


    private static class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.OptionViewHolder> {

        interface OnOptionSelectedListener {
            void onSelected(int index);
        }

        private final String[] options;
        private final OnOptionSelectedListener listener;
        private int selectedIndex = -1;

        OptionsAdapter(String[] options, OnOptionSelectedListener listener) {
            this.options = options;
            this.listener = listener;
        }

        @Override
        public OptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Button btn = new Button(parent.getContext());
            btn.setAllCaps(false);
            btn.setTextSize(14f);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 20);
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.rounded_input_bg);
            return new OptionViewHolder(btn);
        }

        @Override
        public void onBindViewHolder(OptionViewHolder holder, int position) {
            holder.button.setText(options[position]);
            holder.button.setAlpha(selectedIndex == position ? 1.0f : 0.7f);
            holder.button.setElevation(selectedIndex == position ? 8.0f : 2.0f);

            holder.button.setOnClickListener(v -> {
                int previous = selectedIndex;
                selectedIndex = holder.getAdapterPosition();
                if (previous != -1) notifyItemChanged(previous);
                notifyItemChanged(selectedIndex);
                listener.onSelected(selectedIndex);
            });
        }

        @Override
        public int getItemCount() {
            return options.length;
        }

        static class OptionViewHolder extends RecyclerView.ViewHolder {
            Button button;
            OptionViewHolder(Button btn) {
                super(btn);
                this.button = btn;
            }
        }
    }
}