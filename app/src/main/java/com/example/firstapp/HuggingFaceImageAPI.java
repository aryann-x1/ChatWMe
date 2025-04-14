package com.example.firstapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface HuggingFaceImageAPI {
    @Headers({
            "Authorization: Bearer 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",
            "Content-Type: application/json"
    })
    @POST("models/microsoft/vision-emotion")
    Call<List<EmotionResponse>> analyzeEmotion(@Body EmotionRequest request);
}