package com.example.firstapp;

public class ForumPost {
    public String username;
    public String message;
    public long timestamp;

    public ForumPost() {} // Required for Firebase

    public ForumPost(String username, String message, long timestamp) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
    }
}
