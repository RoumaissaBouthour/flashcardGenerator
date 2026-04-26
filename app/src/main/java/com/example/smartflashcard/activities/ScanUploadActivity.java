package com.example.smartflashcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartflashcard.R;

public class ScanUploadActivity extends AppCompatActivity {

    private Button scanBtn, uploadBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_upload);

        backBtn = findViewById(R.id.back_btn);
        scanBtn = findViewById(R.id.scan_btn);
        uploadBtn = findViewById(R.id.upload_btn);

        backBtn.setOnClickListener(v -> finish());

        scanBtn.setOnClickListener(v -> navigateToProcessing());
        uploadBtn.setOnClickListener(v -> navigateToProcessing());
    }

    private void navigateToProcessing() {
        startActivity(new Intent(ScanUploadActivity.this, ProcessingActivity.class));
    }
}