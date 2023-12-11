package com.example.bankboost;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Typing animation for the Splash Screen
        TypingAnimation text = findViewById(R.id.splashPhrase);
        text.setText("");
        text.setCharacterAdder(80);
        text.animateText("Unlock the best rates,\n effortlessly...!");

        requestLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the location permission is enabled after coming back to the Splash Activity, it gets checked here
        if (isLocationPermissionGranted()) {
            startMainActivity();
        }
    }

    private void requestLocationPermission() {
        if (isLocationPermissionGranted()) {
            // If location permission is already granted, then it starts the Main Activity
            startMainActivity();
        } else {
            // If the permission is not granted then it asks for it again
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 5000);
    }

    private void showPermissionAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Permission Required");
        builder.setMessage("The app needs location access to function properly.");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If location permission is granted, then it starts the main activity
                startMainActivity();
            } else {
                // If location permission is denied, it shows permission dialog with settings option
                showPermissionAlertDialog();
            }
        }
    }
}