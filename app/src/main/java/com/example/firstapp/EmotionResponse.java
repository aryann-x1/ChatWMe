package com.example.firstapp;

import com.google.gson.annotations.SerializedName;

public class EmotionResponse {
    @SerializedName("label")
    private String emotion;



    @SerializedName("score")
    private float confidence;

    public String getEmotion() {
        return emotion;
    }

    public float getConfidence() {
        return confidence;
    }
}
