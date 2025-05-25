package com.example.votingsystem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.votingsystem.R;
import com.example.votingsystem.network.CandidateRequest;

public class AddCandidateDialog extends DialogFragment {
    private EditText etName, etPosition, etParty;
    private Button btnSubmit;

    private static Runnable onCandidateAddedCallback;

    public static AddCandidateDialog newInstance(Runnable callback) {
        onCandidateAddedCallback = callback;
        return new AddCandidateDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = requireContext();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_candidate, null);

        etName = view.findViewById(R.id.etName);
        etPosition = view.findViewById(R.id.etPosition);
        etParty = view.findViewById(R.id.etParty);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String position = etPosition.getText().toString().trim();
            String party = etParty.getText().toString().trim();

            if (name.isEmpty() || position.isEmpty() || party.isEmpty()) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage("Please fill all fields")
                        .setPositiveButton("OK", null)
                        .show();

                return;
            }

            CandidateRequest.addCandidate(context, name, position, party, response -> {
                try {
                    if (response.getBoolean("success")) {
                        Toast.makeText(context, "Candidate added", Toast.LENGTH_SHORT).show();
                        dismiss();
                        if (onCandidateAddedCallback != null) {
                            onCandidateAddedCallback.run();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(context, "Failed to add candidate", Toast.LENGTH_SHORT).show();
            });
        });

        return new AlertDialog.Builder(context)
                .setTitle("Add Candidate")
                .setView(view)
                .create();
    }
}
