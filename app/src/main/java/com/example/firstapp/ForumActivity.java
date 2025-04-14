package com.example.firstapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForumActivity extends AppCompatActivity {

    EditText messageInput;
    Button sendButton;
    LinearLayout chatContainer;
    ScrollView scrollView;
    DatabaseReference chatRef;
    String username = "Anonymous";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_chat);

        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.chatScrollView);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "Anonymous";

        chatRef = FirebaseDatabase.getInstance().getReference("forumChat");

        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                String id = chatRef.push().getKey();
                chatRef.child(id).setValue(username + ": " + msg);
                messageInput.setText("");
            }
        });

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatContainer.removeAllViews();
                Log.d("ChatDebug", "Children count: " + snapshot.getChildrenCount());
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String message = snap.getValue(String.class);
                    Log.d("ChatDebug", "Message received: " + message);

                    TextView textView1 = new TextView(ForumActivity.this);
                    TextView textView2 = new TextView(ForumActivity.this);
                    textView1.setText(message.split(":")[0]);
                    textView1.setTextSize(11f);
                    textView1.setPadding(16, 8, 16, 0);
                    textView2.setText(message.split(":")[1]);
                    textView2.setTextSize(16f);
                    textView2.setPadding(16, 4, 16, 8);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 5, 0, 5);
                    params.gravity = message.split(":")[0].equals(username) ? Gravity.END : Gravity.START;
                    textView1.setLayoutParams(params);
                    textView2.setLayoutParams(params);

                    textView2.setBackgroundResource(R.drawable.user_bubble);
                    if(!message.split(":")[0].equals(username))
                        chatContainer.addView(textView1);
                    chatContainer.addView(textView2);
                }
                scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // cry in silence
            }
        });
    }
}