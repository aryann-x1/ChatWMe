package com.example.firstapp;

import java.util.List;

public class ChatResponse {
    public List<Choice> choices;

    public static class Choice {
        public String text;
    }
}
