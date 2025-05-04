package com.example.votingsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ResultsAdapter extends ArrayAdapter<Candidate> {
    Context context;
    List<Candidate> candidates;

    public ResultsAdapter(Context context, List<Candidate> candidates) {
        super(context, 0, candidates);
        this.context = context;
        this.candidates = candidates;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.result_item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.tvCandidateResultName);
        TextView voteCount = convertView.findViewById(R.id.tvVoteCount);

        Candidate candidate = getItem(position);
        if (candidate != null) {
            name.setText(candidate.getName());
            voteCount.setText(candidate.isVoted() ? "Votes: 1" : "Votes: 0"); // simple logic
        }

        return convertView;
    }
}
