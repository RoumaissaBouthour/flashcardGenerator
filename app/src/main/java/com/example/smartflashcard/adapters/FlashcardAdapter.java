package com.example.smartflashcard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartflashcard.R;
import com.example.smartflashcard.models.Flashcard;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> {

    private List<Flashcard> flashcards;

    public FlashcardAdapter(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        holder.bind(flashcards.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    public static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        private TextView numberTv, questionTv, answerTv;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTv = itemView.findViewById(R.id.number_tv);
            questionTv = itemView.findViewById(R.id.question_tv);
            answerTv = itemView.findViewById(R.id.answer_tv);
        }

        public void bind(Flashcard flashcard, int number) {
            numberTv.setText(String.valueOf(number));
            questionTv.setText(flashcard.getQuestion());
            answerTv.setText(flashcard.getAnswer());
        }
    }
}