package com.example.votingsystem.network;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;

import java.util.HashMap;
import java.util.Map;

public class CandidateRequest {

    public static void getAllCandidates(Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    Log.d("CANDIDATE_RESPONSE", response); // Log the raw response
                    try {
                        listener.onResponse(new JSONObject(response));
                    } catch (JSONException e) {
                        Log.e("JSON_ERROR", "Error parsing data: " + e.getMessage());
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                },
                errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "read_candidates"); // <- match your PHP switch case
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    public static void addCandidate(Context context, String name, String position, String party,
                                    Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL, // Make sure this is your PHP endpoint URL
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.getBoolean("success");

                        if (!success) {
                            String message = json.optString("message", "Failed to add candidate.");
                            new AlertDialog.Builder(context)
                                    .setTitle("Error")
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }

                        // Always send response back to listener
                        listener.onResponse(json);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage("Invalid server response")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                },
                errorListener
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "create_candidate");
                params.put("name", name);
                params.put("position", position);
                params.put("party", party);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }



    public static void updateCandidate(Context context, int id, String name, String position, String party, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    try {
                        listener.onResponse(new JSONObject(response));
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                },
                errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "update_candidate");
                params.put("id", String.valueOf(id));
                params.put("name", name);
                params.put("position", position);
                params.put("party", party);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    public static void deleteCandidate(Context context, int candidateId,
                                       Response.Listener<JSONObject> listener,
                                       Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    try {
                        listener.onResponse(new JSONObject(response));
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                },
                errorListener
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "delete_candidate");
                params.put("id", String.valueOf(candidateId));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    public static void voteForCandidate(Context context, int studentId, int candidateId, String position, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {    StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    try {
                        listener.onResponse(new JSONObject(response));
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                },
                errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "cast_vote");
                params.put("student_id", String.valueOf(studentId));
                params.put("candidate_id", String.valueOf(candidateId));
                params.put("position", position);
                return params;
            }
    };
        Volley.newRequestQueue(context).add(request);
    }

    public static void getVoteResults(Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    try {
                        listener.onResponse(new JSONObject(response));
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                },
                errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "get_results");
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }
    public static void startElection(Context context,
                                     Response.Listener<JSONObject> listener,
                                     Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    try {
                        listener.onResponse(new JSONObject(response));
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                },
                errorListener
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "start_election");
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    public static void resetElection(Context context,
                                     Response.Listener<JSONObject> listener,
                                     Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    try {
                        listener.onResponse(new JSONObject(response));
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                },
                errorListener
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "reset_election");
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }


}
