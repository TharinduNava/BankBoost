package com.example.bankboost;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    // Declare necessary views and buttons
    TextView emailText, usernameText;
    Button btnEdit;
    Button btnReset;
    Button btnLogout;
    Button btnDeleteProfile;

    // Declare shared preferences for storing user data
    private SharedPreferences sharedPreferences;

    private static final int REQUEST_CODE_PROFILE_EDIT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize shared preferences
        sharedPreferences = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);

        // Retrieve email and username from shared preferences
        String email = sharedPreferences.getString("email", "");
        String username = sharedPreferences.getString("username", "");

        // Find and set up the email and username TextViews
        emailText = v.findViewById(R.id.emailText);
        emailText.setText(email);
        emailText.setFocusable(false);
        emailText.setClickable(false);

        usernameText = v.findViewById(R.id.usernameText);
        usernameText.setText(username);
        usernameText.setFocusable(false);
        usernameText.setClickable(false);

        // Find and set up the Edit button
        btnEdit = v.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the ProfileEdit activity for editing the profile
                Intent intent = new Intent(getContext(), ProfileEdit.class);
                startActivityForResult(intent, REQUEST_CODE_PROFILE_EDIT);
            }
        });

        // Find and set up the Reset button
        btnReset = v.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the Forgot1 activity for resetting the password
                Intent intent = new Intent(getContext(), Forgot1.class);
                startActivity(intent);
            }
        });

        // Find and set up the Logout button
        btnLogout = v.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display a confirmation dialog for logging out
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(Html.fromHtml("<b>Logout</b>"));
                builder.setMessage("Are you sure you want to logout of your account?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear login status in shared preferences
                        sharedPreferences = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", false);
                        editor.apply();

                        // Redirect to the MainActivity for logging out
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);

                        // Show a logout success message
                        Toast.makeText(getContext(), "Successfully logged out of your account!", Toast.LENGTH_SHORT).show();

                        // Finish the current activity
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });

        // Find and set up the Delete Profile button
        btnDeleteProfile = v.findViewById(R.id.btnDeleteProfile);
        btnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a confirmation dialog for deleting the account
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(Html.fromHtml("<b>Delete Account</b>"));
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call the deleteAccount() method to delete the account
                        deleteAccount();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });

        return v;
    }

    // Handle the result of the ProfileEdit activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE_EDIT && resultCode == RESULT_OK) {
            // Update the email and username TextViews with the edited values
            String updatedEmail = data.getStringExtra("email");
            String updatedUsername = data.getStringExtra("username");
            emailText.setText(updatedEmail);
            usernameText.setText(updatedUsername);

            // Update the email and username values in shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", updatedEmail);
            editor.putString("username", updatedUsername);
            editor.apply();

            // Show a success message for saving the details
            Toast.makeText(getActivity(), "Your details have been successfully saved!", Toast.LENGTH_SHORT).show();
        }
    }

    // Method for deleting the user's account
    private void deleteAccount() {
        // Get the current user's email from shared preferences
        sharedPreferences = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        // Delete the user's account from the Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query query = reference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get the user's ID
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    String userId = userSnapshot.getKey();

                    // Delete the user's account from the Firebase Realtime Database
                    DatabaseReference userRef = reference.child(userId);
                    userRef.removeValue();

                    // Clear the shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Redirect to the MainActivity after deleting the account
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                    // Display a success message for deleting the account
                    Toast.makeText(getActivity(), "Your account has been successfully deleted!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}