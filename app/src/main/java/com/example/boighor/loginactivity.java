package com.example.boighor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginactivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView signupLink, joinSellerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailField = findViewById(R.id.loginEmail);
        passwordField = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink); // Sign-up link
        joinSellerButton = findViewById(R.id.joinSellerButton); // Join as seller button

        // Login Button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(loginactivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);
                }
            }
        });

        // Sign-Up link logic
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(loginactivity.this, signupactivity.class);
                signUpIntent.putExtra("user-type", "user"); // passing user type as 'user'
                startActivity(signUpIntent);
            }
        });

        // Join as Seller button logic
        joinSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sellerSignUpIntent = new Intent(loginactivity.this, signupactivity.class);
                sellerSignUpIntent.putExtra("user-type", "seller"); // passing user type as 'seller'
                startActivity(sellerSignUpIntent);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password) //firebase er nijossho code jeta die login/signin kre
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(loginactivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to homepage or user-specific page
                        Intent intent = new Intent(loginactivity.this, dashboardactivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign-in fails, display a message to the user.
                        Toast.makeText(loginactivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if the user is already signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, redirect to homepage
            Intent intent = new Intent(loginactivity.this, dashboardactivity.class);
            startActivity(intent);
            finish();
        }
    }
}
