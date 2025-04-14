package com.example.chatbot_v10;

import java.util.List;

public class ChatResponse {
    public List<Choice> choices;

    public static class Choice {
        public String text;
    }
}
