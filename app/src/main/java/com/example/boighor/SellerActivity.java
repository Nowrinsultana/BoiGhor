package com.example.boighor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SellerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Set up navigation for BottomNavigationView
        navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    finish();
                    return true;
                case R.id.navigation_order:
                    // Already on this page, no action needed
                    return true;
                case R.id.navigation_seller:
                    startActivity(new Intent(SellerActivity.this, OrderActivity.class));
                    return true;
                case R.id.navigation_staff:
                    startActivity(new Intent(SellerActivity.this, OrderActivity.class));
                    return true;
            }
            return false;
        });
    }
}
