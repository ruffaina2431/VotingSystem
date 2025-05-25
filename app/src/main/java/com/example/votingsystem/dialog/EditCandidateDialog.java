package com.example.votingsystem.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.votingsystem.R;
import com.example.votingsystem.model.Candidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class EditCandidateDialog extends DialogFragment {

    private Candidates candidate;
    private Runnable onSuccess;

    public static EditCandidateDialog newInstance(Candidates candidate, Runnable onSuccess) {
        EditCandidateDialog dialog = new EditCandidateDialog();
        dialog.candidate = candidate;
        dialog.onSuccess = onSuccess;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_candidate, null);
        EditText nameInput = view.findViewById(R.id.edit_candidate_name);
        EditText positionInput = view.findViewById(R.id.edit_candidate_position);
        EditText partyInput = view.findViewById(R.id.edit_candidate_party);

        nameInput.setText(candidate.getName());
        positionInput.setText(candidate.getPosition());
        partyInput.setText(candidate.getParty());

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle("Edit Candidate")
                .setView(view)
                .setPositiveButton("Save", null) // we override this below
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String newName = nameInput.getText().toString().trim();
                String newPosition = positionInput.getText().toString().trim();
                String newParty = partyInput.getText().toString().trim();

                // Input validation
                if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newPosition) || TextUtils.isEmpty(newParty)) {
                    showAlert("All fields are required.");
                    return;
                }

                // No changes detected
                if (newName.equalsIgnoreCase(candidate.getName()) &&
                        newPosition.equalsIgnoreCase(candidate.getPosition()) &&
                        newParty.equalsIgnoreCase(candidate.getParty())) {
                    showAlert("No changes detected.");
                    return;
                }

                // Make update request
                CandidateRequest.updateCandidate(
                        requireContext(),
                        candidate.getId(),
                        newName,
                        newPosition,
                        newParty,
                        response -> {
                            try {
                                JSONObject jsonResponse = new JSONObject(response.toString());
                                boolean success = jsonResponse.getBoolean("success");
                                String message = jsonResponse.getString("message");

                                if (success) {
                                    new AlertDialog.Builder(requireContext())
                                            .setTitle("Success")
                                            .setMessage(message)
                                            .setPositiveButton("OK", (dialog1, which1) -> {
                                                if (onSuccess != null) onSuccess.run();
                                                dismiss();
                                            })
                                            .show();
                                } else {
                                    if (message.contains("already exists")) {
                                        showAlert("A candidate with this name already exists.");
                                    } else {
                                        showAlert("Update failed: " + message);
                                    }
                                }
                            } catch (JSONException e) {
                                showAlert("Error parsing server response.");
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            showAlert("Network error: " + error.getMessage());
                            error.printStackTrace();
                        }
                );
            });
        });

        return dialog;
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
