package com.example.flashcardgenerator.api;

import android.util.Log;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class AIClient {
    private static final String TAG = "AIClient";
    private static final String BASE_URL = "https://api.openai.com/";
    private static AIService apiService;
    
    public static AIService getService(String apiKey) {
        if (apiService == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        Request requestWithHeaders = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + apiKey)
                                .header("Content-Type", "application/json")
                                .build();
                        return chain.proceed(requestWithHeaders);
                    });
            
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .build();
            
            apiService = retrofit.create(AIService.class);
        }
        return apiService;
    }
    
    public static void resetService() {
        apiService = null;
    }
}
