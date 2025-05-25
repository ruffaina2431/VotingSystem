package com.example.votingsystem.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;

public class UserRequest {

    public static void registerUser(
            Context context,
            String name,
            String email,
            String password,
            String role,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                listener,
                errorListener
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("role", role);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    public static void loginUser(
            Context context,
            String email,
            String password,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                listener,
                errorListener
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "login_user");
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }
}
