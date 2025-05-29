package com.example.votingsystem;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.votingsystem.network.ApiURLs;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = findViewById(R.id.etName);
        edtEmail = findViewById(R.id.etEmail);
        edtPassword = findViewById(R.id.etPassword);
        edtConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);



        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                checkEmailExists(edtEmail.getText().toString().trim());
            }
        });
    }

    private void checkEmailExists(String email) {

        btnRegister.setEnabled(false);

        StringRequest checkRequest = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    Log.d("CheckEmail", "Response: " + response);
                    Log.d("CheckEmail", "Raw response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean exists = jsonResponse.getBoolean("exists");

                        if (exists) {
                            btnRegister.setEnabled(true);
                            Toast.makeText(this, "Account already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            sendOTP(edtEmail.getText().toString().trim());
                        }
                    } catch (JSONException e) {
                        Log.e("CheckEmail", "Invalid JSON: " + response);
                        e.printStackTrace();
                        Log.e("CheckEmail", "JSON parsing error: " + e.getMessage());
                        btnRegister.setEnabled(true);
                        Toast.makeText(this, "Error checking email", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnRegister.setEnabled(true);
                    Log.e("CheckEmail", "Volley error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("CheckEmail", "HTTP status code: " + error.networkResponse.statusCode);
                        if (error.networkResponse.data != null) {
                            String errorMsg = new String(error.networkResponse.data);
                            Log.e("CheckEmail", "Error response: " + errorMsg);
                        }
                    }
                    Toast.makeText(this, "Error checking email", Toast.LENGTH_SHORT).show();
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("action", "check_email");
                data.put("email", email);
                return data;
            }
        };

        Volley.newRequestQueue(this).add(checkRequest);
    }
    private void sendOTP(String email) {
        StringRequest otpRequest = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    try {
                        Log.d("OTP_RESPONSE", response); // Log the raw response
                        JSONObject json = new JSONObject(response);
                        boolean success = json.getBoolean("success");
                        if (success) {

                            promptForOTP();
                        } else {
                            String message = json.optString("message", "Failed to send OTP");
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                            btnRegister.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Invalid response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        btnRegister.setEnabled(true);
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    btnRegister.setEnabled(true);
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("action", "send_otp");
                data.put("email", email);
                return data;
            }
        };
        otpRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // timeout in milliseconds (10s)
                0,     // max number of retries → 0 disables retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(this).add(otpRequest);
    }
    private void promptForOTP() {
        EditText input = new EditText(this);
        input.setHint("Enter 6-digit OTP");

        new AlertDialog.Builder(this)
                .setTitle("Email Verification")
                .setMessage("Check your email and enter the OTP to continue.")
                .setView(input)
                .setPositiveButton("Verify", (dialog, which) -> {
                    String enteredOTP = input.getText().toString().trim();
                    if (enteredOTP.length() == 6) {
                        btnRegister.setEnabled(false);

                        verifyOTP(edtEmail.getText().toString().trim(), enteredOTP);
                    } else {
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        btnRegister.setEnabled(true);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    btnRegister.setEnabled(true);
                })
                .show();
    }
    private void verifyOTP(String email, String otp) {
        StringRequest verifyRequest = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        String status = json.getString("status");

                        if (status.equals("verified")) {
                            registerUser(); // proceed to register
                        } else {
                            Toast.makeText(this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                            btnRegister.setEnabled(true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        btnRegister.setEnabled(true);
                    }
                },
                error -> {
                    Toast.makeText(this, "Verification error", Toast.LENGTH_SHORT).show();
                    btnRegister.setEnabled(true);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("action", "verify_otp");
                data.put("email", email);
                data.put("otp", otp);
                return data;
            }
        };

        Volley.newRequestQueue(this).add(verifyRequest);
    }





    // Rest of your existing methods (validateInputs and registerUser) remain the same
    private boolean validateInputs() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Name is required");
            edtName.requestFocus();
            return false;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            edtName.setError("Only letters and spaces allowed");
            edtName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter a valid email");
            edtEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.setError("Please confirm your password");
            edtConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Passwords do not match");
            edtConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }


    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();


        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    btnRegister.setEnabled(true);
                    new AlertDialog.Builder(this)
                            .setTitle("Success")
                            .setMessage("Registration successful!")
                            .setPositiveButton("OK", (dialog, which) -> {
                                finish(); // Close the activity *after* user taps OK
                            })
                            .show();

                },
                error -> {
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("action", "register_user");
                data.put("name", name);
                data.put("email", email);
                data.put("password", password);
                return data;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}