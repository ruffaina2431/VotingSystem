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
import com.example.votingsystem.adapter.VoteCandidatesAdapter;
import com.example.votingsystem.model.AdminCandidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VoteFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtNoCandidates;

    private VoteCandidatesAdapter adapter;
    private List<AdminCandidates> candidateList = new ArrayList<>();

    public VoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote, container, false);
        txtNoCandidates = view.findViewById(R.id.txtNoCandidates);
        recyclerView = view.findViewById(R.id.recycler_view_candidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadCandidates();

        return view;
    }

    private void loadCandidates() {
        // ✅ Check after the loop
        if (candidateList.isEmpty()) {
            txtNoCandidates.setVisibility(View.VISIBLE);
        } else {
            txtNoCandidates.setVisibility(View.GONE);
            CandidateRequest.getAllCandidates(getContext(), response -> {
                candidateList.clear();
                try {
                    // Assume candidates are in a "candidates" array in the response
                    for (int i = 0; i < response.getJSONArray("candidates").length(); i++) {
                        JSONObject candidateData = response.getJSONArray("candidates").getJSONObject(i);
                        AdminCandidates candidate = new AdminCandidates(
                                candidateData.getInt("id"),
                                candidateData.getString("name"),
                                candidateData.getString("position"),
                                candidateData.getString("party")
                        );
                        candidateList.add(candidate);
                    }
                    adapter = new VoteCandidatesAdapter(candidateList, getContext());
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error loading candidates", Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Toast.makeText(getContext(), "Error loading candidates", Toast.LENGTH_SHORT).show();
            });
        }

    }
}
