package com.example.boighor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class dashboardactivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private TextView dashboardHeading, welcomeMessage, userName, userPhone, userAddress, userEmail;
    private Button buttonLogout;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        dashboardHeading = findViewById(R.id.dashboardHeading);
        welcomeMessage = findViewById(R.id.welcomeMessage);
        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        userAddress = findViewById(R.id.userAddress);

        buttonLogout = findViewById(R.id.buttonLogout);

        // Fetch user data from Firestore
        if (currentUser != null) {
            firestore.collection("Users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                String phone = document.getString("contact");
                                String address = document.getString("address");
                                userType = document.getString("user-type");

                                // Set dashboard heading based on user type
                                if (userType != null && userType.equals("seller")) {
                                    dashboardHeading.setText("Seller Dashboard");
                                } else {
                                    dashboardHeading.setText("Dashboard");
                                }
                                // Set user data with default values if null
                                welcomeMessage.setText("Welcome " + (name != null ? name : "User"));
                                userName.setText("Name: " + (name != null ? name : "N/A"));
                                userPhone.setText("Phone: " + (phone != null ? phone : "N/A"));
                                userAddress.setText("Address: " + (address != null ? address : "N/A"));
                            } else {
                                Log.w("DashboardActivity", "No user data found in Firestore for UID: " + currentUser.getUid());
                            }
                        } else {
                            Log.e("DashboardActivity", "Error fetching user data", task.getException());
                            Toast.makeText(dashboardactivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Set the logout button functionality
        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();  // Sign out the user
            Toast.makeText(dashboardactivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            // Redirect to login activity
            Intent intent = new Intent(dashboardactivity.this, loginactivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
