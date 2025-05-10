package com.example.votingsystem;

// LoginActivity.java
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private static final String LOGIN_URL = "http://your-wamp-server-ip/androidvotingsystem/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else {
                new LoginTask().execute(username, password);
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            try {
                URL url = new URL(LOGIN_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON request
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", username);
                jsonParam.put("password", password);

                // Send request
                OutputStream os = connection.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                // Get response
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();

                return response.toString();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result != null) {
                    JSONObject jsonResponse = new JSONObject(result);
                    if (jsonResponse.getBoolean("success")) {
                        JSONObject user = jsonResponse.getJSONObject("user");
                        String role = user.getString("role");

                        // Successful login - redirect based on role
                        Intent intent;
                        if (role.equals("admin")) {
                            intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, HomeActivity.class);
                        }

                        // Pass user data to next activity
                        intent.putExtra("userId", user.getInt("id"));
                        intent.putExtra("fullName", user.getString("fullName"));
                        intent.putExtra("role", role);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
