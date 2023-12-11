package com.example.bankboost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    EditText signupUsername, signupEmail, signupPassword, signupConfirmPassword;

    Button btnRegister;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize EditText fields and buttons
        signupEmail = findViewById(R.id.email);
        signupUsername = findViewById(R.id.username);
        signupPassword = findViewById(R.id.password);
        signupConfirmPassword = findViewById(R.id.confirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Show/hide password functionality
        ImageView passwordShowHide = findViewById(R.id.passwordShowHide);
        passwordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
        passwordShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signupPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordShowHide.setImageResource(R.drawable.baseline_visibility_24);
                }
            }
        });

        // Show/hide confirm password functionality
        ImageView confirmPasswordShowHide = findViewById(R.id.confirmPasswordShowHide);
        confirmPasswordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
        confirmPasswordShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signupConfirmPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    signupConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPasswordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    signupConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPasswordShowHide.setImageResource(R.drawable.baseline_visibility_24);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get an instance of the Firebase database
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                // Retrieve user input
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();
                String confirmPassword = signupConfirmPassword.getText().toString();

                // Validate email address using regular expression
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (TextUtils.isEmpty(email)) {
                    signupEmail.setError("Email is required!");
                    return;
                } else if (!email.matches(emailPattern)) {
                    signupEmail.setError("Invalid email address!");
                    return;
                }

                // Validate username
                if (TextUtils.isEmpty(username)) {
                    signupUsername.setError("Username is required!");
                    return;
                }

                // Validate password
                if (TextUtils.isEmpty(password)) {
                    signupPassword.setError("Password is required!");
                    return;
                }
                if (password.length() < 8) {
                    signupPassword.setError("Password must be at least 8 characters long!");
                    return;
                }
                if (!password.matches(".*\\d.*")) {
                    signupPassword.setError("Password must contain at least one digit!");
                    return;
                }
                if (!password.matches(".*[a-zA-Z].*")) {
                    signupPassword.setError("Password must contain at least one letter!");
                    return;
                }
                if (!password.matches(".*[-!@#$%^&*()_+=<>?/{}~|].*")) {
                    signupPassword.setError("Password must contain at least one special character (-!@#$%^&*()_+=<>?/{}~|)");
                    return;
                }

                // Validate password confirmation
                if (!password.equals(confirmPassword)) {
                    signupConfirmPassword.setError("Passwords do not match!");
                    return;
                }

                // Check if the username and password are the same
                if (username.equals(password)) {
                    signupUsername.setError("Username and password cannot be the same!");
                    return;
                }

                // Check if the email already exists in the database
                reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Username already exists, show error message
                            signupUsername.setError("Username already exists!");
                        } else {
                            // Username does not exist, proceed with email check
                            reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Email already exists, show error message
                                        signupEmail.setError("Email already exists!");
                                    } else {
                                        // Email and username do not exist, save user data to database
                                        HelperClass helperClass = new HelperClass(email, username, password);
                                        reference.child(username).setValue(helperClass);

                                        Toast.makeText(Register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle database error
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database error
                    }
                });
            }
        });

    }
}