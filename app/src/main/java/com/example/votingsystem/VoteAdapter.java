package com.example.votingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class VoteAdapter extends ArrayAdapter<Candidate> {

    private Context context;
    private List<Candidate> candidates;

    public VoteAdapter(Context context, List<Candidate> candidates) {
        super(context, 0, candidates);
        this.context = context;
        this.candidates = candidates;
    }


    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.vote_item, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvCandidateName);
        Button btnVote = convertView.findViewById(R.id.btnVote);

        Candidate candidate = getItem(position);
        /*
        if (candidate != null) {
            tvName.setText(candidate.getName());

            if (candidate.isVoted()) {
                btnVote.setEnabled(false);
                btnVote.setText("Voted");
            } else {
                btnVote.setEnabled(true);
                btnVote.setText("Vote");
            }

            btnVote.setOnClickListener(v -> {
                candidate.setVoted(true);

                // Save the vote using SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences("Votes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                String key = candidate.getName().toLowerCase().replace(" ", "_");
                int currentVotes = prefs.getInt(key, 0);
                editor.putInt(key, currentVotes + 1);
                editor.apply();

                notifyDataSetChanged();

                Toast.makeText(context, "Voted for " + candidate.getName(), Toast.LENGTH_SHORT).show();
            });

        }
*/
        return convertView;
    }
}
