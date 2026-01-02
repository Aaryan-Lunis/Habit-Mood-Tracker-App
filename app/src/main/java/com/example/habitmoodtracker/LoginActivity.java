package com.example.habitmoodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitmoodtracker.AuthManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignup, textViewForgotPassword;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = AuthManager.getInstance(this);

        // Check if user is already logged in
        if (authManager.isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignup = findViewById(R.id.textViewSignup);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> loginUser());

        textViewSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        textViewForgotPassword.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
                return;
            }
            resetPassword(email);
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return;
        }

        setLoadingState(true);

        authManager.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    setLoadingState(false);

                    if (task.isSuccessful()) {
                        String userId = authManager.getCurrentUserId();
                        authManager.saveUserSession(userId, email);

                        Toast.makeText(LoginActivity.this,
                                "Welcome back! 🎉", Toast.LENGTH_SHORT).show();

                        navigateToMainActivity();
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Login failed";
                        Toast.makeText(LoginActivity.this,
                                "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);

        authManager.getAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Password reset email sent! Check your inbox.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "Failed to send reset email: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonLogin.setEnabled(!isLoading);
        editTextEmail.setEnabled(!isLoading);
        editTextPassword.setEnabled(!isLoading);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}