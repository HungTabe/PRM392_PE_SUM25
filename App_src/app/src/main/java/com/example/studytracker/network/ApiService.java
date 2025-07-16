package com.example.studytracker.network;

import com.example.studytracker.model.StudySession;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/subjects")
    Call<List<StudySession>> getStudySessions();

    @POST("api/subjects")
    Call<StudySession> createStudySession(@Body StudySession session);

    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    Call<GeminiResponse> getGeminiSuggestions(
            @Header("X-goog-api-key") String apiKey,
            @Body GeminiRequest request
    );
}