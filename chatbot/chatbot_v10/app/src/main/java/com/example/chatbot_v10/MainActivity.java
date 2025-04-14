package com.example.chatbot_v10;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    OpenAIApi openAIApi = retrofit.create(OpenAIApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ScrollView chatScrollView = findViewById(R.id.chatScrollView);
        LinearLayout chatContainer = findViewById(R.id.chatContainer);
        EditText messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = messageInput.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    addMessageToChat(userMessage, true, chatContainer);
                    messageInput.setText("");
                    getBotResponse(userMessage, chatContainer);
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    private void addMessageToChat(String message, boolean isUser, LinearLayout chatContainer) {
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setPadding(16, 8, 16, 8);
        messageView.setBackgroundResource(isUser ? R.drawable.user_bubble : R.drawable.bot_bubble);
        chatContainer.addView(messageView);
    }

    private void getBotResponse(String userMessage, LinearLayout chatContainer) {
        ChatRequest request = new ChatRequest(
                "text-davinci-003", // Model name
                userMessage,
                150 // Max tokens (response length)
        );

        openAIApi.generateResponse(request).enqueue(new retrofit2.Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, retrofit2.Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String botMessage = response.body().choices.get(0).text.trim();
                    addMessageToChat(botMessage, false, chatContainer);
                } else {
                    addMessageToChat("Oops! Something went wrong.", false, chatContainer);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                addMessageToChat("Failed to connect to the server.", false, chatContainer);
            }
        });
    }

}