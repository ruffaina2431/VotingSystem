package com.example.votingsystem.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingsystem.R;
import com.example.votingsystem.adapter.VoteCandidatesAdapter;
import com.example.votingsystem.model.Candidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VoteFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtNoCandidates;
    private VoteCandidatesAdapter adapter;
    private List<Candidates> candidateList = new ArrayList<>();
    private int studentId;

    public VoteFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote, container, false);
        txtNoCandidates = view.findViewById(R.id.txtNoCandidates);
        recyclerView = view.findViewById(R.id.recycler_view_candidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences userPrefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences electionPrefs = requireContext().getSharedPreferences("election_prefs", Context.MODE_PRIVATE);
        studentId = userPrefs.getInt("user_id", -1);

        if (studentId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }
        boolean electionStarted = electionPrefs.getBoolean("election_started", false);


        if (!electionStarted) {
            txtNoCandidates.setVisibility(View.VISIBLE);
            txtNoCandidates.setText("Election has not started yet.");
            recyclerView.setVisibility(View.GONE);
            return view;
        }



        loadCandidates();
        return view;
    }

    private void loadCandidates() {
        CandidateRequest.getAllCandidates(getContext(), response -> {
            candidateList.clear();
            try {
                boolean electionStarted = response.optBoolean("election_started", false);

                if (!electionStarted) {
                    txtNoCandidates.setVisibility(View.VISIBLE);
                    txtNoCandidates.setText("Election has not started yet.");
                    recyclerView.setVisibility(View.GONE);
                    return;
                }

                for (int i = 0; i < response.getJSONArray("candidates").length(); i++) {
                    JSONObject candidateData = response.getJSONArray("candidates").getJSONObject(i);
                    Candidates candidate = new Candidates(
                            candidateData.getInt("id"),
                            candidateData.getString("name"),
                            candidateData.getString("position"),
                            candidateData.getString("party")
                    );
                    candidateList.add(candidate);
                }

                txtNoCandidates.setVisibility(candidateList.isEmpty() ? View.VISIBLE : View.GONE);
                if (candidateList.isEmpty()) {
                    txtNoCandidates.setText("No candidates available.");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter = new VoteCandidatesAdapter(candidateList, getContext(), studentId);
                adapter.setElectionStarted(true); // ✅ Safe now
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error loading candidates", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(getContext(), "Error loading candidates", Toast.LENGTH_SHORT).show();
        });
    }


}
