package com.example.votingsystem;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

public class ResultActivity extends AppCompatActivity {

    ListView listViewResults;
    List<Candidate> resultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        listViewResults = findViewById(R.id.listViewResults);

        SharedPreferences prefs = getSharedPreferences("Votes", MODE_PRIVATE);
        resultsList = new ArrayList<>();

        // Add candidates with their real vote counts
        resultsList.add(new Candidate("Alice Johnson", "Party A", prefs.getInt("alice_johnson",0 )));
        resultsList.add(new Candidate("Bob Smith", "Party B", prefs.getInt("bob_smith", 0)));
        resultsList.add(new Candidate("Clara Lee", "Party C", prefs.getInt("clara_lee", 0)));


        ResultsAdapter adapter = new ResultsAdapter(this, resultsList);
        listViewResults.setAdapter(adapter);
    }
}
