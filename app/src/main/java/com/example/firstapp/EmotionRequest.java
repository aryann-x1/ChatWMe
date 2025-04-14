package com.example.firstapp;

import java.util.Collections;
import java.util.List;

public class EmotionRequest {
    private List<String> inputs;

    public EmotionRequest(String text) {
        this.inputs = Collections.singletonList(text);
    }
}
