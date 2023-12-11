package com.example.bankboost;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CombankLocationFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean markersVisible = true;
    private Marker currentLocationMarker;
    private List<Marker> customMarkers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_combank_location, container, false);

        setHasOptionsMenu(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.combankMap);
        mapFragment.getMapAsync(this);

        // Setting on click listener for the compass icon
        ImageView floatingCompass = v.findViewById(R.id.floatingCompass);
        floatingCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCompassActivity();
            }
        });

        // Setting on click listener for the bank icon
        ImageView floatingBank = v.findViewById(R.id.floatingBank);
        floatingBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMarkersVisibility();
            }
        });

        return v;
    }

    // Starting the compass activity
    private void showCompassActivity() {
        Intent intent = new Intent(getContext(), Compass.class);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Checking for location permissions
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Requesting for location permissions if not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

        // Getting the user's current location
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            // Adding a marker for the user's current location
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title("My Location");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocationimg));
            currentLocationMarker = mMap.addMarker(markerOptions);

            // Moving the camera to the user's current location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 6));
        }

        // Enabling various map settings
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Retrieving bank branch data from Firebase Realtime Database and add markers to the places
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference commercialRef = database.getReference("banks/Commercial");

        commercialRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Looping through the dataSnapshot to get bank branch data from Firebase Realtime Database
                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    String placeName = placeSnapshot.getKey();
                    double latitude = placeSnapshot.child("latitude").getValue(Double.class);
                    double longitude = placeSnapshot.child("longitude").getValue(Double.class);
                    String title = placeSnapshot.child("title").getValue(String.class);
                    addMarker(latitude, longitude, title, mMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Adding custom markers to the map of the bank branches
    private Marker addMarker(double latitude, double longitude, String title, GoogleMap mMap) {
        LatLng customLocation = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(customLocation)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapbankimg));
        Marker marker = mMap.addMarker(markerOptions);
        customMarkers.add(marker);
        return marker;
    }

    // Toggle the visibility of the markers on the map to display only the nearest bank branch
    private void toggleMarkersVisibility() {
        if (markersVisible) {
            hideMarkersExceptNearest();
        } else {
            showAllMarkers();
        }

        markersVisible = !markersVisible;
    }

    // Hide all markers except the nearest one to the user's current location
    private void hideMarkersExceptNearest() {
        for (Marker marker : customMarkers) {
            if (marker != currentLocationMarker) {
                marker.setVisible(false);
            }
        }

        Marker nearestMarker = findNearestMarker(currentLocationMarker);

        if (nearestMarker != null) {
            nearestMarker.setVisible(true);
        }
    }

    // Show all markers on the map back
    private void showAllMarkers() {
        for (Marker marker : customMarkers) {
            marker.setVisible(true);
        }
    }

    // Finding the nearest marker of bank branch to the user's current location
    private Marker findNearestMarker(Marker currentLocationMarker) {
        Marker nearestMarker = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Marker marker : customMarkers) {
            if (marker != currentLocationMarker) {
                double distance = calculateDistance(currentLocationMarker.getPosition(), marker.getPosition());
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearestMarker = marker;
                }
            }
        }

        return nearestMarker;
    }

    // Calculating the distance between two markers using the Haversine formula
    private double calculateDistance(LatLng point1, LatLng point2) {
        double lat1 = point1.latitude;
        double lon1 = point1.longitude;
        double lat2 = point2.latitude;
        double lon2 = point2.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344; // Converting to kilometers

        return dist;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Handling menu item selection with 4 types of map views
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normalMap:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.satelliteMap:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.hybridMap:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.terrainMap:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}