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

import java.util.List;

public class CandidatesAdapter extends RecyclerView.Adapter<CandidatesAdapter.ViewHolder> {

    private List<Candidates> candidateList;
    private Context context;

    public CandidatesAdapter(List<Candidates> candidateList, Context context) {
        this.candidateList = candidateList;
        this.context = context;
    }

    @NonNull
    @Override
    public CandidatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_candidate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidatesAdapter.ViewHolder holder, int position) {
        Candidates candidate = candidateList.get(position);
        holder.txtName.setText(candidate.getName());
        holder.txtPosition.setText(candidate.getPosition());
        holder.txtParty.setText(candidate.getParty());
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPosition, txtParty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCandidateName);
            txtPosition = itemView.findViewById(R.id.txtCandidatePosition);
            txtParty = itemView.findViewById(R.id.txtCandidateParty);
        }
    }
}