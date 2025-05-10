package com.example.votingsystem;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

public class ResultActivity extends AppCompatActivity {

    ListView listViewResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        listViewResults = findViewById(R.id.listViewResults);


        //ResultsAdapter adapter = new ResultsAdapter(this, resultsList);
        //listViewResults.setAdapter(adapter);
    }
}
