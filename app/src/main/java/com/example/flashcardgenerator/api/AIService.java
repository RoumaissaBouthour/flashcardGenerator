package com.example.flashcardgenerator.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Headers;

public interface AIService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    Call<AIResponse> generateFlashcardAnswer(@Body AIRequest request);
    
    @Headers("Content-Type: application/json")
    @POST("v1/embeddings")
    Call<AIResponse> getEmbeddings(@Body AIRequest request);
}
