package com.example.votingsystem.fragment;

import static com.example.votingsystem.network.ApiURLs.BASE_URL;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.votingsystem.R;
import com.example.votingsystem.adapter.AdminVoteResultsAdapter;
import com.example.votingsystem.model.VoteResult;
import com.example.votingsystem.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminVoteResultsFragment extends Fragment {
    private TextView tvScheduleTime;
    private Button btnSetSchedule, btnClearResults;
    private RecyclerView recyclerView;
    private AdminVoteResultsAdapter adapter;
    private List<VoteResult> voteResults;

    private Handler handler = new Handler();
    private Runnable scheduleChecker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_vote_results, container, false);

        tvScheduleTime = view.findViewById(R.id.textCurrentSchedule);
        btnSetSchedule = view.findViewById(R.id.btnSetSchedule);
        btnClearResults = view.findViewById(R.id.btnClearResults);
        recyclerView = view.findViewById(R.id.recycler_view_vote_results);

        voteResults = new ArrayList<>();
        adapter = new AdminVoteResultsAdapter(voteResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchSchedule();

        btnSetSchedule.setOnClickListener(v -> showDateTimePicker());
        btnClearResults.setOnClickListener(v -> clearResults());

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
                            stopScheduleChecker();
                            return;
                        }
                        tvScheduleTime.setText(dateTime);
                        checkScheduleAndLoadResults(dateTime);
                        startScheduleChecker(dateTime);
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

    private void startScheduleChecker(String dateTime) {
        stopScheduleChecker(); // stop any previous checker

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date scheduledDate;
        try {
            scheduledDate = sdf.parse(dateTime);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid schedule date", Toast.LENGTH_SHORT).show();
            return;
        }

        scheduleChecker = new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                if (now.compareTo(scheduledDate) >= 0) {
                    loadResults();
                    Toast.makeText(getContext(), "Results are now available", Toast.LENGTH_SHORT).show();
                    stopScheduleChecker();
                } else {
                    handler.postDelayed(this, 30000); // check every 30 seconds
                }
            }
        };
        handler.post(scheduleChecker);
    }

    private void stopScheduleChecker() {
        if (handler != null && scheduleChecker != null) {
            handler.removeCallbacks(scheduleChecker);
        }
    }

    private void loadResults() {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);

                        if (!success) {
                            String message = jsonResponse.optString("message", "Results are not available");
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "get_results");  // Make sure this matches your PHP case
                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }



    private void clearResults() {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL,
                response -> {
                    voteResults.clear();
                    adapter.notifyDataSetChanged();
                    tvScheduleTime.setText("Current schedule: Not set");
                    Toast.makeText(getContext(), "Results cleared", Toast.LENGTH_SHORT).show();
                    stopScheduleChecker();
                },
                error -> Toast.makeText(getContext(), "Failed to clear results", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "clear_vote_results");
                params.put("clear_schedule", "1"); // custom flag to indicate schedule reset
                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view, year, month, day) -> {
            TimePickerDialog timePicker = new TimePickerDialog(getContext(), (view1, hour, minute) -> {
                calendar.set(year, month, day, hour, minute);
                String formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.getTime());
                saveSchedule(formatted);
            }, 12, 0, true);
            timePicker.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void saveSchedule(String datetime) {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL,
                response -> {
                    tvScheduleTime.setText(datetime);
                    startScheduleChecker(datetime);
                    checkScheduleAndLoadResults(datetime);
                },
                error -> Toast.makeText(getContext(), "Failed to save", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "set_result_schedule");
                params.put("scheduled_at", datetime);
                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopScheduleChecker();
    }
}
