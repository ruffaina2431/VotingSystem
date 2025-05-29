package com.example.votingsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.votingsystem.network.ApiURLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin, btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v -> loginUser());
        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();



        // Validate input
        if (email.isEmpty()) {
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter a valid email");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
            return;
        }

        if (email.equals("admin2025@gmail.com") && password.equals("AdminTo2025")) {
            // Save admin session
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            prefs.edit().putInt("user_id", 0).apply(); // 0 = reserved for hardcoded admin

            Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminHomeActivity.class));
            finish();
            return; // Skip server login
        }

        // Disable login button during request
        btnLogin.setEnabled(false);

        // Proceed with login request
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    btnLogin.setEnabled(true);

                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("success")) {
                            // Successful login
                            JSONObject user = obj.getJSONObject("user");
                            int userId = user.getInt("id");
                            String role = user.getString("role");

                            // Save user session
                            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                            prefs.edit().putInt("user_id", userId).apply();

                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // Redirect based on role
                            if (role.equalsIgnoreCase("admin")) {
                                startActivity(new Intent(this, AdminHomeActivity.class));
                            } else {
                                startActivity(new Intent(this, HomeActivity.class));
                            }
                            finish();
                        } else {
                            // Handle different error cases
                            String errorMessage = "Invalid credentials";
                            if (obj.has("message")) {
                                errorMessage = obj.getString("message");

                                // Specific handling for password mismatch
                                if (errorMessage.toLowerCase().contains("password")) {
                                    edtPassword.setError("Incorrect password");
                                    edtPassword.requestFocus();
                                }
                            }
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("LoginError", "JSON parsing error", e);
                        Toast.makeText(this, "Login error occurred", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LoginError", "Network error", error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("action", "login_user");
                data.put("email", email);
                data.put("password", password);
                return data;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}