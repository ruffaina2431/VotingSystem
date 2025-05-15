package com.example.votingsystem.adapter;
import com.example.votingsystem.dialog.EditCandidateDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import androidx.recyclerview.widget.RecyclerView;

import com.example.votingsystem.R;
import com.example.votingsystem.model.AdminCandidates;
import com.example.votingsystem.network.CandidateRequest;


import org.json.JSONException;

import java.util.List;

public class AdminCandidatesAdapter extends RecyclerView.Adapter<AdminCandidatesAdapter.ViewHolder> {

    private List<AdminCandidates> candidatesList;
    private Context context;

    public AdminCandidatesAdapter(List<AdminCandidates> candidatesList, Context context) {
        this.candidatesList = candidatesList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminCandidatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_candidate_item, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(AdminCandidatesAdapter.ViewHolder holder, int position) {
        AdminCandidates candidate = candidatesList.get(position);

        holder.nameText.setText(candidate.getName());
        holder.positionText.setText(candidate.getPosition());
        holder.partyText.setText(candidate.getParty());

        holder.btnEdit.setOnClickListener(v -> {
            EditCandidateDialog dialog = EditCandidateDialog.newInstance(candidate, () -> {
                notifyItemChanged(holder.getAdapterPosition());
            });

            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "editCandidate");
        });



        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Candidate")
                    .setMessage("Are you sure you want to delete " + candidate.getName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (position != RecyclerView.NO_POSITION) {
                            CandidateRequest.deleteCandidate(
                                    context,
                                    candidate.getId(),
                                    response -> {
                                        try {
                                            if (response.getBoolean("success")) {
                                                candidatesList.remove(position);
                                                notifyItemRemoved(position);
                                                Toast.makeText(context, "Candidate deleted", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            Toast.makeText(context, "Response error", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> {
                                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                        error.printStackTrace();
                                    }
                            );
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


    }

    @Override
    public int getItemCount() {
        return candidatesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, positionText, partyText;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_candidate_name);
            positionText = itemView.findViewById(R.id.text_candidate_position);
            partyText = itemView.findViewById(R.id.text_candidate_party);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
