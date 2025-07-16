package com.example.studytracker.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://687319aac75558e273535336.mockapi.io/";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/";
    private static Retrofit retrofit = null;
    private static Retrofit geminiRetrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public static ApiService getGeminiApiService() {
        if (geminiRetrofit == null) {
            geminiRetrofit = new Retrofit.Builder()
                    .baseUrl(GEMINI_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return geminiRetrofit.create(ApiService.class);
    }
}