package com.example.boighor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signupactivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailField, passwordField, nameField, addressField, contactField;
    private Button signupButton;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Retrieve user type from Intent (from loginactivity)
        userType = getIntent().getStringExtra("user-type");

        // UI elements
        emailField = findViewById(R.id.signupEmail);
        passwordField = findViewById(R.id.signupPassword);
        nameField = findViewById(R.id.signupName);
        addressField = findViewById(R.id.signupAddress);
        contactField = findViewById(R.id.signupContact);
        signupButton = findViewById(R.id.signupButton);

        // Set the Sign-Up button click listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                String name = nameField.getText().toString().trim();
                String address = addressField.getText().toString().trim();
                String contact = contactField.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || name.isEmpty() || address.isEmpty() || contact.isEmpty()) {
                    Toast.makeText(signupactivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    createAccount(email, password, name, address, contact);
                }
            }
        });
    }

    private void createAccount(String email, String password, String name, String address, String contact) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Account creation successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Log the user details


//                         Save the user details in Firestore
                        saveUserDetails(user.getUid(), name, address, contact, userType);

                        Toast.makeText(signupactivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                        // Redirect to login or homepage activity
                        startActivity(new Intent(signupactivity.this, dashboardactivity.class));
                        finish();
                    } else {
                        // If sign up fails, display an error message
                        Toast.makeText(signupactivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetails(String userId, String name, String address, String contact, String userType) {
        // Create a map to store user details in Firestore

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("address", address);
        user.put("contact", contact);
        user.put("userType", userType);

        // Save the data in Firestore under the "Users" collection
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.set(user)//data create hoise
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User details saved successfully!");
                    Toast.makeText(signupactivity.this, "User details saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving user details", e);
                    Toast.makeText(signupactivity.this, "Error saving user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}