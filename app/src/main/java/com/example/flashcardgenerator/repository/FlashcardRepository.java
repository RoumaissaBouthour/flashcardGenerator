package com.example.flashcardgenerator.repository;

import android.content.Context;
import android.util.Log;
import com.example.flashcardgenerator.database.AppDatabase;
import com.example.flashcardgenerator.database.Flashcard;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlashcardRepository {
    private static final String TAG = "FlashcardRepository";
    private AppDatabase database;
    private ExecutorService executorService;
    
    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
    
    public FlashcardRepository(Context context) {
        this.database = AppDatabase.getInstance(context);
        this.executorService = Executors.newFixedThreadPool(2);
    }
    
    public void insertFlashcard(Flashcard flashcard, RepositoryCallback<Long> callback) {
        executorService.execute(() -> {
            try {
                long id = database.flashcardDao().insertFlashcard(flashcard);
                Log.d(TAG, "Flashcard inserted with ID: " + id);
                if (callback != null) {
                    callback.onSuccess(id);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error inserting flashcard", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
    
    public void updateFlashcard(Flashcard flashcard, RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            try {
                flashcard.updatedAt = System.currentTimeMillis();
                database.flashcardDao().updateFlashcard(flashcard);
                Log.d(TAG, "Flashcard updated: " + flashcard.id);
                if (callback != null) {
                    callback.onSuccess(null);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error updating flashcard", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
    
    public void deleteFlashcard(Flashcard flashcard, RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            try {
                database.flashcardDao().deleteFlashcard(flashcard);
                Log.d(TAG, "Flashcard deleted: " + flashcard.id);
                if (callback != null) {
                    callback.onSuccess(null);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting flashcard", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
    
    public void getFlashcardById(int id, RepositoryCallback<Flashcard> callback) {
        executorService.execute(() -> {
            try {
                Flashcard flashcard = database.flashcardDao().getFlashcardById(id);
                if (callback != null) {
                    callback.onSuccess(flashcard);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching flashcard", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
    
    public void getAllFlashcardsForUser(String userId, RepositoryCallback<List<Flashcard>> callback) {
        executorService.execute(() -> {
            try {
                List<Flashcard> flashcards = database.flashcardDao().getAllFlashcardsForUser(userId);
                Log.d(TAG, "Fetched " + flashcards.size() + " flashcards for user: " + userId);
                if (callback != null) {
                    callback.onSuccess(flashcards);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching flashcards", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
    
    public void getFlashcardsByCategory(String userId, String category, RepositoryCallback<List<Flashcard>> callback) {
        executorService.execute(() -> {
            try {
                List<Flashcard> flashcards = database.flashcardDao().getFlashcardsByCategory(userId, category);
                if (callback != null) {
                    callback.onSuccess(flashcards);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching flashcards by category", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
    
    public void getDifficultFlashcards(String userId, RepositoryCallback<List<Flashcard>> callback) {
        executorService.execute(() -> {
            try {
                List<Flashcard> flashcards = database.flashcardDao().getDifficultFlashcards(userId);
                if (callback != null) {
                    callback.onSuccess(flashcards);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching difficult flashcards", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
    
    public void release() {
        executorService.shutdown();
    }
}
