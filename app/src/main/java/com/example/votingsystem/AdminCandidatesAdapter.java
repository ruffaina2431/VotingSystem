package com.example.votingsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminCandidatesAdapter extends RecyclerView.Adapter<AdminCandidatesAdapter.AdminCandidateViewHolder> {
    private List<Candidate> candidates;
    private OnCandidateClickListener listener;
    public void updateList(List<Candidate> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new CandidateDiffCallback(candidates, newList)
        );
        candidates.clear();
        candidates.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }
    public interface OnCandidateClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public AdminCandidatesAdapter(List<Candidate> candidates, OnCandidateClickListener listener) {
        this.candidates = candidates;
        this.listener = listener;
    }


    @Override
    public AdminCandidateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_candidate_item, parent, false);
        return new AdminCandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder( AdminCandidateViewHolder holder, int position) {
        Candidate candidate = candidates.get(position);
        holder.bind(candidate);
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    class AdminCandidateViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView positionTextView;
        private ImageButton editButton;
        private ImageButton deleteButton;

        public AdminCandidateViewHolder( View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvCandidateName);
            positionTextView = itemView.findViewById(R.id.tvCandidatePosition);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(position);
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position);
                }
            });
        }

        public void bind(Candidate candidate) {
            nameTextView.setText(candidate.getName());
            positionTextView.setText(candidate.getPosition());
        }


    }
}