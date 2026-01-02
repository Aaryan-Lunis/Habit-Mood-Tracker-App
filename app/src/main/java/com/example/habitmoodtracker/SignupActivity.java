package com.example.habitmoodtracker;

import com.example.habitmoodtracker.AuthManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonSignup;
    private TextView textViewLogin;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        authManager = AuthManager.getInstance(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSignup = findViewById(R.id.buttonSignup);
        textViewLogin = findViewById(R.id.textViewLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        buttonSignup.setOnClickListener(v -> signupUser());

        textViewLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void signupUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (!validateInputs(email, password, confirmPassword)) {
            return;
        }

        setLoadingState(true);

        authManager.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    setLoadingState(false);

                    if (task.isSuccessful()) {
                        String userId = authManager.getCurrentUserId();
                        authManager.saveUserSession(userId, email);

                        Toast.makeText(SignupActivity.this,
                                "Account created successfully! 🎉", Toast.LENGTH_SHORT).show();

                        navigateToMainActivity();
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Signup failed";
                        Toast.makeText(SignupActivity.this,
                                "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
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

        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError("Please confirm your password");
            editTextConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonSignup.setEnabled(!isLoading);
        editTextEmail.setEnabled(!isLoading);
        editTextPassword.setEnabled(!isLoading);
        editTextConfirmPassword.setEnabled(!isLoading);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}