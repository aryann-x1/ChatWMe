package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register);
        db = new DBHelper(this);

        Button login = findViewById(R.id.loginButton);
        Button register = findViewById(R.id.registerButton);
        EditText user = findViewById(R.id.usernameEditText);
        EditText pass = findViewById(R.id.passwordEditText);

        login.setOnClickListener(v -> {
            if (db.verifyUser(user.getText().toString(), pass.getText().toString())) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, mainPage.class);
                intent.putExtra("username", user.getText().toString());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show();
            }
        });

        register.setOnClickListener(v -> {
            if (db.userExists(user.getText().toString())) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            } else if(!user.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {

                db.registerUser(user.getText().toString(), pass.getText().toString());
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please enter a valid username and password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}