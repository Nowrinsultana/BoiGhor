package com.example.boighor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Admin_dashboard extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        buttonLogout = findViewById(R.id.buttonLogout);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Logout button functionality
        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut(); // Sign out the user
            Toast.makeText(Admin_dashboard.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            // Redirect to login activity
            Intent intent = new Intent(Admin_dashboard.this, loginactivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Navigation listener for BottomNavigationView
        navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    // Already on this activity, no action needed
                    return true;
                case R.id.navigation_order:
                    startActivity(new Intent(Admin_dashboard.this, OrderActivity.class));
                    return true;
                case R.id.navigation_seller:
                    startActivity(new Intent(Admin_dashboard.this, SellerActivity.class));
                    return true;
                case R.id.navigation_staff:
                    startActivity(new Intent(Admin_dashboard.this, staffActivity.class));
                    return true;
            }
            return false;
        });
    }
}
