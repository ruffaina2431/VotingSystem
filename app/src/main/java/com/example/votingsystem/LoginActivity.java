package com.example.votingsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.votingsystem.network.ApiURLs;

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

        // Optionally check password length
        if (password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
            return;
        }

        // Proceed with login request
        StringRequest request = new StringRequest(
                com.android.volley.Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONObject user = obj.getJSONObject("user");
                            int userId = user.getInt("id");
                            String role = user.getString("role");

                            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                            prefs.edit().putInt("user_id", userId).apply();

                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                            if (role.equals("admin")) {
                                startActivity(new Intent(this, AdminHomeActivity.class));
                            } else {
                                startActivity(new Intent(this, HomeActivity.class));
                            }

                            finish();
                        } else {
                            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
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
