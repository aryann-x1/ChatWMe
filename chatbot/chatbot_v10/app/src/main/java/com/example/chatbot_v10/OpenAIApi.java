package com.example.chatbot_v10;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIApi {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"
    })
    @POST("v1/completions")
    Call<ChatResponse> generateResponse(@Body ChatRequest request);
}
