package com.example.votingsystem.fragment;

import static com.example.votingsystem.network.ApiURLs.BASE_URL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.votingsystem.R;
import com.example.votingsystem.adapter.AdminVoteResultsAdapter;
import com.example.votingsystem.adapter.VoteResultsAdapter;
import com.example.votingsystem.model.VoteResult;
import com.example.votingsystem.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VoteResultsFragment extends Fragment {

    private TextView tvScheduleTime;
    private RecyclerView recyclerView;
    private VoteResultsAdapter adapter; // or create a new User adapter
    private List<VoteResult> voteResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote_results, container, false);

        tvScheduleTime = view.findViewById(R.id.textUserSchedule);
        recyclerView = view.findViewById(R.id.recycler_view_user_results);

        voteResults = new ArrayList<>();
        adapter = new VoteResultsAdapter(voteResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchSchedule();

        return view;
    }

    private void fetchSchedule() {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String dateTime = obj.optString("scheduled_at", "");
                        if (dateTime.isEmpty() || dateTime.equalsIgnoreCase("null")) {
                            tvScheduleTime.setText("Current schedule: Not set");
                            voteResults.clear();
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        tvScheduleTime.setText("Schedule: " + dateTime);
                        checkScheduleAndLoadResults(dateTime);
                    } catch (Exception e) {
                        tvScheduleTime.setText("Failed to load schedule");
                    }
                },
                error -> tvScheduleTime.setText("Error loading schedule")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "get_result_schedule");
                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void checkScheduleAndLoadResults(String dateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date scheduledDate = sdf.parse(dateTime);
            Date now = new Date();
            if (now.compareTo(scheduledDate) >= 0) {
                loadResults();
            } else {
                Toast.makeText(getContext(), "Results are not yet available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error parsing schedule", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadResults() {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);

                        if (!success) {
                            String message = jsonResponse.optString("message", "Results not available");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray resultsArray = jsonResponse.getJSONArray("results");
                        voteResults.clear();

                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject obj = resultsArray.getJSONObject(i);
                            voteResults.add(new VoteResult(
                                    obj.getString("candidate_name"),
                                    obj.getString("position"),
                                    obj.getString("party"),
                                    obj.getInt("vote_count")
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Failed to parse results", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Failed to load results", Toast.LENGTH_SHORT).show()
        )



        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "get_results");
                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

}
