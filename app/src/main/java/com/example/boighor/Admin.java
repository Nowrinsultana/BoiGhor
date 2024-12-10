package com.example.boighor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.boighor.databinding.ActivityAdminBinding;

public class Admin extends AppCompatActivity {

    ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set default fragment
        replaceFragment(new HomeFragment());

        // Set listener for bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.order) {
                replaceFragment(new OrderFragment());
            } else if (id == R.id.seller) {
                replaceFragment(new SellerFragment());
            } else if (id == R.id.staff) {
                replaceFragment(new StaffFragment());
            } else {
                return false; // If no case matches
            }

            return true; // Successfully handled
        });

    }

    private void replaceFragment(Fragment fragment) {
        // Use support FragmentManager for transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
