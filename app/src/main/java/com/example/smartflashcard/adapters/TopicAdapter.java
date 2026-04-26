package com.example.smartflashcard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartflashcard.R;
import com.example.smartflashcard.models.Topic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<Topic> topics;
    private List<Topic> topicsFull;
    private OnTopicClickListener listener;
    private boolean showingFavorites = false;

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);
        void onFavoriteClick(Topic topic);
    }

    public TopicAdapter(List<Topic> topics, OnTopicClickListener listener) {
        this.topics = topics != null ? new ArrayList<>(topics) : new ArrayList<>();
        this.topicsFull = new ArrayList<>(this.topics);
        this.listener = listener;
    }

    public void updateData(List<Topic> newTopics) {
        this.topicsFull = newTopics != null ? new ArrayList<>(newTopics) : new ArrayList<>();
        applyFilters("");
    }

    public void filter(String text) {
        applyFilters(text);
    }

    public void toggleFavorites(boolean showOnlyFavorites) {
        this.showingFavorites = showOnlyFavorites;
        applyFilters("");
    }

    public void sortData(boolean byTitle) {
        if (byTitle) {
            Collections.sort(topicsFull, (t1, t2) -> t1.getTitle().compareToIgnoreCase(t2.getTitle()));
        } else {
            Collections.sort(topicsFull, (t1, t2) -> t2.getLastUpdated().compareTo(t1.getLastUpdated()));
        }
        applyFilters("");
    }

    private void applyFilters(String query) {
        topics.clear();
        String lowerQuery = query.toLowerCase().trim();
        for (Topic item : topicsFull) {
            boolean matchesQuery = item.getTitle().toLowerCase().contains(lowerQuery);
            boolean matchesFavorite = !showingFavorites || item.isFavorite();
            
            if (matchesQuery && matchesFavorite) {
                topics.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        holder.bind(topics.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv, updatedTv, flashcardsTv, quizzesTv, progressTv;
        private ProgressBar progressBar;
        private ImageView favoriteStar;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title_tv);
            updatedTv = itemView.findViewById(R.id.updated_tv);
            flashcardsTv = itemView.findViewById(R.id.flashcards_tv);
            quizzesTv = itemView.findViewById(R.id.quizzes_tv);
            progressTv = itemView.findViewById(R.id.progress_tv);
            progressBar = itemView.findViewById(R.id.progress_bar);
            favoriteStar = itemView.findViewById(R.id.favorite_star);
        }

        public void bind(Topic topic, OnTopicClickListener listener) {
            if (titleTv != null) titleTv.setText(topic.getTitle());
            if (updatedTv != null) updatedTv.setText("Updated " + topic.getLastUpdated());
            if (flashcardsTv != null) flashcardsTv.setText(topic.getFlashcards() + " cards");
            if (quizzesTv != null) quizzesTv.setText(topic.getQuizzes() + " quizzes");
            if (progressTv != null) progressTv.setText(topic.getProgress() + "%");
            if (progressBar != null) progressBar.setProgress(topic.getProgress());
            
            if (favoriteStar != null) {
                favoriteStar.setVisibility(View.VISIBLE);
                favoriteStar.setImageResource(topic.isFavorite() ? R.drawable.ic_star : R.drawable.ic_star_outline);
                favoriteStar.setOnClickListener(v -> listener.onFavoriteClick(topic));
            }

            itemView.setOnClickListener(v -> listener.onTopicClick(topic));
        }
    }
}