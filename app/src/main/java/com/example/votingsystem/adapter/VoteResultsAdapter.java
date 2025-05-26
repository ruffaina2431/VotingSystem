package com.example.votingsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingsystem.R;
import com.example.votingsystem.model.Candidates;
import com.example.votingsystem.model.VoteResult;

import java.util.List;

public class VoteResultsAdapter extends RecyclerView.Adapter<VoteResultsAdapter.VoteResultViewHolder> {

    private final List<VoteResult> results;

    public VoteResultsAdapter(List<VoteResult> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public VoteResultsAdapter.VoteResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vote_result, parent, false);
        return new VoteResultsAdapter.VoteResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoteResultsAdapter.VoteResultViewHolder holder, int position) {
        VoteResult result = results.get(position);
        holder.textName.setText(result.getName());
        holder.textPosition.setText(result.getPosition());
        holder.textParty.setText(result.getParty());
        holder.textVotes.setText("Votes: " + result.getVoteCount());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class VoteResultViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPosition, textParty, textVotes;

        public VoteResultViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textCandidateName);
            textPosition = itemView.findViewById(R.id.textCandidatePosition);
            textParty = itemView.findViewById(R.id.textCandidateParty);
            textVotes = itemView.findViewById(R.id.textVoteCount);
        }
    }
}
