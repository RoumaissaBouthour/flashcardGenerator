package com.example.flashcardgenerator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flashcardgenerator.database.AppDatabase;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize database
        AppDatabase.getInstance(this);
    }
}
