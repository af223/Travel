package com.codepath.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    
    private EditText etName;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etName.getText().toString();
                String password = etPassword.getText().toString();
                if (username.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Need a username", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Need a password", Toast.LENGTH_SHORT).show();
                } else {
                    login(username, password);
                }
            }
        });
    }

    private void login(String username, String password) {
    }
}