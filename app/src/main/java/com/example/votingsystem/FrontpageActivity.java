package com.example.votingsystem;

import android.content.Intent; // <-- Needed for Intent
import android.os.Bundle;
import android.view.LayoutInflater; // <-- Needed if using inflater
import android.view.View; // <-- Needed for View
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog; // <-- Needed for AlertDialog
import androidx.appcompat.app.AppCompatActivity;

public class FrontpageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);

        // Initialize buttons
        Button btnVoteNow = findViewById(R.id.btnVoteNow);
        ImageButton btnHelp = findViewById(R.id.btnHelp);

        // Vote Now button click - navigate to Login
        btnVoteNow.setOnClickListener(v -> {
            Intent intent = new Intent(FrontpageActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Help button click - show instructions
        btnHelp.setOnClickListener(v -> showHelpDialog());
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_help, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set up Got It button
        Button btnGotIt = view.findViewById(R.id.btnGotIt);
        btnGotIt.setOnClickListener(v -> dialog.dismiss());
    }
}
