package com.example.smartflashcard.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartflashcard.R;
import com.example.smartflashcard.database.AppDatabase;
import com.example.smartflashcard.models.Quiz;
import com.example.smartflashcard.models.Topic;
import com.example.smartflashcard.utils.GeminiAI;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessingActivity extends AppCompatActivity {

    private TextView statusText;
    private EditText subjectInput;
    private Button saveBtn;
    private ProgressBar progressBar;
    private StringBuilder fullExtractedText = new StringBuilder();
    private String imageUriString = "";
    private AppDatabase db;
    private GeminiAI geminiAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        db = AppDatabase.getInstance(this);
        geminiAI = new GeminiAI();

        statusText = findViewById(R.id.status_text);
        subjectInput = findViewById(R.id.subject_name_input);
        saveBtn = findViewById(R.id.save_btn);
        progressBar = findViewById(R.id.progress_bar);

        imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            handleIncomingUri(Uri.parse(imageUriString));
        } else {
            Toast.makeText(this, "No file found", Toast.LENGTH_SHORT).show();
            finish();
        }

        saveBtn.setOnClickListener(v -> {
            String subject = subjectInput.getText().toString().trim();
            if (subject.isEmpty()) {
                subjectInput.setError("Please enter a subject name");
                return;
            }
            generateAIContentAndSave(subject);
        });
    }

    private void handleIncomingUri(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        
        if (mimeType == null) {
            String path = uri.getPath();
            if (path != null && path.toLowerCase().endsWith(".pdf")) {
                mimeType = "application/pdf";
            }
        }
        
        if ("application/pdf".equals(mimeType)) {
            processPdf(uri);
        } else {
            processImage(uri);
        }
    }

    private void processImage(Uri imageUri) {
        try {
            InputImage image = InputImage.fromFilePath(this, imageUri);
            recognizeText(image, true);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void processPdf(Uri pdfUri) {
        statusText.setText("Processing PDF...");
        new Thread(() -> {
            try {
                File tempFile = new File(getCacheDir(), "temp.pdf");
                copyUriToFile(pdfUri, tempFile);

                ParcelFileDescriptor pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer renderer = new PdfRenderer(pfd);
                
                int pageCount = renderer.getPageCount();
                int limit = Math.min(pageCount, 5);
                
                for (int i = 0; i < limit; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    
                    InputImage image = InputImage.fromBitmap(bitmap, 0);
                    recognizeTextSync(image, i == limit - 1);
                    
                    page.close();
                }
                
                renderer.close();
                pfd.close();
            } catch (Exception e) {
                Log.e("ProcessingActivity", "PDF error", e);
                runOnUiThread(() -> Toast.makeText(this, "Error processing PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void recognizeText(InputImage image, boolean isLast) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    fullExtractedText.append(visionText.getText()).append("\n");
                    if (isLast) {
                        runOnUiThread(() -> {
                            if (fullExtractedText.toString().trim().isEmpty()) {
                                statusText.setText("No text found.");
                            } else {
                                statusText.setText("Text extracted successfully!");
                            }
                            showInputFields();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (isLast) {
                        runOnUiThread(() -> {
                            statusText.setText("Failed to recognize text.");
                            showInputFields();
                        });
                    }
                });
    }

    private void recognizeTextSync(InputImage image, boolean isLast) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        try {
            com.google.android.gms.tasks.Task<com.google.mlkit.vision.text.Text> task = recognizer.process(image);
            com.google.android.gms.tasks.Tasks.await(task);
            fullExtractedText.append(task.getResult().getText()).append("\n");
            
            if (isLast) {
                runOnUiThread(() -> {
                    statusText.setText("PDF Text extracted!");
                    showInputFields();
                });
            }
        } catch (Exception e) {
            Log.e("ProcessingActivity", "Sync recognition error", e);
        }
    }

    private void copyUriToFile(Uri uri, File destFile) throws IOException {
        try (InputStream is = getContentResolver().openInputStream(uri);
             FileOutputStream os = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    private void showInputFields() {
        progressBar.setVisibility(View.GONE);
        subjectInput.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
    }

    private void generateAIContentAndSave(String subjectName) {
        String topicId = UUID.randomUUID().toString();
        String textToProcess = fullExtractedText.toString().trim();
        
        if (textToProcess.length() < 10) {
            saveTopic(topicId, subjectName, 0, 0);
            return;
        }

        statusText.setText("AI is generating flashcards & quizzes...");
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);

        geminiAI.generateContent(topicId, textToProcess, new GeminiAI.AICallback<GeminiAI.AIResult>() {
            @Override
            public void onSuccess(GeminiAI.AIResult result) {
                new Thread(() -> {
                    try {
                        db.flashcardDao().insertAll(result.flashcards);
                        
                        // Group questions into a single Quiz record for the UI list
                        Quiz quizHeader = new Quiz(
                                UUID.randomUUID().toString(),
                                subjectName + " Quiz",
                                result.quizzes.size(),
                                false,
                                0
                        );
                        quizHeader.setTopicId(topicId);
                        
                        List<Quiz> allQuizData = new ArrayList<>();
                        allQuizData.add(quizHeader);
                        allQuizData.addAll(result.quizzes);
                        
                        db.quizDao().insertAll(allQuizData);
                        
                        runOnUiThread(() -> saveTopic(topicId, subjectName, result.flashcards.size(), 1));
                    } catch (Exception e) {
                        Log.e("ProcessingActivity", "Database error", e);
                        runOnUiThread(() -> Toast.makeText(ProcessingActivity.this, "Error saving to database", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ProcessingActivity.this, "AI Generation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    saveTopic(topicId, subjectName, 0, 0);
                });
            }
        });
    }

    private void saveTopic(String topicId, String subjectName, int flashcardCount, int quizCount) {
        Topic newTopic = new Topic(
                topicId,
                subjectName,
                flashcardCount,
                quizCount,
                "Just now",
                false,
                0,
                imageUriString
        );

        new Thread(() -> {
            try {
                db.topicDao().insert(newTopic);
                runOnUiThread(() -> {
                    Toast.makeText(ProcessingActivity.this, "Topic created!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProcessingActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                Log.e("ProcessingActivity", "Database error in saveTopic", e);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (geminiAI != null) {
            geminiAI.cancel();
        }
    }
}
