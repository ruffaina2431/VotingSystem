package com.example.votingsystem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.votingsystem.R;
import com.example.votingsystem.network.CandidateRequest;

public class AddCandidateDialog extends DialogFragment {
    private EditText etName, etPosition, etParty;
    private Button btnSubmit;
    private Runnable refreshCallback;
    private int candidateId = -1; // -1 indicates new candidate

    public static AddCandidateDialog newInstance(Runnable callback) {
        AddCandidateDialog dialog = new AddCandidateDialog();
        dialog.setRefreshCallback(callback);
        return dialog;
    }

    public static AddCandidateDialog newInstanceForEdit(int id, String name, String position, String party, Runnable callback) {
        AddCandidateDialog dialog = new AddCandidateDialog();
        Bundle args = new Bundle();
        args.putInt("candidate_id", id);
        args.putString("name", name);
        args.putString("position", position);
        args.putString("party", party);
        dialog.setArguments(args);
        dialog.setRefreshCallback(callback);
        return dialog;
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            candidateId = getArguments().getInt("candidate_id", -1);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_candidate, null);

        etName = view.findViewById(R.id.etName);
        etPosition = view.findViewById(R.id.etPosition);
        etParty = view.findViewById(R.id.etParty);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // If editing, populate fields and change button text
        if (getArguments() != null) {
            etName.setText(getArguments().getString("name", ""));
            etPosition.setText(getArguments().getString("position", ""));
            etParty.setText(getArguments().getString("party", ""));
            btnSubmit.setText("Update Candidate");
        }

        btnSubmit.setOnClickListener(v -> handleSubmit());

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view);

        if (candidateId == -1) {
            builder.setTitle("Add Candidate");
        } else {
            builder.setTitle("Edit Candidate");
        }

        return builder.create();
    }

    private void handleSubmit() {
        Context context = requireContext();
        String name = etName.getText().toString().trim();
        String position = etPosition.getText().toString().trim();
        String party = etParty.getText().toString().trim();

        if (name.isEmpty() || position.isEmpty() || party.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (candidateId == -1) {
            // Add new candidate
            CandidateRequest.addCandidate(context, name, position, party,
                    response -> handleSuccess("Candidate added"),
                    error -> handleError("Failed to add candidate")
            );
        } else {
            // Update existing candidate
            CandidateRequest.updateCandidate(context, candidateId, name, position, party,
                    response -> handleSuccess("Candidate updated"),
                    error -> handleError("Failed to update candidate")
            );
        }
    }

    private void handleSuccess(String message) {
        Context context = requireContext();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        dismiss();
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }

    private void handleError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}