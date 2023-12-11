package com.example.bankboost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Forgot2 extends AppCompatActivity {

    TextView usernameText;
    EditText newPassword, confirmNewPassword;
    private Button btnProceed;
    private Button btnCancel;

    private DatabaseReference usersRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot2);
        getSupportActionBar().hide();

        newPassword = findViewById(R.id.newPassword);
        confirmNewPassword = findViewById(R.id.confirmNewPassword);
        btnProceed = findViewById(R.id.btnProceed);
        btnCancel = findViewById(R.id.btnCancel);
        usernameText = findViewById(R.id.usernameText);

        // Visibility icon for new password field
        ImageView newPasswordShowHide = findViewById(R.id.newPasswordShowHide);
        newPasswordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
        newPasswordShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPasswordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    newPasswordShowHide.setImageResource(R.drawable.baseline_visibility_24);
                }
            }
        });

        // Visibility icon for confirm new password field
        ImageView confirmNewPasswordShowHide = findViewById(R.id.confirmNewPasswordShowHide);
        confirmNewPasswordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
        confirmNewPasswordShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmNewPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    confirmNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmNewPasswordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    confirmNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmNewPasswordShowHide.setImageResource(R.drawable.baseline_visibility_24);
                }
            }
        });

        // Retrieve the username value from the Intent
        String username = getIntent().getStringExtra("username");

        // Set the username value in the usernameText TextView
        usernameText.setText(username);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = newPassword.getText().toString().trim();
                String confirmPassword = confirmNewPassword.getText().toString().trim();

                // Check if the password and confirm password fields are empty
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    newPassword.setError("Please enter a password in both fields!");
                    confirmNewPassword.setError("Please enter a password in both fields!");
                }
                // Check if the password and confirm password match
                else if (!password.equals(confirmPassword)) {
                    confirmNewPassword.setError("Passwords do not match!");
                }
                // Check if the password meets the length requirement
                else if (password.length() < 8) {
                    newPassword.setError("Password must be at least 8 characters long!");
                }
                // Check if the password contains at least one letter
                else if (!containsLetter(password)) {
                    newPassword.setError("Password must contain at least one letter!");
                }
                // Check if the password contains at least one number
                else if (!containsNumber(password)) {
                    newPassword.setError("Password must contain at least one number!");
                }
                // Check if the password contains at least one special character
                else if (!containsSpecialCharacter(password)) {
                    newPassword.setError("Password must contain at least one special character (-!@#$%^&*()_+=<>?/{}~|)");
                }
                // Check if the password meets the requirement of containing both letters, numbers, and special characters
                else if (!containsLetterAndNumber(password) || !containsSpecialCharacter(password)) {
                    newPassword.setError("Password must contain letters, numbers, and special characters!");
                }
                else {
                    // Update the password in the database
                    updatePassword(username, password);
                    // Log out the user
                    logout();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Toast.makeText(Forgot2.this, "Password Reset Cancelled!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword(String username, String password) {
        // Update the password in the database
        usersRef.child(username).child("password").setValue(password);
        Toast.makeText(Forgot2.this, "Password Reset Successful!", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        // Clear the saved user data from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.apply();

        // Start the login activity and clear the back stack
        Intent intent = new Intent(Forgot2.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Method to check if the password contains at least one letter
    private boolean containsLetter(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    // Method to check if the password contains at least one number
    private boolean containsNumber(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    // Method to check if the password contains at least one special character
    private boolean containsSpecialCharacter(String password) {
        String specialCharacters = "!@#$%^&*()_-+=<>?/{}~|";
        for (char c : password.toCharArray()) {
            if (specialCharacters.contains(Character.toString(c))) {
                return true;
            }
        }
        return false;
    }

    // Method to check if the password contains both letters and numbers
    private boolean containsLetterAndNumber(String password) {
        boolean hasLetter = false;
        boolean hasNumber = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }

            // Break the loop if both conditions are satisfied
            if (hasLetter && hasNumber) {
                break;
            }
        }

        return hasLetter && hasNumber;
    }
}