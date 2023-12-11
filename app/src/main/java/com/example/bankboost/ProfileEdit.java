package com.example.bankboost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ProfileEdit extends AppCompatActivity {

    Button btnCancel;
    Button btnUpdateEmail;
    Button btnUpdateUsername;

    private EditText emailEdit, usernameEdit;
    private SharedPreferences sharedPreferences;

    private DatabaseReference reference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String username = sharedPreferences.getString("username", "");

        emailEdit = findViewById(R.id.emailText);
        emailEdit.setText(email);

        usernameEdit = findViewById(R.id.usernameText);
        usernameEdit.setText(username);

        reference = FirebaseDatabase.getInstance().getReference("users");
        userId = sharedPreferences.getString("userId", "");

        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(ProfileEdit.this, "Save Profile Canceled!", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdateEmail = findViewById(R.id.btnUpdateEmail);
        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedEmail = emailEdit.getText().toString().trim();
                String updatedUsername = usernameEdit.getText().toString().trim();
                String currentUsername = sharedPreferences.getString("username", "");

                // Check if the email is in the proper format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(updatedEmail).matches()) {
                    emailEdit.setError("Please enter a valid email address!");
                    return;
                }

                // Check if the email field is empty
                if (TextUtils.isEmpty(updatedEmail)) {
                    emailEdit.setError("Please fill in your email address!");
                    return;
                }

                // Check if the email already exists
                reference.orderByChild("email").equalTo(updatedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            emailEdit.setError("This email address is already in use. Please choose a different email address!");
                        } else {
                            // Update the email in the Firebase Realtime Database
                            if (!updatedEmail.equals(sharedPreferences.getString("email", ""))) {
                                reference.child(userId).child(currentUsername).child("email").setValue(updatedEmail);
                                sharedPreferences.edit().putString("email", updatedEmail).apply();
                            }

                            Intent intent = new Intent();
                            intent.putExtra("email", updatedEmail);
                            intent.putExtra("username", updatedUsername);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            }
        });

        btnUpdateUsername = findViewById(R.id.btnUpdateUsername);
        btnUpdateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedUsername = usernameEdit.getText().toString().trim();
                String updatedEmail = emailEdit.getText().toString().trim();
                String currentUsername = sharedPreferences.getString("username", "");

                // Check if the username field is empty
                if (TextUtils.isEmpty(updatedUsername)) {
                    usernameEdit.setError("Please fill in your username!");
                    return;
                }

                // Check if the new username already exists
                reference.child(userId).child(updatedUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // The new username already exists, display an error message
                            usernameEdit.setError("This username already exists!");
                        } else {
                            // The new username does not exist, update the username in the Firebase Realtime Database
                            if (!updatedUsername.equals(currentUsername)) {
                                reference.child(userId).child(currentUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get the old node's data
                                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                                        // Create a new node with the updated username
                                        reference.child(userId).child(updatedUsername).setValue(data);

                                        // Remove the old node
                                        reference.child(userId).child(currentUsername).removeValue();

                                        // Update the username attribute with the new username value
                                        reference.child(userId).child(updatedUsername).child("username").setValue(updatedUsername);

                                        // Update the shared preferences
                                        sharedPreferences.edit().putString("username", updatedUsername).apply();

                                        Intent intent = new Intent();
                                        intent.putExtra("username", updatedUsername);
                                        intent.putExtra("email", updatedEmail);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle errors
                                    }
                                });

                            } else {
                                // The username hasn't changed, update the shared preferences and return to the previous activity
                                sharedPreferences.edit().putString("username", updatedUsername).apply();

                                Intent intent = new Intent();
                                intent.putExtra("username", updatedUsername);
                                intent.putExtra("email", updatedEmail);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            }
        });
    }
}
