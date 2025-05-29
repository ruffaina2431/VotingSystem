package com.example.votingsystem.fragment;
import com.example.votingsystem.UserVote;
import static com.example.votingsystem.network.ApiURLs.BASE_URL;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        Button btnDownloadPdf = view.findViewById(R.id.btnDownloadPdf);


        voteResults = new ArrayList<>();
        adapter = new AdminVoteResultsAdapter(voteResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchSchedule();

        btnSetSchedule.setOnClickListener(v -> showDateTimePicker());
        btnClearResults.setOnClickListener(v -> clearResults());
        btnDownloadPdf.setOnClickListener(v -> {
            if (voteResults.isEmpty()) {
                Toast.makeText(requireContext(), "No results to export", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create PDF document
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(14);

            int x = 40, y = 50;
            canvas.drawText("Vote Results Report", x, y, paint);
            y += 30;

            // Draw header
            canvas.drawText("Candidate Name | Position | Party | Votes", x, y, paint);
            y += 30;
            canvas.drawText("---------------------------------------------", x, y, paint);
            y += 20;

            // Draw each vote result
            for (VoteResult result : voteResults) {
                String line = result.getName() + " | " + result.getPosition() + " | " +
                        result.getParty() + " | " + result.getVoteCount();
                canvas.drawText(line, x, y, paint);
                y += 20;

                if (y > 800) {  // New page if running out of space
                    pdfDocument.finishPage(page);
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = 50;
                }
            }

            pdfDocument.finishPage(page);

            // Save the PDF file to the public Downloads directory
            // Auto-renaming if file exists
            String baseFileName = "vote_results";
            String fileExtension = ".pdf";
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File pdfFile = new File(downloadsDir, baseFileName + fileExtension);
            int fileIndex = 1;

            while (pdfFile.exists()) {
                pdfFile = new File(downloadsDir, baseFileName + "(" + fileIndex + ")" + fileExtension);
                fileIndex++;
            }

            try (FileOutputStream out = new FileOutputStream(pdfFile)) {
                pdfDocument.writeTo(out);
                Toast.makeText(requireContext(), "PDF saved: " + pdfFile.getName(), Toast.LENGTH_LONG).show();
                logAuditAction("Admin downloaded vote result PDF");
            } catch (IOException e) {
                Toast.makeText(requireContext(), "PDF save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            pdfDocument.close();
        });


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
    private void generatePDF(String codeResult) {
        PdfDocument pdfDocument = new PdfDocument();

        // Define page size: A4 (595x842 points)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);

        // Draw code result on canvas
        int x = 40, y = 50;
        for (String line : codeResult.split("\n")) {
            canvas.drawText(line, x, y, paint);
            y += 20;
        }

        pdfDocument.finishPage(page);

        // Save to Downloads folder
        File pdfFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "voting result.pdf");


        try {
            FileOutputStream out = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(out);
            Toast.makeText(requireContext(), "PDF saved", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "PDF not saved!", Toast.LENGTH_SHORT).show();

        }

        pdfDocument.close();
    }



    /*
    private void generatePDFWithVoteResults(List<VoteResult> voteResults, List<UserVote> userVotes) {
        if (voteResults.isEmpty() || userVotes.isEmpty()) {
            Toast.makeText(requireContext(), "No results to export", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        // Setup paints
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(18);
        titlePaint.setFakeBoldText(true);

        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        // 1. Summary Page
        PdfDocument.PageInfo summaryPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page summaryPage = pdfDocument.startPage(summaryPageInfo);
        Canvas summaryCanvas = summaryPage.getCanvas();

        int y = 50;
        summaryCanvas.drawText("Election Results Summary", 40, y, titlePaint);
        y += 30;

        // Draw table headers
        summaryCanvas.drawText("Candidate", 40, y, paint);
        summaryCanvas.drawText("Position", 200, y, paint);
        summaryCanvas.drawText("Party", 350, y, paint);
        summaryCanvas.drawText("Votes", 450, y, paint);
        y += 20;

        // Draw separator line
        summaryCanvas.drawLine(40, y, 550, y, paint);
        y += 20;

        // Add vote results
        for (VoteResult result : voteResults) {
            summaryCanvas.drawText(result.getName(), 40, y, paint);
            summaryCanvas.drawText(result.getPosition(), 200, y, paint);
            summaryCanvas.drawText(result.getParty(), 350, y, paint);
            summaryCanvas.drawText(String.valueOf(result.getVoteCount()), 450, y, paint);
            y += 20;

            if (y > 800) {
                y = 50;
                pdfDocument.finishPage(summaryPage);
                summaryPage = pdfDocument.startPage(summaryPageInfo);
                summaryCanvas = summaryPage.getCanvas();
            }
        }
        pdfDocument.finishPage(summaryPage);

        // 2. Detailed Voter Breakdown
        PdfDocument.PageInfo detailPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 2).create();
        PdfDocument.Page detailPage = pdfDocument.startPage(detailPageInfo);
        Canvas detailCanvas = detailPage.getCanvas();

        y = 50;
        detailCanvas.drawText("Detailed Voter Breakdown", 40, y, titlePaint);
        y += 30;

        // Group votes by candidate
        Map<String, List<UserVote>> votesByCandidate = new HashMap<>();
        for (UserVote vote : userVotes) {
            String key = vote.getCandidateName() + "|" + vote.getPosition();
            if (!votesByCandidate.containsKey(key)) {
                votesByCandidate.put(key, new ArrayList<>());
            }
            votesByCandidate.get(key).add(vote);
        }

        // Add voter details for each candidate
        for (Map.Entry<String, List<UserVote>> entry : votesByCandidate.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            String candidateName = parts[0];
            String position = parts[1];

            // Candidate header
            detailCanvas.drawText(candidateName + " for " + position, 40, y, paint);
            y += 20;

            // Column headers
            detailCanvas.drawText("Voter Name", 40, y, paint);
            detailCanvas.drawText("Voted At", 300, y, paint);
            y += 20;

            // Separator line
            detailCanvas.drawLine(40, y, 550, y, paint);
            y += 20;

            // Voter details
            for (UserVote vote : entry.getValue()) {
                detailCanvas.drawText(vote.getUserName(), 40, y, paint);
                detailCanvas.drawText(vote.getVotedAt(), 300, y, paint);
                y += 20;

                if (y > 800) {
                    y = 50;
                    pdfDocument.finishPage(detailPage);
                    detailPage = pdfDocument.startPage(detailPageInfo);
                    detailCanvas = detailPage.getCanvas();
                }
            }

            y += 10; // Space between candidates
        }
        pdfDocument.finishPage(detailPage);

        // Save PDF
        File pdfFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "election_results_" + System.currentTimeMillis() + ".pdf");

        try (FileOutputStream out = new FileOutputStream(pdfFile)) {
            pdfDocument.writeTo(out);
            Toast.makeText(requireContext(), "PDF saved to " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Log audit action
            logAuditAction("Admin downloaded detailed vote results PDF");

            // Open the PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(requireContext(),
                            requireContext().getPackageName() + ".provider", pdfFile),
                    "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }
    */
    // Inside AdminVoteResultsFragment.java
    private void logAuditAction(String actionDesc) {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL,
                response -> Log.d("AUDIT_LOG", "Logged: " + actionDesc),
                error -> Log.e("AUDIT_LOG", "Failed: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("action", "log_audit");
                data.put("admin_id", "1"); // Replace with dynamic admin ID
                data.put("description", actionDesc);
                data.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                return data;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

}
