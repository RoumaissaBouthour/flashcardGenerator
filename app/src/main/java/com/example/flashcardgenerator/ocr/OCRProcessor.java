package com.example.flashcardgenerator.ocr;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

public class OCRProcessor {
    private static final String TAG = "OCRProcessor";
    private TextRecognizer latinTextRecognizer;
    private TextRecognizer chineseTextRecognizer;
    
    public interface OCRCallback {
        void onSuccess(String extractedText);
        void onError(String error);
    }
    
    public OCRProcessor() {
        // Initialize both Latin and Chinese text recognizers
        this.latinTextRecognizer = TextRecognition.getClient(new TextRecognizerOptions.Builder().build());
        this.chineseTextRecognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
    }
    
    public void processImage(Bitmap bitmap, OCRCallback callback) {
        processImageWithLatin(bitmap, callback);
    }
    
    private void processImageWithLatin(Bitmap bitmap, OCRCallback callback) {
        try {
            InputImage image = InputImage.fromBitmap(bitmap, 0);
            latinTextRecognizer.process(image)
                    .addOnSuccessListener(result -> {
                        String extractedText = result.getText();
                        if (!extractedText.isEmpty()) {
                            Log.d(TAG, "Latin text extracted: " + extractedText);
                            if (callback != null) {
                                callback.onSuccess(extractedText);
                            }
                        } else {
                            // Try Chinese if Latin didn't find text
                            processImageWithChinese(bitmap, callback);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Latin text recognition failed", e);
                        // Try Chinese as fallback
                        processImageWithChinese(bitmap, callback);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error processing image", e);
            if (callback != null) {
                callback.onError("Error processing image: " + e.getMessage());
            }
        }
    }
    
    private void processImageWithChinese(Bitmap bitmap, OCRCallback callback) {
        try {
            InputImage image = InputImage.fromBitmap(bitmap, 0);
            chineseTextRecognizer.process(image)
                    .addOnSuccessListener(result -> {
                        String extractedText = result.getText();
                        Log.d(TAG, "Chinese text extracted: " + extractedText);
                        if (callback != null) {
                            if (!extractedText.isEmpty()) {
                                callback.onSuccess(extractedText);
                            } else {
                                callback.onError("No text found in image");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Chinese text recognition failed", e);
                        if (callback != null) {
                            callback.onError("Text recognition failed: " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error processing image with Chinese", e);
            if (callback != null) {
                callback.onError("Error processing image: " + e.getMessage());
            }
        }
    }
    
    public void release() {
        try {
            latinTextRecognizer.close();
            chineseTextRecognizer.close();
        } catch (Exception e) {
            Log.e(TAG, "Error releasing recognizers", e);
        }
    }
}
