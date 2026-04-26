package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartflashcard.R;
import com.example.smartflashcard.adapters.TopicAdapter;
import com.example.smartflashcard.database.AppDatabase;
import com.example.smartflashcard.models.Topic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements TopicAdapter.OnTopicClickListener {
    
    private RecyclerView rvTopics;
    private TopicAdapter adapter;
    private List<Topic> topicList = new ArrayList<>();
    private EditText searchInput;
    private boolean isFavoriteFilterActive = false;
    private TextView recentTopicTv, welcomeUserTv;
    private Topic latestTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rvTopics = findViewById(R.id.topics_recycler_view);
        ImageButton btnAdd = findViewById(R.id.add_btn);
        ImageButton btnSettings = findViewById(R.id.settings_btn);
        View profileIcon = findViewById(R.id.profile_icon);
        LinearLayout favoritesBtn = findViewById(R.id.favorites_btn);
        LinearLayout filterBtn = findViewById(R.id.filter_btn);
        LinearLayout recentActivityCard = findViewById(R.id.recent_activity_card);
        recentTopicTv = findViewById(R.id.recent_topic_tv);
        welcomeUserTv = findViewById(R.id.welcome_user_tv);
        LinearLayout quickStudyBtn = findViewById(R.id.quick_study_btn);
        searchInput = findViewById(R.id.search_input);

        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new TopicAdapter(topicList, this);
        rvTopics.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, ScanUploadActivity.class)));
        
        View.OnClickListener openSettings = v -> startActivity(new Intent(this, SettingsActivity.class));
        btnSettings.setOnClickListener(openSettings);
        profileIcon.setOnClickListener(openSettings);

        favoritesBtn.setOnClickListener(v -> {
            isFavoriteFilterActive = !isFavoriteFilterActive;
            adapter.toggleFavorites(isFavoriteFilterActive);
            favoritesBtn.setBackgroundResource(isFavoriteFilterActive ? R.drawable.rounded_fab_bg : R.drawable.rounded_favorite_bg);
            Toast.makeText(this, isFavoriteFilterActive ? "Showing Favorites" : "Showing All Topics", Toast.LENGTH_SHORT).show();
        });

        filterBtn.setOnClickListener(v -> showFilterMenu(v));

        quickStudyBtn.setOnClickListener(v -> {
            if (latestTopic != null) {
                onTopicClick(latestTopic);
            } else {
                Toast.makeText(this, "No topics to study yet!", Toast.LENGTH_SHORT).show();
            }
        });

        if (searchInput != null) {
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.filter(s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void displayWelcomeMessage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && welcomeUserTv != null) {
            user.reload().addOnCompleteListener(task -> {
                String name = user.getDisplayName();
                if (name == null || name.isEmpty()) {
                    String email = user.getEmail();
                    if (email != null && email.contains("@")) {
                        name = email.split("@")[0];
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    } else {
                        name = "User";
                    }
                }
                welcomeUserTv.setText("Welcome back, " + name);
            });
        }
    }

    private void showFilterMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Sort by Title");
        popup.getMenu().add("Sort by Last Updated");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Sort by Title")) {
                adapter.sortData(true);
            } else {
                adapter.sortData(false);
            }
            return true;
        });
        popup.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayWelcomeMessage();
        loadTopics(); 
    }

    private void loadTopics() {
        new Thread(() -> {
            List<Topic> topics = AppDatabase.getInstance(this).topicDao().getAllTopics();
            runOnUiThread(() -> {
                if (!topics.isEmpty()) {
                    latestTopic = topics.get(0);
                    recentTopicTv.setText("You studied " + latestTopic.getTitle());
                } else {
                    recentTopicTv.setText("No recent activity");
                    latestTopic = null;
                }
                adapter.updateData(topics);
            });
        }).start();
    }

    @Override
    public void onTopicClick(Topic topic) {
        Intent intent = new Intent(this, TopicDetailActivity.class);
        intent.putExtra("topicId", topic.getId());
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Topic topic) {
        topic.setFavorite(!topic.isFavorite());
        new Thread(() -> {
            AppDatabase.getInstance(this).topicDao().update(topic);
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                if (isFavoriteFilterActive && !topic.isFavorite()) {
                    loadTopics(); // Refresh list if we are in favorite view and removed one
                }
            });
        }).start();
    }
}
