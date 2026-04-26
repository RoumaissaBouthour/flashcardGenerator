package com.example.smartflashcard.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartflashcard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {

    private EditText usernameInput;
    private Button saveBtn;
    private ProgressBar loadingProgress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        ImageButton backBtn = findViewById(R.id.back_btn);
        usernameInput = findViewById(R.id.username_input);
        saveBtn = findViewById(R.id.save_btn);
        loadingProgress = findViewById(R.id.loading_progress);

        if (user != null) {
            String name = user.getDisplayName();
            if (name == null || name.isEmpty()) {
                String email = user.getEmail();
                if (email != null && email.contains("@")) {
                    name = email.split("@")[0];
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                }
            }
            usernameInput.setText(name);
        }

        backBtn.setOnClickListener(v -> finish());

        saveBtn.setOnClickListener(v -> {
            String newName = usernameInput.getText().toString().trim();
            if (newName.isEmpty()) {
                usernameInput.setError("Name cannot be empty");
                return;
            }
            updateProfile(newName);
        });
    }

    private void updateProfile(String newName) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            saveBtn.setVisibility(View.GONE);
            loadingProgress.setVisibility(View.VISIBLE);

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        loadingProgress.setVisibility(View.GONE);
                        saveBtn.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
