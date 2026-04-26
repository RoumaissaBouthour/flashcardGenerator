package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartflashcard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private static final int EDIT_PROFILE_REQUEST = 101;
    private ImageButton backBtn;
    private View logoutBtn;
    private TextView userNameTv, userEmailTv, userAvatarTv;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        backBtn = findViewById(R.id.back_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        userNameTv = findViewById(R.id.user_name_tv);
        userEmailTv = findViewById(R.id.user_email_tv);
        userAvatarTv = findViewById(R.id.user_avatar_tv);
        
        View editProfileBtn = findViewById(R.id.edit_profile_btn);
        View notificationsBtn = findViewById(R.id.notifications_btn);
        View appearanceBtn = findViewById(R.id.appearance_btn);
        View privacyBtn = findViewById(R.id.privacy_btn);

        displayUserProfile();

        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v -> {
                // Sign out from Firebase
                mAuth.signOut();
                
                // Clear activity stack and go to LoginActivity
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (editProfileBtn != null) {
            editProfileBtn.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST);
            });
        }

        View.OnClickListener comingSoon = v -> 
                Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show();

        if (notificationsBtn != null) notificationsBtn.setOnClickListener(comingSoon);
        if (appearanceBtn != null) appearanceBtn.setOnClickListener(comingSoon);
        if (privacyBtn != null) privacyBtn.setOnClickListener(comingSoon);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {
            // Refresh profile if it was updated
            displayUserProfile();
        }
    }

    private void displayUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String name = user.getDisplayName();

            if (name == null || name.isEmpty()) {
                // If display name is not set, use the part of email before @
                if (email != null && email.contains("@")) {
                    name = email.split("@")[0];
                    // Capitalize first letter
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                } else {
                    name = "User";
                }
            }

            userNameTv.setText(name);
            userEmailTv.setText(email);
            
            // Set avatar initial
            if (name.length() > 0) {
                userAvatarTv.setText(name.substring(0, 1).toUpperCase());
            }
        }
    }
}
