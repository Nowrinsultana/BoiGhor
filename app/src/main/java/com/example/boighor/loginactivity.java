package com.example.boighor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class loginactivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView signupLink, joinSellerButton;
    private LinearLayout googleSignInButton;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private static final String TAG = "loginactivity";
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.loginEmail);
        passwordField = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink);
        joinSellerButton = findViewById(R.id.joinSellerButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        // Initialize Google Sign-In client
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id)) // Add this to your strings.xml
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .build();

        // Email login
        loginButton.setOnClickListener(view -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(loginactivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Sign-up link
        signupLink.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(loginactivity.this, signupactivity.class);
            signUpIntent.putExtra("user-type", "user");
            startActivity(signUpIntent);
        });

        // Join as seller button
        joinSellerButton.setOnClickListener(view -> {
            Intent sellerSignUpIntent = new Intent(loginactivity.this, signupactivity.class);
            sellerSignUpIntent.putExtra("user-type", "seller");
            startActivity(sellerSignUpIntent);
        });

        // Google Sign-In button
        googleSignInButton.setOnClickListener(view -> signInWithGoogle());
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(loginactivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(loginactivity.this, dashboardactivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(loginactivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), GOOGLE_SIGN_IN_REQUEST_CODE, null, 0, 0, 0);
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting Google Sign-In", e);
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Google Sign-In failed", e);
                    Toast.makeText(loginactivity.this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error retrieving Google Sign-In credential", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Extract user details
                            String userId = user.getUid();
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            String userType = "user"; // Default user type

                            // Store user details in Firestore
                            saveUserDetails(userId, name, "", email, "", userType);
                        }

                        Toast.makeText(loginactivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to the dashboard
                        Intent intent = new Intent(loginactivity.this, dashboardactivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Google Sign-In failed", task.getException());
                        Toast.makeText(loginactivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetails(String userId, String name, String address, String email, String contact, String userType) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("address", address);
        user.put("email", email);
        user.put("contact", contact);
        user.put("userType", userType);

        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User details saved successfully!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving user details", e);
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(loginactivity.this, dashboardactivity.class);
            startActivity(intent);
            finish();
        }
    }
}
