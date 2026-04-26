package com.example.flashcardgenerator.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CameraManager {
    private static final String TAG = "CameraManager";
    private Context context;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    
    public interface CameraCallback {
        void onImageCaptured(File imageFile);
        void onError(String error);
    }
    
    public CameraManager(Context context) {
        this.context = context;
    }
    
    public void startCamera(PreviewView previewView, LifecycleOwner lifecycleOwner) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(context);
        
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, previewView, lifecycleOwner);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(context));
    }
    
    private void bindPreview(ProcessCameraProvider cameraProvider, 
                            PreviewView previewView, 
                            LifecycleOwner lifecycleOwner) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();
        
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        
        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
            );
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }
    
    public void captureImage(File outputFile, CameraCallback callback) {
        if (imageCapture == null) {
            if (callback != null) {
                callback.onError("Camera not initialized");
            }
            return;
        }
        
        ImageCapture.OutputFileOptions outputOptions = 
                new ImageCapture.OutputFileOptions.Builder(outputFile).build();
        
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults output) {
                        Log.d(TAG, "Photo capture succeeded");
                        if (callback != null) {
                            callback.onImageCaptured(outputFile);
                        }
                    }
                    
                    @Override
                    public void onError(ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed", exception);
                        if (callback != null) {
                            callback.onError(exception.getMessage());
                        }
                    }
                }
        );
    }
    
    public static Bitmap compressImage(String imagePath, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        
        int scaleFactor = Math.max(
                imageWidth / maxWidth,
                imageHeight / maxHeight
        );
        
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        
        return BitmapFactory.decodeFile(imagePath, options);
    }
    
    public static File saveImageToFile(Context context, Bitmap bitmap) throws IOException {
        File file = new File(context.getCacheDir(), "captured_image_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.close();
        return file;
    }
}
