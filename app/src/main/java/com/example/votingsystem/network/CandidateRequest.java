package com.example.votingsystem.network;

import android.content.Context;
import android.util.Log;

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
                ApiURLs.BASE_URL, // Using your single POST endpoint
                response -> {
                    try {
                        listener.onResponse(new JSONObject(response)); // Parsing the response
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(new VolleyError(e)); // Handling error
                    }
                },
                errorListener
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "create_candidate"); // Action to create a candidate
                params.put("name", name); // Candidate's name
                params.put("position", position); // Candidate's position
                params.put("party", party); // Candidate's party
                return params; // Return the parameters to be sent to the server
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request); // Use Singleton to add to the request queue
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

    public static void voteForCandidate(Context context, int candidateId, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
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
                params.put("action", "cast_vote");
                params.put("candidate_id", String.valueOf(candidateId));
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
                params.put("action", "get_vote_results");
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }


}
