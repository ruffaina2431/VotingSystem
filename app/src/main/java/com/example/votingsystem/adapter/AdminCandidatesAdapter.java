// === AdminCandidatesAdapter.java ===
package com.example.votingsystem.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
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
import com.example.votingsystem.dialog.EditCandidateDialog;
import com.example.votingsystem.model.Candidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONException;

import java.util.List;

public class AdminCandidatesAdapter extends RecyclerView.Adapter<AdminCandidatesAdapter.ViewHolder> {

    private List<Candidates> candidatesList;
    private Context context;
    private boolean electionStarted = false;

    public AdminCandidatesAdapter(List<Candidates> candidatesList, Context context) {
        this.candidatesList = candidatesList;
        this.context = context;
    }

    public void setElectionStarted(boolean started) {
        this.electionStarted = started;
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

        holder.btnEdit.setEnabled(!electionStarted);
        holder.btnDelete.setEnabled(!electionStarted);

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
                        CandidateRequest.deleteCandidate(context, candidate.getId(), response -> {
                            int id = candidate.getId();
                            try {
                                if (response.getBoolean("success")) {
                                    int indexToRemove = -1;
                                    for (int i = 0; i < candidatesList.size(); i++) {
                                        if (candidatesList.get(i).getId() == id) {
                                            indexToRemove = i;
                                            break;
                                        }
                                    }
                                    if (indexToRemove != -1) {
                                        candidatesList.remove(indexToRemove);
                                        notifyItemRemoved(indexToRemove);
                                    } else {
                                        Log.w("AdminCandidatesAdapter", "Candidate ID not found for removal: " + id);
                                    }


                                } else {
                                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(context, "Response error", Toast.LENGTH_SHORT).show();
                            }
                        }, error -> Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show());
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
