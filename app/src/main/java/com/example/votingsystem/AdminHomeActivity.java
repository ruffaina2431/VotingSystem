package com.example.votingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.votingsystem.fragment.AdminCandidatesFragment;
import com.example.votingsystem.fragment.CandidateFragment;
import com.example.votingsystem.fragment.VoteFragment;
import com.example.votingsystem.fragment.VoteResultsFragment;
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
            } else if (id == R.id.nav_result) {
                selectedFragment = new VoteResultsFragment();
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(AdminHomeActivity.this, LoginActivity.class));
                finish(); // Prevent going back to this activity
                return true;
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