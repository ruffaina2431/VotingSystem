package com.example.votingsystem.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingsystem.dialog.AddCandidateDialog;
import com.example.votingsystem.R;
import com.example.votingsystem.adapter.AdminCandidatesAdapter;
import com.example.votingsystem.model.Candidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminCandidatesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtNoCandidates;
    private Button btnAddCandidate, btnStartVoting, btnResetElection;

    private AdminCandidatesAdapter adapter;
    private List<Candidates> candidateList = new ArrayList<>();

    private boolean electionStarted = false;
    private String officialEndTime = "";

    public AdminCandidatesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_candidates, container, false);
        txtNoCandidates = view.findViewById(R.id.txt_no_candidates);
        btnAddCandidate = view.findViewById(R.id.btnAddCandidate);
        btnStartVoting = view.findViewById(R.id.btnStartVoting);
        btnResetElection = view.findViewById(R.id.btnResetElection);
        recyclerView = view.findViewById(R.id.recycler_view_candidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AdminCandidatesAdapter(candidateList, getContext());
        recyclerView.setAdapter(adapter);

        btnAddCandidate.setOnClickListener(v -> {

            if(electionStarted){
                btnAddCandidate.setEnabled(false);
            }
            else{
                AddCandidateDialog dialog = AddCandidateDialog.newInstance(this::loadCandidates);
                dialog.show(getParentFragmentManager(), "addCandidate");
            }

        });

        btnStartVoting.setOnClickListener(v -> {
            if (!electionStarted) {
                CandidateRequest.startElection(getContext(), response -> {
                    Toast.makeText(getContext(), "Election started", Toast.LENGTH_SHORT).show();
                    electionStarted = true;
                    adapter.setElectionStarted(true);

                    SharedPreferences prefs = getContext().getSharedPreferences("election_prefs", Context.MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean("election_started", true)
                            .putString("official_end_time", officialEndTime)
                            .apply();
                    adapter.notifyDataSetChanged();
                    // Disable Add button when election starts
                    btnAddCandidate.setEnabled(false);
                }, error -> Toast.makeText(getContext(), "Failed to start election", Toast.LENGTH_SHORT).show());
            }
        });

        SharedPreferences prefs = getContext().getSharedPreferences("election_prefs", Context.MODE_PRIVATE);
        officialEndTime = prefs.getString("official_end_time", "0");


        btnResetElection.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            long endTime;

            try {
                endTime = Long.parseLong(officialEndTime);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invalid end time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!electionStarted) {
                Toast.makeText(getContext(), "Election has not started yet", Toast.LENGTH_SHORT).show();
                return;
            }

            if (now < endTime) {
                long timeLeftMillis = endTime - now;

                long hours = timeLeftMillis / (1000 * 60 * 60);
                long minutes = (timeLeftMillis / (1000 * 60)) % 60;
                long seconds = (timeLeftMillis / 1000) % 60;

                String timeLeftStr = String.format("Election not finished yet. Time left: %02d:%02d:%02d", hours, minutes, seconds);
                if (getContext() instanceof Activity && !((Activity) getContext()).isFinishing()) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Election Still Ongoing")
                                .setMessage(timeLeftStr)
                                .setPositiveButton("OK", null)
                                .show();
                    });
                }

            }else{

            CandidateRequest.resetElection(getContext(), response -> {
                Toast.makeText(getContext(), "Election reset", Toast.LENGTH_SHORT).show();
                candidateList.clear();
                adapter.notifyDataSetChanged();
                electionStarted = false;
                adapter.setElectionStarted(false);
                btnAddCandidate.setEnabled(true);

                // ✅ Now clear officialEndTime after successful reset
                getContext().getSharedPreferences("election_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .remove("official_end_time")
                        .apply();
            }, error -> Toast.makeText(getContext(), "Reset failed", Toast.LENGTH_SHORT).show());
        }
        });

        loadCandidates();
        return view;
    }

    private void loadCandidates() {
        CandidateRequest.getAllCandidates(getContext(), response -> {
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
                electionStarted = response.optBoolean("election_started", false);
                officialEndTime = response.optString("official_end_time", "0");

                txtNoCandidates.setVisibility(candidateList.isEmpty() ? View.VISIBLE : View.GONE);
                adapter.setElectionStarted(electionStarted);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            error.printStackTrace();

        });
    }
}