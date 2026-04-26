package com.example.smartflashcard.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartflashcard.R;
import com.example.smartflashcard.database.AppDatabase;
import com.example.smartflashcard.models.Topic;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.io.IOException;
import java.util.UUID;

public class ProcessingActivity extends AppCompatActivity {

    private TextView statusText;
    private EditText subjectInput;
    private Button saveBtn;
    private ProgressBar progressBar;
    private String recognizedText = "";
    private String imageUriString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        statusText = findViewById(R.id.status_text);
        subjectInput = findViewById(R.id.subject_name_input);
        saveBtn = findViewById(R.id.save_btn);
        progressBar = findViewById(R.id.progress_bar);

        imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            processImage(Uri.parse(imageUriString));
        } else {
            Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
            finish();
        }

        saveBtn.setOnClickListener(v -> {
            String subject = subjectInput.getText().toString().trim();
            if (subject.isEmpty()) {
                subjectInput.setError("Please enter a subject name");
                return;
            }
            saveTopic(subject);
        });
    }

    private void processImage(Uri imageUri) {
        try {
            InputImage image = InputImage.fromFilePath(this, imageUri);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        recognizedText = visionText.getText();
                        if (recognizedText.isEmpty()) {
                            statusText.setText("No text found in image.");
                        } else {
                            statusText.setText("Text extracted successfully!");
                        }
                        showInputFields();
                    })
                    .addOnFailureListener(e -> {
                        statusText.setText("Failed to recognize text.");
                        showInputFields();
                    });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showInputFields() {
        progressBar.setVisibility(View.GONE);
        subjectInput.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
    }

    private void saveTopic(String subjectName) {
        // Save to Room Database with the image path
        Topic newTopic = new Topic(
                UUID.randomUUID().toString(),
                subjectName,
                0,
                0,
                "Just now",
                false,
                0,
                imageUriString // Save the URI as a string
        );

        AppDatabase.getInstance(this).topicDao().insert(newTopic);

        Toast.makeText(this, "Topic '" + subjectName + "' created with attachment!", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(ProcessingActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}