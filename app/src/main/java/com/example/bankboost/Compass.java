package com.example.bankboost;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Compass extends AppCompatActivity implements SensorEventListener, LocationListener {

    // Constants
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final float ALPHA = 0.97f; // Low-pass filter constant

    // UI elements
    private ImageView compassImage;
    private TextView degreeText;
    private TextView locationText;
    private TextView latitudeTextView;
    private TextView longitudeTextView;

    // Sensor variables
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;
    private float[] lastAccelerometerData = new float[3];
    private float[] lastMagnetometerData = new float[3];
    private boolean hasSensorData = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];

    // Location variables
    private LocationManager locationManager;

    // Filtered sensor data
    private float[] smoothedAccelerometerData = new float[3];
    private float[] smoothedMagnetometerData = new float[3];
    private float[] fusedOrientation = new float[3];
    private float[] currentOrientation = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        getSupportActionBar().setTitle("Compass");
        getSupportActionBar().hide();

        // Initialize UI elements
        compassImage = findViewById(R.id.compassImage);
        degreeText = findViewById(R.id.degreeTextView);
        locationText = findViewById(R.id.locationTextView);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);

        // Initialize sensor and location managers
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check and request location permission if not granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        } else {
            requestLocationUpdates();
        }
    }

    // Request location updates if permission granted
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listeners
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listeners and remove location updates
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometerSensor) {
            // Apply low-pass filter to accelerometer data
            smoothedAccelerometerData = lowPassFilter(event.values.clone(), smoothedAccelerometerData);
            hasSensorData = true;
        } else if (event.sensor == magnetometerSensor) {
            // Apply low-pass filter to magnetometer data
            smoothedMagnetometerData = lowPassFilter(event.values.clone(), smoothedMagnetometerData);
            hasSensorData = true;
        }

        if (hasSensorData) {
            // Get rotation matrix and orientation from sensor data
            SensorManager.getRotationMatrix(rotationMatrix, null, smoothedAccelerometerData, smoothedMagnetometerData);
            SensorManager.getOrientation(rotationMatrix, currentOrientation);

            // Apply complementary filter to fuse sensor data
            fusedOrientation[0] = ALPHA * fusedOrientation[0] + (1 - ALPHA) * currentOrientation[0];
            fusedOrientation[1] = ALPHA * fusedOrientation[1] + (1 - ALPHA) * currentOrientation[1];
            fusedOrientation[2] = ALPHA * fusedOrientation[2] + (1 - ALPHA) * currentOrientation[2];

            // Calculate azimuth in degrees and update compass image and degree text
            float azimuth = (float) Math.toDegrees(fusedOrientation[0]);
            azimuth = (azimuth + 360) % 360;
            compassImage.setRotation(-azimuth);
            String degree = String.format("%.2f", azimuth) + "°";
            degreeText.setText(degree);
        }
    }

    // Apply low-pass filter to input data
    private float[] lowPassFilter(float[] input, float[] output) {
        if (output == null) {
            return input;
        }

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }

        return output;
    }

    // LocationListener methods
    @Override
    public void onLocationChanged(Location location) {
        // Retrieve latitude and longitude
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Retrieve address from geocoder using latitude and longitude
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                locationText.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update latitude and longitude TextViews
        String latitudeString = "" + String.valueOf(latitude);
        String longitudeString = "" + String.valueOf(longitude);
        latitudeTextView.setText(latitudeString);
        longitudeTextView.setText(longitudeString);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Do nothing
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Do nothing
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Do nothing
    }
}