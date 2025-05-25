package com.example.votingsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import com.example.votingsystem.fragment.AdminCandidatesFragment;
import com.example.votingsystem.fragment.AdminVoteResultsFragment;
import com.example.votingsystem.fragment.VoteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Load UserCandidatesFragment into the frame layout
        if (savedInstanceState == null) {
            loadFragment(new AdminCandidatesFragment());
        }
        bottomNav = findViewById(R.id.bottom_navigation_admin);

        // Load default fragment
        loadFragment(new AdminCandidatesFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.nav_candidates) {
                selectedFragment = new AdminCandidatesFragment();
            } else if (id == R.id.nav_vote) {
                selectedFragment = new VoteFragment();
            }else if (id == R.id.nav_result) {
                selectedFragment = new AdminVoteResultsFragment();
            } else if (id == R.id.nav_logout) {
                // Show logout confirmation dialog
                new AlertDialog.Builder(AdminHomeActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Proceed with logout
                            startActivity(new Intent(AdminHomeActivity.this, LoginActivity.class));
                            finish(); // Prevent going back
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                return true; // Don't change fragment on logout click
            }

            return loadFragment(selectedFragment);
        });

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.admin_content_frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}