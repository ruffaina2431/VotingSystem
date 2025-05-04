
package com.example.votingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button btnViewCandidates, btnCastVote, btnViewResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        //------------------TO VIEW CANDIDATES INFORMATION----------------
        Button btnViewCandidates = findViewById(R.id.btnViewCandidates);

        btnViewCandidates.setOnClickListener(v ->
                startActivity(new Intent(this, CandidatesActivity.class)));
        //----------------------------------------------------------------------

        //-----------------TO CAST A VOTE -----------------------------------
        Button btnCastVote = findViewById(R.id.btnCastVote);

        btnCastVote.setOnClickListener(v ->
                startActivity(new Intent(this, VoteActivity.class)));
        //-------------------------------------------------------------------

        //-----------------TO VIEW THE RESULT -----------------------------------
        Button btnViewResults = findViewById(R.id.btnViewResults);

        btnViewResults.setOnClickListener(v ->
                startActivity(new Intent(this, ResultActivity.class)));
        //-------------------------------------------------------------------


    }

}