package com.example.votingsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.votingsystem.network.ApiURLs;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPassword;
    Spinner spinnerRole;
    Button btnRegister;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);

        // Set Spinner with only "Student" and make it look transparent / disabled
        // Set Spinner with only "Student"
        String[] roles = {"Student"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

// Slight transparency, text still visible, and user can't interact
        spinnerRole.setEnabled(false);       // Disable interaction
        spinnerRole.setAlpha(0.6f);          // Slightly transparent (0.0 = fully transparent, 1.0 = fully visible)


        btnRegister.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Registration")
                    .setMessage("Are you sure you want to register?")
                    .setPositiveButton("Yes", (dialog, which) -> registerUser())
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String role = "student"; // hardcoded role

        // Validation
        if (name.isEmpty()) {
            edtName.setError("Name is required");
            edtName.requestFocus();
            return;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            edtName.setError("Only letters and spaces allowed");
            edtName.requestFocus();
            return;
        }

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        btnRegister.setEnabled(false);

        StringRequest request = new StringRequest(
                com.android.volley.Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    progressDialog.dismiss();
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("action", "register_user");
                data.put("name", name);
                data.put("email", email);
                data.put("password", password);
                data.put("role", role);
                return data;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
