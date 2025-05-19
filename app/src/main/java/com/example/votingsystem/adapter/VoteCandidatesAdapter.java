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
import com.example.votingsystem.model.Candidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VoteCandidatesAdapter extends RecyclerView.Adapter<VoteCandidatesAdapter.ViewHolder> {

    private List<Candidates> candidates;
    private Context context;
    private int studentId;
    private Set<String> votedPositions = new HashSet<>(); // Track positions voted for

    public VoteCandidatesAdapter(List<Candidates> candidates, Context context, int studentId) {
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
        Candidates candidate = candidates.get(position);
        holder.name.setText(candidate.getName());
        holder.position.setText(candidate.getPosition());
        holder.party.setText(candidate.getParty());

        holder.voteButton.setOnClickListener(v -> {
            String candidatePosition = candidate.getPosition();

            if (votedPositions.contains(candidatePosition)) {
                Toast.makeText(context, "You have already voted for " + candidatePosition, Toast.LENGTH_SHORT).show();
                return;
            }

            CandidateRequest.voteForCandidate(context, studentId, candidate.getId(), candidatePosition, response -> {
                try {
                    String status = response.getString("status");
                    String message = response.getString("message");

                    if (status.equals("error")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Vote casted for " + candidate.getName(), Toast.LENGTH_SHORT).show();
                        votedPositions.add(candidatePosition); // Still locally track it
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Invalid response from server", Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Toast.makeText(context, "Error casting vote", Toast.LENGTH_SHORT).show();
            });

        });

        // Disable the vote button if already voted for this position
        holder.voteButton.setEnabled(!votedPositions.contains(candidate.getPosition()));
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
