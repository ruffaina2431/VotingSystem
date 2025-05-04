 package com.example.votingsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CandidatesDetail extends AppCompatActivity {

    TextView candidateNameTextView, candidateDescriptionTextView;
    ImageView candidateImageView;
    Button backButton;

    @Override
    protected  void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_candidates_details);

        // Initialize the views
        candidateNameTextView = findViewById(R.id.candidateNameTextView);
        candidateDescriptionTextView = findViewById(R.id.candidateDescriptionTextView);
        candidateImageView = findViewById(R.id.candidateImageView);
        backButton = findViewById(R.id.backButton);

        // Get the data from the intent
        String candidatesName = getIntent().getStringExtra("candidatesName");
        int candidatesImages = getIntent().getIntExtra("candidatesImages", 0);

        // Set the data to the views
        candidateNameTextView.setText(candidatesName);
        candidateImageView.setImageResource(candidatesImages);
        candidateDescriptionTextView.setText(getDescription(candidatesName));
        String formattedText = getString(R.string.candidate_name, candidatesName);

        candidateNameTextView.setText(formattedText);
        backButton.setOnClickListener(v -> finish());
    }
    private String getDescription(String candidatesName) {
        switch (candidatesName) {
            case "Alice Johnson":
                return "short description";
            case "Bob Smith":
                return "short descriptionasdasd";
            case "Clara Lee":
                return "short descriptionasdc";
            default:
                return "Candidates description not available.";
        }
    }
}