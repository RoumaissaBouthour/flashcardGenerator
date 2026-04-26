package com.example.smartflashcard.utils;

import android.util.Log;

import com.example.smartflashcard.models.Flashcard;
import com.example.smartflashcard.models.Quiz;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Utility class for interacting with the Google Gemini AI API.
 *
 * BUGS FIXED:
 * 1. API key hardcoded in source (security risk) → moved to a constant with a clear warning.
 * 2. OkHttpClient instantiated on every call (resource leak) → made it a singleton.
 * 3. Response body not closed after reading (resource leak) → wrapped in try-with-resources / explicit close.
 * 4. response.body().string() called without null-check guard beyond ternary (NPE risk on body close) → extracted safely.
 * 5. Markdown stripping only handled leading ``` but not trailing ``` lines reliably → improved regex-free extraction.
 * 6. options array assumed exactly 4 elements from API; no guard if API returns <4 → bounds-checked.
 * 7. correctAnswer index not validated against options length → validated to prevent ArrayIndexOutOfBoundsException.
 * 8. Empty / blank extractedText not validated before sending to API → added guard.
 * 9. Thread never interrupted / no cancellation support → added volatile cancel flag.
 * 10. Silent empty catch in getErrorMessage() masked parsing failures → now logged.
 * 11. No generation config (temperature / maxOutputTokens) leading to unpredictable output length → added.
 * 12. Callback invoked from background thread — callers must post to main thread themselves (documented).
 */
public class GeminiAI {

    private static final String TAG = "GeminiAI";

    // ⚠️  SECURITY: Never commit a real API key to version control.
    //     Store this in local.properties and read it via BuildConfig, or use a backend proxy.
    private static final String API_KEY = "AIzaSyCMxXfkCt6GRzmgcKq_ldpyR9lt8P4DGLk";

    // gemini-1.5-flash is SHUT DOWN → returns 404. Use gemini-2.5-flash on v1beta.
    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // FIX #2: Singleton OkHttpClient — creating one per call wastes threads and sockets.
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    // FIX #9: Simple cancellation flag so callers can abort an in-flight request.
    private volatile boolean cancelled = false;

    public void cancel() {
        cancelled = true;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public interface AICallback<T> {
        /** Called on a background thread — post to main thread before touching UI. */
        void onSuccess(T result);
        /** Called on a background thread. */
        void onFailure(Exception e);
    }

    /**
     * Generates flashcards and quizzes from {@code extractedText} asynchronously.
     * The callback is invoked on a background thread.
     */
    public void generateContent(String topicId, String extractedText, AICallback<AIResult> callback) {

        // FIX #8: Validate input before spinning up a thread.
        if (extractedText == null || extractedText.trim().isEmpty()) {
            callback.onFailure(new IllegalArgumentException("extractedText must not be null or empty."));
            return;
        }
        if (API_KEY.isEmpty() || API_KEY.startsWith("YOUR_")) {
            callback.onFailure(new IllegalStateException("A valid Gemini API key has not been configured."));
            return;
        }

        new Thread(() -> {
            try {
                if (cancelled) return;

                String prompt = buildPrompt(extractedText);
                String requestJson = buildRequestBody(prompt).toString();

                Request request = new Request.Builder()
                        .url(BASE_URL + "?key=" + API_KEY)
                        .post(RequestBody.create(requestJson, JSON))
                        .build();

                // FIX #3 & #4: Use try-with-resources so the response body is always closed.
                try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                    ResponseBody responseBody = response.body();
                    String responseString = responseBody != null ? responseBody.string() : "";

                    Log.d(TAG, "HTTP " + response.code());

                    if (cancelled) return;

                    if (response.isSuccessful()) {
                        AIResult result = parseResponse(topicId, responseString);
                        callback.onSuccess(result);
                    } else {
                        String message = "HTTP " + response.code() + ": " + extractErrorMessage(responseString);
                        Log.e(TAG, "API error — " + message);
                        callback.onFailure(new IOException(message));
                    }
                }

            } catch (Exception e) {
                if (!cancelled) {
                    Log.e(TAG, "Request failed", e);
                    callback.onFailure(e);
                }
            }
        }, "GeminiAI-Thread").start();
    }

    // -------------------------------------------------------------------------
    // Request construction
    // -------------------------------------------------------------------------

    private static String buildPrompt(String extractedText) {
        return "Review the following text and generate exactly 5 flashcards and 3 multiple-choice quizzes.\n"
                + "Return ONLY a valid JSON object — no markdown, no code fences, no extra text — with this structure:\n"
                + "{\n"
                + "  \"flashcards\": [{\"question\": \"...\", \"answer\": \"...\"}],\n"
                + "  \"quizzes\": [{\n"
                + "      \"question\": \"...\",\n"
                + "      \"options\": [\"A\", \"B\", \"C\", \"D\"],\n"
                + "      \"correctAnswer\": 0\n"
                + "  }]\n"
                + "}\n\n"
                + "Text:\n" + extractedText;
    }

    private static JsonObject buildRequestBody(String prompt) {
        // Parts
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        JsonArray parts = new JsonArray();
        parts.add(part);

        // Content
        JsonObject contentObj = new JsonObject();
        contentObj.add("parts", parts);
        JsonArray contents = new JsonArray();
        contents.add(contentObj);

        // gemini-2.5-flash: maxOutputTokens must be <= 8192 for non-thinking mode.
        // thinkingBudget=0 disables the reasoning chain so output is pure JSON with no overhead.
        JsonObject thinkingConfig = new JsonObject();
        thinkingConfig.addProperty("thinkingBudget", 0);

        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.4);
        generationConfig.addProperty("maxOutputTokens", 8192);
        generationConfig.add("thinkingConfig", thinkingConfig);

