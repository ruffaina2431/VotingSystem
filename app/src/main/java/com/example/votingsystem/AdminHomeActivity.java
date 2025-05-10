package com.example.votingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class AdminHomeActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backPressedToast;
    Button btnViewCandidates, btnCastVote, btnViewResults,btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    // Exit the app completely
                    finishAffinity(); // Closes all activities in the task
                    System.exit(0);  // Ensures the app process is killed (optional)
                } else {
                    Toast.makeText(
                            AdminHomeActivity.this,
                            "Press back again to exit",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                backPressedTime = System.currentTimeMillis();
            }
        });

        //------------------TO VIEW CANDIDATES INFORMATION----------------
        Button btnViewCandidates = findViewById(R.id.btnViewCandidates);

        btnViewCandidates.setOnClickListener(v ->
                startActivity(new Intent(this, AdminCandidates.class)));
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

        //-----------------TO LOG-OUT -----------------------------------
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
        //-------------------------------------------------------------------



    }

}