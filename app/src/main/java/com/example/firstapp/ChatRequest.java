package com.example.firstapp;

import java.util.List;

public class ChatRequest {
    private String model;
    private String prompt;
    private int max_tokens;

    public ChatRequest(String model, String prompt, int max_tokens) {
        this.model = model;
        this.prompt = prompt;
        this.max_tokens = max_tokens;
    }
}
