package com.example.votingsystem.network;

import static com.example.votingsystem.network.ApiURLs.BASE_URL;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.votingsystem.model.VoteResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultRequest {


    public interface ResultCallback {
        void onSuccess(List<VoteResult> results);
        void onFailure(String message);
    }

    public interface ScheduleCallback {
        void onSuccess(String schedule);
        void onFailure(String message);
    }

    public static void fetchResultSchedule(Context context, ScheduleCallback callback) {
        String url = BASE_URL + "?action=get_result_schedule";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            callback.onSuccess(obj.getString("release_datetime"));
                        } else {
                            callback.onFailure("No schedule set.");
                        }
                    } catch (JSONException e) {
                        callback.onFailure(e.getMessage());
                    }
                },
                error -> callback.onFailure(error.toString())
        );
        Volley.newRequestQueue(context).add(request);
    }

    public static void setResultSchedule(Context context, String datetime, ScheduleCallback callback) {
        String url = BASE_URL + "?action=set_result_schedule";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            callback.onSuccess(datetime);
                        } else {
                            callback.onFailure(obj.getString("message"));
                        }
                    } catch (JSONException e) {
                        callback.onFailure(e.getMessage());
                    }
                },
                error -> callback.onFailure(error.toString())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("release_datetime", datetime);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    public static void fetchVoteResults(Context context, ResultCallback callback) {
        String url = BASE_URL + "?action=get_results";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        List<VoteResult> list = new ArrayList<>();
                        if (obj.getBoolean("success")) {
                            JSONArray array = obj.getJSONArray("results");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject item = array.getJSONObject(i);
                                list.add(new VoteResult(
                                        item.getString("name"),
                                        item.getString("position"),
                                        item.getString("party"),
                                        item.getInt("vote_count")
                                ));
                            }
                            callback.onSuccess(list);
                        } else {
                            callback.onFailure("No results found.");
                        }
                    } catch (JSONException e) {
                        callback.onFailure(e.getMessage());
                    }
                },
                error -> callback.onFailure(error.toString())
        );
        Volley.newRequestQueue(context).add(request);
    }
}
