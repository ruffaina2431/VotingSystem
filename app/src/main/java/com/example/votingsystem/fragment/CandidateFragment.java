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
import com.example.votingsystem.adapter.CandidatesAdapter;
import com.example.votingsystem.model.Candidates;
import com.example.votingsystem.network.CandidateRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CandidateFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtNoCandidates;

    private CandidatesAdapter adapter;
    private List<Candidates> candidateList = new ArrayList<>();

    public CandidateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidates, container, false);
        txtNoCandidates = view.findViewById(R.id.txt_no_candidates);
        recyclerView = view.findViewById(R.id.recycler_view_candidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CandidatesAdapter(candidateList, getContext());
        recyclerView.setAdapter(adapter);

        loadCandidates();

        return view;
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
}