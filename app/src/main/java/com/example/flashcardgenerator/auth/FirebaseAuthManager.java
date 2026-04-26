package com.example.flashcardgenerator.auth;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import android.util.Log;

public class FirebaseAuthManager {
    private static final String TAG = "FirebaseAuthManager";
    private FirebaseAuth mAuth;
    
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }
    
    public FirebaseAuthManager() {
        mAuth = FirebaseAuth.getInstance();
    }
    
    public void signUpWithEmail(String email, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (callback != null) {
                            callback.onSuccess(user);
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Sign up failed";
                        Log.e(TAG, "Sign up error: " + error);
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                });
    }
    
    public void signInWithEmail(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (callback != null) {
                            callback.onSuccess(user);
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Sign in failed";
                        Log.e(TAG, "Sign in error: " + error);
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                });
    }
    
    public void signInWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (callback != null) {
                            callback.onSuccess(user);
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Google sign in failed";
                        Log.e(TAG, "Google sign in error: " + error);
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                });
    }
    
    public void resetPassword(String email, AuthCallback callback) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (callback != null) {
                            callback.onSuccess(null);
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Password reset failed";
                        Log.e(TAG, "Password reset error: " + error);
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                });
    }
    
    public void signOut() {
        mAuth.signOut();
    }
    
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }
}
