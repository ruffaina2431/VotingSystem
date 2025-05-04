package com.example.votingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class CandidatesActivity extends AppCompatActivity {
    ListView listViewCandidates;
    String[] candidatesName = {"Alice Johnson", "Bob Smith", "Clara Lee"};

    int[] candidatesImages = {
            R.drawable.alice_johnson,
            R.drawable.bob_smith,
            R.drawable.clara_lee,
    };

    String[] candidatesSubtitles = {"party A", "party B", "party C"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidate_item);
        listViewCandidates = findViewById(R.id.listViewCandidates);

        CandidatesAdapter adapter = new CandidatesAdapter(this,candidatesName,candidatesImages,candidatesSubtitles);

        listViewCandidates.setAdapter(adapter);

        listViewCandidates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CandidatesActivity.this, CandidatesDetail.class);
                intent.putExtra("candidatesName", candidatesName[position]);
                intent.putExtra("candidatesImages", candidatesImages[position]);
                intent.putExtra("candidatesSubtitles", candidatesSubtitles[position]);
                startActivity(intent);
            }
        });
    }
}