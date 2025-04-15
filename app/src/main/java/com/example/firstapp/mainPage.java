package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import android.app.AlertDialog;
import android.widget.Toast;


import java.util.ArrayList;

public class mainPage extends AppCompatActivity {

    ListView listView;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listView = findViewById(R.id.listView);
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);



        // Add items to the list
        addItem("Group Community");
        addItem("Mental Health Talks");
        addItem("Daily Positivity Circle");
        addItem("Coping with Stress");
        addItem("Productivity & Focus Tips");
        addItem("Share Your Story");
        addItem("Q&A: Ask the Experts");
        String username;
        username = getIntent().getStringExtra("username");

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = items.get(position);

            if (selectedItem.equals("Group Community")) {
                        Intent intent = new Intent(this, ForumActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);


            } else {
                showForumPopup(selectedItem);
            }
        });

        findViewById(R.id.login).setOnClickListener(v -> {
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthActivity.class));
        });

        findViewById(R.id.chatbot).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        findViewById(R.id.camera).setOnClickListener(v ->
                startActivity(new Intent(this, FaceActivity.class)));
        findViewById(R.id.fab).setOnClickListener(v -> showAddItemDialog());


    }
    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Forum Topic");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        final EditText inputTitle = dialogView.findViewById(R.id.inputTitle);
        final EditText inputContent = dialogView.findViewById(R.id.inputContent);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = inputTitle.getText().toString().trim();
            String content = inputContent.getText().toString().trim();

            if (!title.isEmpty()) {
                // You can format title + content however you'd like
                addItem(title + ": " + content);
                Toast.makeText(this, "Topic added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }



    private void showForumPopup(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        String[] links;
        switch (title) {
            case "Mental Health Talks":
                links = new String[] {
                        "https://www.who.int/news-room/fact-sheets/detail/mental-health-strengthening-our-response",
                        "https://www.headspace.com/articles"
                };
                break;
            case "Daily Positivity Circle":
                links = new String[] {
                        "https://positivepsychology.com/",
                        "https://tinybuddha.com/blog-posts/"
                };
                break;
            case "Coping with Stress":
                links = new String[] {
                        "https://www.cdc.gov/mentalhealth/stress-coping/",
                        "https://www.mindtools.com/pages/article/newTCS_00.htm"
                };
                break;
            case "Productivity & Focus Tips":
                links = new String[] {
                        "https://www.notion.so/blog",
                        "https://www.nirandfar.com/indistractable/"
                };
                break;
            case "Share Your Story":
                links = new String[] {
                        "https://themighty.com/",
                        "https://livethroughthis.org/"
                };
                break;
            case "Q&A: Ask the Experts":
                links = new String[] {
                        "https://www.psychologytoday.com/us",
                        "https://www.quora.com/topic/Mental-Health"
                };
                break;
            default:
                links = new String[] { "https://www.google.com" };
                break;
        }

        builder.setItems(links, (dialog, which) -> {
            Intent intent = new Intent(this, ArticleViewActivity.class);
            intent.putExtra("url", links[which]);
            startActivity(intent);
        });

        builder.setPositiveButton("Close", null);
        builder.show();
    }


    // Use a TextView that handles hyperlinks


    private void addItem(String message) {
        items.add(message);          // Add item to the list
        adapter.notifyDataSetChanged(); // Notify adapter to refresh ListView
    }
    public void openBot(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    // New way to handle activity result
    private final ActivityResultLauncher<Intent> faceEmotionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String detectedEmotion = data.getStringExtra("emotion");
                    float confidence = data.getFloatExtra("confidence", 0);
                }
            });

    public void openCam(View view) {
        Intent intent = new Intent(getApplicationContext(), FaceActivity.class);
        faceEmotionLauncher.launch(intent);
    }
}
