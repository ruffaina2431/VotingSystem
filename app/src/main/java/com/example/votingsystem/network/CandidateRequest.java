package com.example.votingsystem.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;

import java.util.HashMap;
import java.util.Map;
import com.example.votingsystem.util.SharedPrefManager;
public class CandidateRequest {

    public static void getAllCandidates(Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    Log.d("CANDIDATE_RESPONSE", response);
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
                params.put("action", "read_candidates");
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void addCandidate(Context context, String name, String position, String party,
                                    Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
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
                params.put("action", "create_candidate");
                params.put("name", name);
                params.put("position", position);
                params.put("party", party);
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void updateCandidate(Context context, int id, String name, String position, String party,
                                       Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
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
        VolleySingleton.getInstance(context).addToRequestQueue(request);
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
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void voteForCandidate(Context context, int studentId, int candidateId, String position,
                                        Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
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
                params.put("student_id", String.valueOf(studentId));
                params.put("candidate_id", String.valueOf(candidateId));
                params.put("position", position);
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void finalizeCandidates(Context context,
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
                errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "finalize_candidates");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(context).getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void checkFinalizationStatus(Context context,
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
                errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "check_finalization");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(context).getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void getVoteResults(Context context,
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
                errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "get_results");
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}