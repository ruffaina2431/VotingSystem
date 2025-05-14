package com.example.votingsystem.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.votingsystem.R;
import com.example.votingsystem.model.AdminCandidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class EditCandidateDialog extends DialogFragment {

    private AdminCandidates candidate;
    private Runnable onSuccess;

    public static EditCandidateDialog newInstance(AdminCandidates candidate, Runnable onSuccess) {
        EditCandidateDialog dialog = new EditCandidateDialog();
        dialog.candidate = candidate;
        dialog.onSuccess = onSuccess;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_candidate, null);
        EditText nameInput = view.findViewById(R.id.edit_candidate_name);
        EditText positionInput = view.findViewById(R.id.edit_candidate_position);
        EditText partyInput = view.findViewById(R.id.edit_candidate_party);

        nameInput.setText(candidate.getName());
        positionInput.setText(candidate.getPosition());
        partyInput.setText(candidate.getParty());

        return new AlertDialog.Builder(requireActivity())
                .setTitle("Edit Candidate")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    String newPosition = positionInput.getText().toString().trim();
                    String newParty = partyInput.getText().toString().trim();

                    if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newPosition) || TextUtils.isEmpty(newParty)) {
                        showToast("All fields are required");
                        return;
                    }

                    CandidateRequest.updateCandidate(
                            requireContext(),
                            candidate.getId(),
                            newName,
                            newPosition,
                            newParty,
                            response -> {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response.toString());
                                    if (jsonResponse.getBoolean("success")) {
                                        showToast(jsonResponse.getString("message"));
                                        if (onSuccess != null) onSuccess.run();
                                    } else {
                                        showToast("Update failed: " + jsonResponse.getString("message"));
                                    }
                                } catch (JSONException e) {
                                    showToast("Error parsing response");
                                    e.printStackTrace();
                                }
                            },
                            error -> {
                                showToast("Network error: " + error.getMessage());
                                error.printStackTrace();
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    // Safe Toast showing method
    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show()
            );
        }
    }
}