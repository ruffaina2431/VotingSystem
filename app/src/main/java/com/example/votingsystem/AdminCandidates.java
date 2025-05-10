package com.example.votingsystem;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdminCandidates extends AppCompatActivity
        implements AdminCandidatesAdapter.OnCandidateClickListener {  // Implement the interface

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AdminCandidatesAdapter adapter;
    private TextView tvEmptyState;
    private List<Candidate> candidates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_candidates);
        // Initialize ProgressBar
        progressBar = findViewById(R.id.progressBar); // Add this line
        if (progressBar == null) {
            throw new IllegalStateException("ProgressBar not found in layout!");
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rvAdminCandidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvEmptyState = findViewById(R.id.tvEmptyState); // Add this line

        // Pass 'this' as the listener (now valid)
        adapter = new AdminCandidatesAdapter(candidates, this);
        recyclerView.setAdapter(adapter);

        loadCandidates();  // Initialize data
    }
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void loadCandidates() {
        // Check if views are initialized
        if (progressBar == null || recyclerView == null) {
            Toast.makeText(this, "UI not properly initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                candidates.addAll(getDummyCandidates());
                adapter.notifyDataSetChanged();
            } finally {
                showLoading(false);
                updateEmptyState();
            }
        }, 1000);
    }// Implement this method
    private void updateEmptyState() {
        if (candidates == null || candidates.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    // Implement interface methods
    @Override
    public void onEditClick(int position) {
        // Handle edit
    }

    @Override
    public void onDeleteClick(int position) {
        // Handle delete
    }
    private List<Candidate> getDummyCandidates() {
        List<Candidate> dummyList = new ArrayList<>();
        dummyList.add(new Candidate(1,"John Doe", "President"));
        dummyList.add(new Candidate(2,"Jane Smith", "Vice President"));
        return dummyList;
    }
}