
package com.example.votingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;


import com.example.votingsystem.fragment.CandidateFragment;
import com.example.votingsystem.fragment.VoteFragment;
import com.example.votingsystem.fragment.VoteResultsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private long backPressedTime;
    private Toast backPressedToast;
    BottomNavigationView bottomNav;
    Button btnViewCandidates, btnCastVote, btnViewResults,btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        bottomNav = findViewById(R.id.bottom_navigation);
        // Load UserCandidatesFragment into the frame layout
        if (savedInstanceState == null) {
            loadFragment(new CandidateFragment());
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    // Exit the app completely
                    finishAffinity(); // Closes all activities in the task
                    System.exit(0);  // Ensures the app process is killed (optional)
                } else {
                    Toast.makeText(
                            HomeActivity.this,
                            "Press back again to exit",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                backPressedTime = System.currentTimeMillis();
            }
        });


        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.nav_candidates) {
                selectedFragment = new CandidateFragment();
            } else if (id == R.id.nav_vote) {
                selectedFragment = new VoteFragment();
            }else if (id == R.id.nav_result) {
                selectedFragment = new VoteResultsFragment();
            } else if (id == R.id.nav_logout) {
                // Show confirmation dialog before logging out
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Proceed with logout
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            finish(); // Prevent returning to this activity
                        })
                        .setNegativeButton("Cancel", null) // Do nothing on cancel
                        .show();

                return true; // Prevent fragment change on logout click
            }

            return loadFragment(selectedFragment);
        });

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }




}