package com.example.votingsystem.fragment;

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
import com.example.votingsystem.adapter.VoteResultsAdapter;
import com.example.votingsystem.model.AdminCandidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VoteResultsFragment extends Fragment {

    private RecyclerView recyclerView;

    private TextView txtNoCandidates;

    private VoteResultsAdapter adapter;
    private List<AdminCandidates> candidateList = new ArrayList<>();

    public VoteResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote_results, container, false);
        txtNoCandidates = view.findViewById(R.id.txtNoCandidates);

        recyclerView = view.findViewById(R.id.recycler_view_vote_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadVoteResults();

        return view;
    }

    private void loadVoteResults() {
        // After populating candidateList and setting the adapter
        if (candidateList.isEmpty()) {
            txtNoCandidates.setVisibility(View.VISIBLE); // Show "No results yet"
        } else {
            txtNoCandidates.setVisibility(View.GONE); // Hide it
            CandidateRequest.getVoteResults(getContext(), response -> {
                candidateList.clear();
                try {

                    // Assume vote results are in a "results" array in the response
                    for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                        JSONObject candidateData = response.getJSONArray("results").getJSONObject(i);
                        AdminCandidates candidate = new AdminCandidates(
                                candidateData.getInt("id"),
                                candidateData.getString("name"),
                                candidateData.getString("position"),
                                candidateData.getString("party")
                        );
                        candidate.setVoteCount(candidateData.getInt("vote_count"));
                        candidateList.add(candidate);
                    }

                    adapter = new VoteResultsAdapter(candidateList, getContext());
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error loading vote results", Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Toast.makeText(getContext(), "Error loading vote results", Toast.LENGTH_SHORT).show();
            });
        }

    }
}
