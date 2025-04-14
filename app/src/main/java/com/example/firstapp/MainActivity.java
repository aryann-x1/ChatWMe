package com.example.firstapp;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Response;
import com.google.gson.Gson;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout chatContainer;
    private Retrofit retrofit;
    private OpenAIApi openAIApi;
    private Retrofit emotionRetrofit;
    private EmotionApi emotionApi;

    String allResponses = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatContainer = findViewById(R.id.chatContainer);
        ScrollView chatScrollView = findViewById(R.id.chatScrollView);
        EditText messageInput = findViewById(R.id.messageInput);
        ImageButton sendButton = findViewById(R.id.sendButton);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openAIApi = retrofit.create(OpenAIApi.class);

        emotionRetrofit = new Retrofit.Builder()
                .baseUrl("https://api-inference.huggingface.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        emotionApi = emotionRetrofit.create(EmotionApi.class);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = messageInput.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    addMessageToChat(userMessage, true);
                    messageInput.setText("");
                    analyzeEmotionBeforeOpenAI(userMessage, chatContainer,0);
                }
            }

        });
    }

    private void addMessageToChat(String message, boolean isUser) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextSize(16f);
        textView.setPadding(16, 8, 16, 8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);
        params.gravity = isUser ? Gravity.END : Gravity.START;
        textView.setLayoutParams(params);
        textView.setBackgroundResource(isUser ? R.drawable.user_bubble : R.drawable.bot_bubble);
        chatContainer.addView(textView);
        if (isUser)
            allResponses += message + "\n";
    }

    private void getBotResponse(String userMessage, String detectedEmotion, LinearLayout chatContainer) {
        List<OpenAIRequest.Message> messages = new ArrayList<>();
        messages.add(new OpenAIRequest.Message("system", "You are a helpful assistant that responds based on user emotions. Adjust your tone accordingly.If it is not strictly based on user's mental health or emotions, don't answer the question and say that you are not capable of answering the question."));
        messages.add(new OpenAIRequest.Message("user", "Emotion detected: " + detectedEmotion + ". Message: " + userMessage));
        OpenAIRequest request = new OpenAIRequest("gpt-3.5-turbo", messages);

        openAIApi.generateResponse(request).enqueue(new Callback<OpenAIResponse>() {
            @Override
            public void onResponse(Call<OpenAIResponse> call, Response<OpenAIResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getChoices().isEmpty()) {
                    String botReply = response.body().getChoices().get(0).getMessage().getContent();
                    addMessageToChat(botReply, false);
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.errorBody());
                    addMessageToChat("Sorry, I couldn't generate a response.", false);
                }
            }

            @Override
            public void onFailure(Call<OpenAIResponse> call, Throwable t) {
                Log.e("API_ERROR", "Request failed", t);
                addMessageToChat("Sorry, there was an error processing your request.", false);
            }
        });
    }

    private void analyzeEmotionBeforeOpenAI(String userMessage, LinearLayout chatContainer, int retryCount) {
        EmotionRequest emotionRequest = new EmotionRequest(allResponses);
        emotionApi.analyzeEmotion(emotionRequest).enqueue(new Callback<List<List<EmotionResponse>>>() {
            @Override
            public void onResponse(Call<List<List<EmotionResponse>>> call, Response<List<List<EmotionResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Get the first array of emotion objects
                    List<EmotionResponse> emotions = response.body().get(0);
                    if (!emotions.isEmpty()) {
                        // Log all emotions
                        for (EmotionResponse emotion : emotions) {
                            String emotionLabel = emotion.getEmotion();
                            float confidence = emotion.getConfidence();
                            Log.d("EMOTION_API", "Emotion: " + emotionLabel + ", Confidence: " + (confidence * 100) + "%");
                        }
                        // Get the bot response based on the primary emotion (first in the list)
                        String primaryEmotion = emotions.get(0).getEmotion();
                        getBotResponse(userMessage, primaryEmotion, chatContainer);

                    }
                } else if (response.code() == 503 && retryCount < 3) {
                    long delay = (long) Math.pow(2, retryCount) * 1000;
                    new Handler().postDelayed(() -> analyzeEmotionBeforeOpenAI(userMessage, chatContainer, retryCount + 1), delay);
                } else {
                    Log.e("EMOTION_API_ERROR", "Response failed: " + response.errorBody());
                    getBotResponse(userMessage, "neutral", chatContainer);
                }

            }

            @Override
            public void onFailure(Call<List<List<EmotionResponse>>> call, Throwable t) {
                Log.e("EMOTION_API_ERROR", "Request failed", t);
                getBotResponse(allResponses, "neutral", chatContainer);
            }
        });
    }
}
