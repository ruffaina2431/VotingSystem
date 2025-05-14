package com.example.votingsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.votingsystem.fragment.AdminCandidatesFragment;
import com.example.votingsystem.fragment.VoteFragment;
import com.example.votingsystem.fragment.VoteResultsFragment;

public class AdminHomeActivity extends AppCompatActivity {

    Button btnViewCandidates, btnCastVote, btnViewResult;
    FrameLayout contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        btnViewCandidates = findViewById(R.id.btn_view_candidates);
        btnCastVote = findViewById(R.id.btn_cast_vote);
        btnViewResult = findViewById(R.id.btn_view_result);
        contentFrame = findViewById(R.id.admin_content_frame);

        btnViewCandidates.setOnClickListener(v -> {
            loadFragment(new AdminCandidatesFragment()); // Load view candidate fragment
        });

        btnCastVote.setOnClickListener(v -> {
            loadFragment(new VoteFragment()); // Load cast vote fragment
        });

        btnViewResult.setOnClickListener(v -> {
            loadFragment(new VoteResultsFragment()); // Load view results fragment
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_content_frame, fragment)
                .commit();
    }
}
