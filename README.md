# Flashcard Generator - Android App

An intelligent Android application that generates smart flashcards using camera, OCR, and AI technologies.

## Features

### 1. **Room Database** 📦
- Local SQLite database for offline flashcard storage
- Efficient data management with Room DAO
- User-specific flashcard organization
- Category-based organization
- Performance metrics tracking (review count, difficulty level)

### 2. **Firebase Authentication** 🔐
- Email/Password authentication
- Google Sign-In integration
- Password reset functionality
- Secure user session management

### 3. **Camera & OCR** 📸
- Real-time camera preview
- Text extraction from images using ML Kit
- Support for Latin and Chinese text recognition
- High-quality image capture

### 4. **AI API Integration** 🤖
- Integration with OpenAI API (or custom AI provider)
- Automatic answer generation
- Question generation from text
- Customizable AI parameters (temperature, max tokens)

## Project Structure

```
app/src/main/java/com/example/flashcardgenerator/
├── database/
│   ├── Flashcard.java          # Flashcard entity
│   ├── FlashcardDao.java        # Database access object
│   └── AppDatabase.java         # Room database instance
├── auth/
│   └── FirebaseAuthManager.java # Authentication management
├── camera/
│   └── CameraManager.java       # Camera functionality
├── ocr/
│   └── OCRProcessor.java        # OCR text extraction
├── api/
│   ├── AIService.java           # Retrofit service interface
│   ├── AIRequest.java           # API request model
│   ├── AIResponse.java          # API response model
│   └── AIClient.java            # Retrofit client setup
├── repository/
│   └── FlashcardRepository.java # Data repository layer
└── MainActivity.java            # Main activity
```

## Dependencies

### Core Android
- AndroidX AppCompat
- Material Components
- ConstraintLayout

### Database
- Room Database (v2.6.0)
- Room Coroutines Support

### Firebase
- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Google Play Services Auth

### Camera & Image Processing
- AndroidX Camera Core/Camera2
- AndroidX Camera Lifecycle
- Camera View
- Google ML Kit Text Recognition

### Networking & API
- Retrofit 2
- OkHttp Logging Interceptor
- Gson for JSON conversion

### Coroutines & Lifecycle
- Kotlin Coroutines
- AndroidX Lifecycle

### Image Loading
- Glide

## Setup Instructions

### 1. Clone Repository
```bash
git clone https://github.com/RoumaissaBouthour/flashcardGenerator.git
cd flashcardGenerator
```

### 2. Configure Firebase
- Download `google-services.json` from Firebase Console
- Place it in `app/` directory
- Update Firebase project credentials in Firebase Console

### 3. Set Up AI API
- Get API key from OpenAI (or your AI provider)
- Update `AIClient.java` with your base URL
- Store API key securely (consider using BuildConfig)

### 4. Build and Run
```bash
./gradlew build
./gradlew installDebug
```

## Usage

### Authentication
```java
FirebaseAuthManager authManager = new FirebaseAuthManager();
authManager.signUpWithEmail(email, password, new FirebaseAuthManager.AuthCallback() {
    @Override
    public void onSuccess(FirebaseUser user) {
        // Handle successful sign-up
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

### Camera & OCR
```java
CameraManager cameraManager = new CameraManager(context);
OCRProcessor ocrProcessor = new OCRProcessor();

// Capture image
cameraManager.captureImage(outputFile, new CameraManager.CameraCallback() {
    @Override
    public void onImageCaptured(File imageFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
        ocrProcessor.processImage(bitmap, new OCRProcessor.OCRCallback() {
            @Override
            public void onSuccess(String extractedText) {
                // Use extracted text to generate flashcard
            }
        });
    }
});
```

### Create Flashcard
```java
FlashcardRepository repository = new FlashcardRepository(context);
Flashcard flashcard = new Flashcard(question, answer, category, userId);

repository.insertFlashcard(flashcard, new FlashcardRepository.RepositoryCallback<Long>() {
    @Override
    public void onSuccess(Long id) {
        // Flashcard created successfully
    }
});
```

### AI-Generated Answers
```java
AIService aiService = AIClient.getService(apiKey);
List<AIRequest.Message> messages = new ArrayList<>();
messages.add(new AIRequest.Message("user", "What is photosynthesis?"));

AIRequest request = new AIRequest("gpt-3.5-turbo", messages, 0.7f, 150);
aiService.generateFlashcardAnswer(request).enqueue(new Callback<AIResponse>() {
    @Override
    public void onResponse(Call<AIResponse> call, Response<AIResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String answer = response.body().getChoices().get(0).getMessage().getContent();
        }
    }
});
```

## Permissions Required

- `INTERNET` - API calls and Firebase
- `CAMERA` - Image capture
- `READ_EXTERNAL_STORAGE` - Access stored images
- `WRITE_EXTERNAL_STORAGE` - Save captured images

## Security Considerations

1. Store API keys in `BuildConfig` or secure configuration
2. Use Firebase security rules for Firestore access
3. Implement proper error handling and logging
4. Validate all user inputs
5. Use HTTPS for all API communications

## Future Enhancements

- Spaced repetition algorithm
- Flashcard statistics and analytics
- Sync across devices using Firestore
- Support for multiple languages
- Audio pronunciation support
- Collaborative study groups
- Export/Import functionality

## Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions, please open an issue on GitHub or contact the development team.

---

**Happy Learning! 📚✨**
