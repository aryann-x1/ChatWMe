package com.example.firstapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIApi {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"
    })
    @POST("v1/chat/completions")
    Call<OpenAIResponse> generateResponse(@Body OpenAIRequest request);

}
