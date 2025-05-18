package com.example.votingsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.votingsystem.R;
import com.example.votingsystem.model.Candidates;

import java.util.List;

public class VoteResultsAdapter extends RecyclerView.Adapter<VoteResultsAdapter.ViewHolder> {

    private List<Candidates> candidates;
    private Context context;

    public VoteResultsAdapter(List<Candidates> candidates, Context context) {
        this.candidates = candidates;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.candidate_item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Candidates candidate = candidates.get(position);
        holder.name.setText(candidate.getName());
        holder.position.setText(candidate.getPosition());
        holder.party.setText(candidate.getParty());
        holder.voteCount.setText("Votes: " + candidate.getVoteCount());
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, position, party, voteCount;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.candidate_name);
            position = itemView.findViewById(R.id.candidate_position);
            party = itemView.findViewById(R.id.candidate_party);
            voteCount = itemView.findViewById(R.id.candidate_vote_count);
        }
    }
}
