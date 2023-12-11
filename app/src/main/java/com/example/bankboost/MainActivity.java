package com.example.bankboost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    // Declare variables for views
    private EditText loginUsername, loginPassword;
    private TextView signupText;
    private Button btnLogin, btnForgot;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Initialize views
        loginUsername = findViewById(R.id.username);
        loginPassword = findViewById(R.id.password);

        // Show/hide password functionality
        ImageView passwordShowHide = findViewById(R.id.passwordShowHide);
        passwordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
        passwordShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordShowHide.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordShowHide.setImageResource(R.drawable.baseline_visibility_24);
                }
            }
        });

        // Handle click on "Sign Up" text
        signupText = findViewById(R.id.signupText);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterText();
            }
        });

        // Handle click on "Forgot Password" button
        btnForgot = findViewById(R.id.btnForgot);
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgot1();
            }
        });

        // Handle click on "Login" button
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate input fields
                if (!validateUsername() && !validatePassword()) {
                    return;
                }
                // Check user credentials
                checkUser();
            }
        });

        // Check if the user is already logged in
        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // If logged in, redirect to home activity
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    // Validate the username field
    private boolean validateUsername() {
        String val = loginUsername.getText().toString().trim();
        if (val.isEmpty()) {
            loginUsername.setError("Username Field Cannot Be Empty!");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    // Validate the password field
    private boolean validatePassword() {
        String val = loginPassword.getText().toString().trim();
        if (val.isEmpty()) {
            loginPassword.setError("Password Field Cannot Be Empty!");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    // Check user credentials in the database
    private void checkUser() {
        String userInput = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase;
        if (userInput.contains("@")) {
            // User entered an email address
            checkUserDatabase = reference.orderByChild("email").equalTo(userInput);
        } else {
            // User entered a username
            checkUserDatabase = reference.orderByChild("username").equalTo(userInput);
        }

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = dataSnapshot.child("password").getValue(String.class);
                        if (passwordFromDB.equals(userPassword)) {
                            // Save user login status and information in shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            String emailFromDB = dataSnapshot.child("email").getValue(String.class);
                            String usernameFromDB = dataSnapshot.child("username").getValue(String.class);
                            editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("email", emailFromDB);
                            editor.putString("username", usernameFromDB);
                            editor.apply();

                            // Show a toast message with customized text
                            SpannableStringBuilder builder = new SpannableStringBuilder();

                            // Add the username to the builder
                            String username = " " + usernameFromDB + " ";
                            SpannableString usernameSpannable = new SpannableString(username);
                            usernameSpannable.setSpan(new RelativeSizeSpan(1.2f), 0, username.length(), 0);
                            usernameSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, username.length(), 0);

                            // Add the rest of the message to the builder
                            builder.append("Login Successful! Welcome ");
                            builder.append(usernameSpannable);

                            // Create a Toast message with the customized text including the username
                            Toast toast = Toast.makeText(MainActivity.this, builder, Toast.LENGTH_SHORT);
                            toast.show();

                            // Redirect to home activity
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            startActivity(intent);
                            finish();
                        } else {
                            loginPassword.setError("Invalid Password!");
                            loginPassword.requestFocus();
                        }
                    }
                } else {
                    loginUsername.setError("User does not exist!");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Open the Register activity
    public void openRegisterText() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    // Open the Forgot1 activity
    public void openForgot1() {
        Intent intent = new Intent(this, Forgot1.class);
        startActivity(intent);
    }
}