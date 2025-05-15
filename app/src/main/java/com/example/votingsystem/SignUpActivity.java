package com.example.votingsystem;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);

        // Populate Spinner with roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString().toLowerCase();

        StringRequest request = new StringRequest(
                com.android.volley.Request.Method.POST,
                ApiURLs.BASE_URL,
                response -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to login screen
                },
                error -> {
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
