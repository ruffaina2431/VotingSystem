package com.example.votingsystem.fragment;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingsystem.R;
import com.example.votingsystem.adapter.AdminVoteResultsAdapter;
import com.example.votingsystem.model.VoteResult;
import com.example.votingsystem.network.ResultRequest;

import java.text.SimpleDateFormat;
import java.util.*;


public class AdminVoteResultsFragment extends Fragment {

    private TextView textCurrentSchedule, txtNoCandidates;
    private Button btnSetSchedule;
    private RecyclerView recyclerView;
    private AdminVoteResultsAdapter adapter;
    private List<VoteResult> resultList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_vote_results, container, false);

        textCurrentSchedule = view.findViewById(R.id.textCurrentSchedule);
        txtNoCandidates = view.findViewById(R.id.txtNoCandidates);
        btnSetSchedule = view.findViewById(R.id.btnSetSchedule);
        recyclerView = view.findViewById(R.id.recycler_view_vote_results);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultList = new ArrayList<>();
        adapter = new AdminVoteResultsAdapter(resultList);
        recyclerView.setAdapter(adapter);

        loadSchedule();
        loadResults();

        btnSetSchedule.setOnClickListener(v -> showDateTimeDialog());

        return view;
    }

    private void loadSchedule() {
        ResultRequest.fetchResultSchedule(requireContext(), new ResultRequest.ScheduleCallback() {
            @Override
            public void onSuccess(String schedule) {
                textCurrentSchedule.setText("Current schedule: " + schedule);
            }

            @Override
            public void onFailure(String message) {
                textCurrentSchedule.setText("Current schedule: Not set");
            }
        });
    }

    private void loadResults() {
        ResultRequest.fetchVoteResults(requireContext(), new ResultRequest.ResultCallback() {
            @Override
            public void onSuccess(List<VoteResult> results) {
                resultList.clear();
                resultList.addAll(results);
                txtNoCandidates.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {
                txtNoCandidates.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showDateTimeDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePicker = new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String datetime = sdf.format(calendar.getTime());

                ResultRequest.setResultSchedule(requireContext(), datetime, new ResultRequest.ScheduleCallback() {
                    @Override
                    public void onSuccess(String schedule) {
                        Toast.makeText(getContext(), "Schedule set!", Toast.LENGTH_SHORT).show();
                        textCurrentSchedule.setText("Current schedule: " + schedule);
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

            timePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }
}