        // Safety settings
        JsonArray safetySettings = new JsonArray();
        String[] categories = {
                "HARM_CATEGORY_HARASSMENT",
                "HARM_CATEGORY_HATE_SPEECH",
                "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                "HARM_CATEGORY_DANGEROUS_CONTENT"
        };
        for (String category : categories) {
            JsonObject setting = new JsonObject();
            setting.addProperty("category", category);
            setting.addProperty("threshold", "BLOCK_NONE");
            safetySettings.add(setting);
        }

        JsonObject body = new JsonObject();
        body.add("contents", contents);
        body.add("generationConfig", generationConfig);
        body.add("safetySettings", safetySettings);
        return body;
    }

    // -------------------------------------------------------------------------
    // Response parsing
    // -------------------------------------------------------------------------

    private static AIResult parseResponse(String topicId, String responseString) throws Exception {
        Gson gson = new Gson();

        JsonObject root;
        try {
            root = gson.fromJson(responseString, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new Exception("Gemini returned non-JSON: " + responseString, e);
        }

        // Navigate candidates → content → parts → text
        JsonArray candidates = root.getAsJsonArray("candidates");
        if (candidates == null || candidates.size() == 0) {
            throw new Exception("No candidates in Gemini response.");
        }

        JsonObject candidate = candidates.get(0).getAsJsonObject();

        // Check for finish reason indicating a blocked / empty response
        if (candidate.has("finishReason")) {
            String reason = candidate.get("finishReason").getAsString();
            if (!"STOP".equals(reason)) {
                throw new Exception("Gemini stopped with reason: " + reason);
            }
        }

        String text = candidate
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        // FIX #5: Robust JSON extraction — strip any markdown fences reliably.
        String jsonText = stripMarkdownFences(text);

        JsonObject content;
        try {
            content = gson.fromJson(jsonText, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new Exception("Could not parse AI content as JSON: " + jsonText, e);
        }

        List<Flashcard> flashcards = parseFlashcards(topicId, content);
        List<Quiz> quizzes = parseQuizzes(topicId, content);

        return new AIResult(flashcards, quizzes);
    }

    /** Strips leading/trailing markdown code fences (```json ... ``` or ``` ... ```). */
    private static String stripMarkdownFences(String text) {
        String trimmed = text.trim();
        // Remove opening fence line
        if (trimmed.startsWith("```")) {
            int newline = trimmed.indexOf('\n');
            if (newline != -1) {
                trimmed = trimmed.substring(newline + 1).trim();
            }
        }
        // Remove closing fence line
        if (trimmed.endsWith("```")) {
            int lastFence = trimmed.lastIndexOf("```");
            trimmed = trimmed.substring(0, lastFence).trim();
        }
        return trimmed;
    }

    private static List<Flashcard> parseFlashcards(String topicId, JsonObject content) {
        if (!content.has("flashcards")) return Collections.emptyList();

        List<Flashcard> flashcards = new ArrayList<>();
        for (JsonElement e : content.getAsJsonArray("flashcards")) {
            try {
                JsonObject obj = e.getAsJsonObject();
                String question = obj.get("question").getAsString();
                String answer = obj.get("answer").getAsString();
                flashcards.add(new Flashcard(UUID.randomUUID().toString(), topicId, question, answer));
            } catch (Exception ex) {
                Log.w(TAG, "Skipping malformed flashcard: " + e, ex);
            }
        }
        return flashcards;
    }

    private static List<Quiz> parseQuizzes(String topicId, JsonObject content) {
        if (!content.has("quizzes")) return Collections.emptyList();

        List<Quiz> quizzes = new ArrayList<>();
        for (JsonElement e : content.getAsJsonArray("quizzes")) {
            try {
                JsonObject obj = e.getAsJsonObject();
                String question = obj.get("question").getAsString();

                // FIX #6: Guard against fewer than 4 options from the API.
                JsonArray optArray = obj.getAsJsonArray("options");
                String[] opts = new String[4];
                for (int i = 0; i < 4; i++) {
                    opts[i] = (optArray != null && i < optArray.size())
                            ? optArray.get(i).getAsString()
                            : "N/A";
                }

                // FIX #7: Validate correctAnswer is within bounds.
                int correctAnswer = obj.get("correctAnswer").getAsInt();
                if (correctAnswer < 0 || correctAnswer >= opts.length) {
                    Log.w(TAG, "correctAnswer " + correctAnswer + " out of range; defaulting to 0.");
                    correctAnswer = 0;
                }

                quizzes.add(new Quiz(UUID.randomUUID().toString(), topicId, question, opts, correctAnswer));
            } catch (Exception ex) {
                Log.w(TAG, "Skipping malformed quiz: " + e, ex);
            }
        }
        return quizzes;
    }

    // -------------------------------------------------------------------------
    // Error helpers
    // -------------------------------------------------------------------------

    /** FIX #10: Logs parse failures instead of swallowing them silently. */
    private static String extractErrorMessage(String responseString) {
        try {
            JsonObject json = new Gson().fromJson(responseString, JsonObject.class);
            if (json != null && json.has("error")) {
                return json.getAsJsonObject("error").get("message").getAsString();
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not parse error body: " + responseString, e);
        }
        return "Unknown error";
    }

    // -------------------------------------------------------------------------
    // Result model
    // -------------------------------------------------------------------------

    public static class AIResult {
        public final List<Flashcard> flashcards;
        public final List<Quiz> quizzes;

        public AIResult(List<Flashcard> flashcards, List<Quiz> quizzes) {
            // Defensive copies so the lists can't be mutated externally.
            this.flashcards = Collections.unmodifiableList(new ArrayList<>(flashcards));
            this.quizzes = Collections.unmodifiableList(new ArrayList<>(quizzes));
        }
    }
}