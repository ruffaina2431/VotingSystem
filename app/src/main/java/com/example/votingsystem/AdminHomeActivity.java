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

    // Keep fragment instances as fields
    private Fragment adminCandidatesFragment;
    private Fragment voteFragment;
    private Fragment adminVoteResultsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        bottomNav = findViewById(R.id.bottom_navigation_admin);

        // Create fragments only once
        if (savedInstanceState == null) {
            adminCandidatesFragment = new AdminCandidatesFragment();
            voteFragment = new VoteFragment();
            adminVoteResultsFragment = new AdminVoteResultsFragment();

            // Add all fragments, but show only adminCandidatesFragment initially
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.admin_content_frame, adminCandidatesFragment, "candidates")
                    .add(R.id.admin_content_frame, voteFragment, "vote")
                    .hide(voteFragment)
                    .add(R.id.admin_content_frame, adminVoteResultsFragment, "results")
                    .hide(adminVoteResultsFragment)
                    .commit();
        } else {
            // After configuration change, get the fragments by tag
            adminCandidatesFragment = getSupportFragmentManager().findFragmentByTag("candidates");
            voteFragment = getSupportFragmentManager().findFragmentByTag("vote");
            adminVoteResultsFragment = getSupportFragmentManager().findFragmentByTag("results");
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_candidates) {
                showFragment(adminCandidatesFragment);
                return true;
            } else if (id == R.id.nav_vote) {
                showFragment(voteFragment);
                return true;
            } else if (id == R.id.nav_result) {
                showFragment(adminVoteResultsFragment);
                return true;
            } else if (id == R.id.nav_logout) {
                new AlertDialog.Builder(AdminHomeActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            startActivity(new Intent(AdminHomeActivity.this, LoginActivity.class));
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            }

            return false;
        });
    }

    private void showFragment(Fragment fragmentToShow) {
        getSupportFragmentManager().beginTransaction()
                .hide(adminCandidatesFragment)
                .hide(voteFragment)
                .hide(adminVoteResultsFragment)
                .show(fragmentToShow)
                .commit();
    }
}
