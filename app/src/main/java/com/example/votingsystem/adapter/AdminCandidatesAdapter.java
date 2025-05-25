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
import com.example.votingsystem.model.Candidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONException;

import java.util.List;

public class AdminCandidatesAdapter extends RecyclerView.Adapter<AdminCandidatesAdapter.ViewHolder> {

    private List<Candidates> candidatesList;
    private Context context;
    private boolean isFinalized = false;
    private OnCandidateActionListener actionListener;

    public interface OnCandidateActionListener {
        void onCandidateEdited(Candidates candidate);
        void onCandidateDeleted(int position);
        void onFinalizationStatusChanged(boolean isFinalized);
    }

    public AdminCandidatesAdapter(List<Candidates> candidatesList, Context context, OnCandidateActionListener listener) {
        this.candidatesList = candidatesList;
        this.context = context;
        this.actionListener = listener;
    }

    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
        notifyDataSetChanged();
        if (actionListener != null) {
            actionListener.onFinalizationStatusChanged(isFinalized);
        }
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_candidate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Candidates candidate = candidatesList.get(position);

        holder.nameText.setText(candidate.getName());
        holder.positionText.setText(candidate.getPosition());
        holder.partyText.setText(candidate.getParty());

        // Set visibility of edit/delete buttons based on finalization status
        if (isFinalized) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (!isFinalized) {
                EditCandidateDialog dialog = EditCandidateDialog.newInstance(candidate, () -> {
                    if (actionListener != null) {
                        actionListener.onCandidateEdited(candidate);
                    }
                    notifyItemChanged(holder.getAdapterPosition());
                });
                dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "editCandidate");
            } else {
                Toast.makeText(context, "Cannot edit candidates after finalization", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (!isFinalized) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Candidate")
                        .setMessage("Are you sure you want to delete " + candidate.getName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (position != RecyclerView.NO_POSITION && actionListener != null) {
                                actionListener.onCandidateDeleted(position);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                Toast.makeText(context, "Cannot delete candidates after finalization", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return candidatesList.size();
    }

    public void updateCandidateList(List<Candidates> newList) {
        candidatesList = newList;
        notifyDataSetChanged();
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