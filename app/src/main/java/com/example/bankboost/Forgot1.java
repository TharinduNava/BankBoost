package com.example.bankboost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Forgot1 extends AppCompatActivity {

    EditText usernameEditText;
    private Button btnNext;

    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot1);
        getSupportActionBar().hide();

        usernameEditText = findViewById(R.id.usernameEditText);
        btnNext = findViewById(R.id.btnNext);
        btnCancel = findViewById(R.id.btnCancel);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();

                if (username.isEmpty()) {
                    // Display error message when the field is empty
                    usernameEditText.setError("Username Field cannot be empty!");
                } else {
                    // Check if the username exists in the database
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                    reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Username exists, pass the username to the next screen
                                openNext(username);
                            } else {
                                // Username does not exist, display error message
                                usernameEditText.setError("Invalid username! Please provide the accurate username linked to your account!");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // Starting the Forgot2 class while displaying a toast message
    public void openNext(String username) {
        Intent intent = new Intent(this, Forgot2.class);
        intent.putExtra("username", username);
        startActivity(intent);
        Toast.makeText(this, "You can now successfully update the password for your account!", Toast.LENGTH_SHORT).show();
    }

}