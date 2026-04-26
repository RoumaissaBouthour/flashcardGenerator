package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartflashcard.R;

public class ProcessingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(ProcessingActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}