package com.example.firstapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import java.util.List;

public interface EmotionApi {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"
    })
    @POST("models/michellejieli/emotion_text_classifier")
    Call<List<List<EmotionResponse>>> analyzeEmotion(@Body EmotionRequest request);
}
