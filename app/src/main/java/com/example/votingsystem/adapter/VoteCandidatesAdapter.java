package com.example.votingsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.votingsystem.R;
import com.example.votingsystem.model.AdminCandidates;
import com.example.votingsystem.network.CandidateRequest;

import java.util.List;

public class VoteCandidatesAdapter extends RecyclerView.Adapter<VoteCandidatesAdapter.ViewHolder> {


    private List<AdminCandidates> candidates;
    private Context context;
    private int studentId;

    public VoteCandidatesAdapter(List<AdminCandidates> candidates, Context context, int studentId) {
        this.candidates = candidates;
        this.context = context;
        this.studentId = studentId;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.candidate_item_vote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AdminCandidates candidate = candidates.get(position);
        holder.name.setText(candidate.getName());
        holder.position.setText(candidate.getPosition());
        holder.party.setText(candidate.getParty());

        holder.voteButton.setOnClickListener(v -> {
            // Call API to register the vote
            CandidateRequest.voteForCandidate(context, studentId, candidate.getId(), candidate.getPosition(), response -> {
                Toast.makeText(context, "Vote casted for " + candidate.getName(), Toast.LENGTH_SHORT).show();
            }, error -> {
                Toast.makeText(context, "Error casting vote", Toast.LENGTH_SHORT).show();
            });

        });
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, position, party;
        Button voteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.candidate_name);
            position = itemView.findViewById(R.id.candidate_position);
            party = itemView.findViewById(R.id.candidate_party);
            voteButton = itemView.findViewById(R.id.vote_button);
        }
    }
}
