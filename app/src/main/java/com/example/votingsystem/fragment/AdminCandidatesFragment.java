package com.example.votingsystem.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingsystem.dialog.AddCandidateDialog;
import com.example.votingsystem.R;
import com.example.votingsystem.adapter.AdminCandidatesAdapter;
import com.example.votingsystem.model.Candidates;
import com.example.votingsystem.network.CandidateRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminCandidatesFragment extends Fragment implements AdminCandidatesAdapter.OnCandidateActionListener {

    private RecyclerView recyclerView;
    private TextView txtNoCandidates;
    private FloatingActionButton btnAddCandidate;
    private Button btnFinalizeCandidates;

    private AdminCandidatesAdapter adapter;
    private List<Candidates> candidateList = new ArrayList<>();
    private boolean isFinalized = false;

    public AdminCandidatesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_candidates, container, false);
        txtNoCandidates = view.findViewById(R.id.txt_no_candidates);
        btnAddCandidate = view.findViewById(R.id.btnAddCandidate);
        btnFinalizeCandidates = view.findViewById(R.id.btnFinalizeCandidates);
        recyclerView = view.findViewById(R.id.recycler_view_candidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        checkFinalizationStatus();

        btnAddCandidate.setOnClickListener(v -> {
            if (!isFinalized) {
                AddCandidateDialog dialog = new AddCandidateDialog();
                dialog.setRefreshCallback(this::loadCandidates);
                dialog.show(getParentFragmentManager(), "addCandidate");
            } else {
                Toast.makeText(getContext(), "Cannot add candidates after finalization", Toast.LENGTH_SHORT).show();
            }
        });

        btnFinalizeCandidates.setOnClickListener(v -> {
            if (!isFinalized) {
                finalizeCandidateList();
            }
        });

        adapter = new AdminCandidatesAdapter(candidateList, getContext(), this);
        recyclerView.setAdapter(adapter);

        loadCandidates();
        updateUI();

        return view;
    }

    @Override
    public void onCandidateEdited(Candidates candidate) {
        if (!isFinalized) {
            AddCandidateDialog dialog = new AddCandidateDialog();
            Bundle args = new Bundle();
            args.putInt("candidate_id", candidate.getId());
            args.putString("name", candidate.getName());
            args.putString("position", candidate.getPosition());
            args.putString("party", candidate.getParty());
            dialog.setArguments(args);
            dialog.setRefreshCallback(this::loadCandidates);
            dialog.show(getParentFragmentManager(), "editCandidate");
        } else {
            Toast.makeText(getContext(), "Cannot edit candidates after finalization", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCandidateDeleted(int candidateId) {
        if (!isFinalized) {
            deleteCandidate(candidateId);
        } else {
            Toast.makeText(getContext(), "Cannot delete candidates after finalization", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinalizationStatusChanged(boolean isFinalized) {
        // Handle if needed
    }

    private void checkFinalizationStatus() {
        CandidateRequest.checkFinalizationStatus(
                getContext(),
                response -> {
                    try {
                        isFinalized = response.getBoolean("is_finalized");
                        updateUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    isFinalized = false;
                    updateUI();
                }
        );
    }

    private void updateUI() {
        if (isFinalized) {
            btnAddCandidate.setVisibility(View.GONE);
            btnFinalizeCandidates.setVisibility(View.GONE);
        } else {
            btnAddCandidate.setVisibility(View.VISIBLE);
            btnFinalizeCandidates.setVisibility(View.VISIBLE);
        }
        if (adapter != null) {
            adapter.setFinalized(isFinalized);
        }
    }

    private void loadCandidates() {
        CandidateRequest.getAllCandidates(
                getContext(),
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("candidates");
                        candidateList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Candidates candidate = new Candidates(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getString("position"),
                                    obj.getString("party")
                            );
                            candidateList.add(candidate);
                        }

                        if (candidateList.isEmpty()) {
                            txtNoCandidates.setVisibility(View.VISIBLE);
                        } else {
                            txtNoCandidates.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );
    }

    private void deleteCandidate(int candidateId) {
        CandidateRequest.deleteCandidate(
                getContext(),
                candidateId,
                response -> {
                    Toast.makeText(getContext(), "Candidate deleted successfully", Toast.LENGTH_SHORT).show();
                    loadCandidates();
                },
                error -> {
                    Toast.makeText(getContext(), "Failed to delete candidate", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );
    }

    private void finalizeCandidateList() {
        CandidateRequest.finalizeCandidates(
                getContext(),
                response -> {
                    try {
                        isFinalized = true;
                        updateUI();
                        Toast.makeText(getContext(), "Candidate list finalized successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Failed to finalize candidate list", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );
    }
}