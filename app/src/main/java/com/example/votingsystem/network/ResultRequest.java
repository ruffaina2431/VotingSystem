package com.example.votingsystem.network;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class ResultRequest {

    public static void getResults(Context context,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener errorListener) {

        StringRequest request = new StringRequest(Request.Method.POST, ApiURLs.BASE_URL, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "get_results");
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }
}
