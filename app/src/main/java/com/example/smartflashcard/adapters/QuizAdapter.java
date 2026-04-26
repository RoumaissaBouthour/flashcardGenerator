package com.example.smartflashcard.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartflashcard.R;
import com.example.smartflashcard.activities.QuizActivity;
import com.example.smartflashcard.models.Quiz;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizzes;

    public QuizAdapter(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        holder.bind(quizzes.get(position));
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv, questionsTv, scoreTv, completedBadge;
        private View startBtn;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.quiz_title_tv);
            questionsTv = itemView.findViewById(R.id.quiz_questions_tv);
            scoreTv = itemView.findViewById(R.id.quiz_score_tv);
            completedBadge = itemView.findViewById(R.id.completed_badge);
            startBtn = itemView.findViewById(R.id.start_quiz_btn);
        }

        public void bind(Quiz quiz) {
            Context context = itemView.getContext();
            titleTv.setText(quiz.getTitle() != null ? quiz.getTitle() : "Topic Quiz");
            questionsTv.setText(quiz.getQuestions() + " questions");
            
            if (quiz.isCompleted()) {
                completedBadge.setVisibility(View.VISIBLE);
                scoreTv.setVisibility(View.VISIBLE);
                scoreTv.setText(quiz.getScore() + "%");
                startBtn.setVisibility(View.GONE);
            } else {
                completedBadge.setVisibility(View.GONE);
                scoreTv.setVisibility(View.GONE);
                startBtn.setVisibility(View.VISIBLE);
            }

            startBtn.setOnClickListener(v -> {
                Intent intent = new Intent(context, QuizActivity.class);
                intent.putExtra("topicId", quiz.getTopicId());
                context.startActivity(intent);
            });
        }
    }
}
