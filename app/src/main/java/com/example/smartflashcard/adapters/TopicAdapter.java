package com.example.smartflashcard.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartflashcard.databinding.ItemTopicBinding;
import com.example.smartflashcard.models.Topic;
import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<Topic> topics;
    private List<Topic> topicsFiltered;
    private OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);
    }

    public TopicAdapter(List<Topic> topics, OnTopicClickListener listener) {
        this.topics = new ArrayList<>(topics);
        this.topicsFiltered = new ArrayList<>(topics);
        this.listener = listener;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemTopicBinding binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TopicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        holder.bind(topicsFiltered.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return topicsFiltered.size();
    }

    public void filter(String query) {
        topicsFiltered.clear();
        if (query.isEmpty()) {
            topicsFiltered.addAll(topics);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Topic topic : topics) {
                if (topic.getTitle().toLowerCase().contains(lowerQuery)) {
                    topicsFiltered.add(topic);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        private ItemTopicBinding binding;

        public TopicViewHolder(ItemTopicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Topic topic, OnTopicClickListener listener) {
            binding.titleTv.setText(topic.getTitle());
            binding.updatedTv.setText("Updated " + topic.getLastUpdated());
            binding.flashcardsTv.setText(topic.getFlashcards() + " cards");
            binding.quizzesTv.setText(topic.getQuizzes() + " quizzes");
            binding.progressTv.setText(topic.getProgress() + "%");
            binding.progressBar.setProgress(topic.getProgress());

            binding.getRoot().setOnClickListener(v -> listener.onTopicClick(topic));
        }
    }
}