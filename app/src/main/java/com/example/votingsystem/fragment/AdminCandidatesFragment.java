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

import com.example.votingsystem.dialog.AddCandidateDialog;
import com.example.votingsystem.R;
import com.example.votingsystem.adapter.AdminCandidatesAdapter;
import com.example.votingsystem.model.AdminCandidates;
import com.example.votingsystem.network.CandidateRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminCandidatesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtNoCandidates;

    private FloatingActionButton btnAddCandidate;

    private AdminCandidatesAdapter adapter;
    private List<AdminCandidates> candidateList = new ArrayList<>();

    public AdminCandidatesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_candidates, container, false);
        txtNoCandidates = view.findViewById(R.id.txt_no_candidates);
        btnAddCandidate = view.findViewById(R.id.btnAddCandidate);
        recyclerView = view.findViewById(R.id.recycler_view_candidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddCandidate.setOnClickListener(v -> {
            AddCandidateDialog dialog = AddCandidateDialog.newInstance(this::loadCandidates);
            dialog.show(getParentFragmentManager(), "addCandidate");
        });

        adapter = new AdminCandidatesAdapter(candidateList, getContext());
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
                            AdminCandidates candidate = new AdminCandidates(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getString("position"),
                                    obj.getString("party")
                            );

                            candidateList.add(candidate);
                        }
                        // ✅ Check after the loop
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
