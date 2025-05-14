package com.example.votingsystem.network;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class VoteRequest {

    public static void cast(Context context, int studentId, int candidateId, String position,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {

        StringRequest request = new StringRequest(Request.Method.POST, ApiURLs.BASE_URL, listener, errorListener) {
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
}
