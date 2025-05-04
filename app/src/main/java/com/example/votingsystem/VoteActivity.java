package com.example.votingsystem;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import java.util.List;

public class VoteActivity extends AppCompatActivity {

    ListView listViewVote;
    List<Candidate> candidates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        listViewVote = findViewById(R.id.listViewVote);

        candidates = new ArrayList<>();
        candidates.add(new Candidate("Alice Johnson", "Party A", 0)); // Added voteCount
        candidates.add(new Candidate("Bob Smith", "Party B", 0));     // Added voteCount
        candidates.add(new Candidate("Clara Lee", "Party C", 0));     // Added voteCount

        VoteAdapter adapter = new VoteAdapter(this, candidates);

        listViewVote.setAdapter(adapter);
    }
}

