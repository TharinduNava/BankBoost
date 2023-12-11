package com.example.bankboost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Combank extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    // Fragments for different sections of the Commercial Bank Activity
    CombankDetailsFragment combankDetailsFragment = new CombankDetailsFragment();
    CombankRatesFragment combankRatesFragment = new CombankRatesFragment();
    CombankLocationFragment combankLocationFragment = new CombankLocationFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combank);

        // Setting the title for the Activity's action bar
        getSupportActionBar().setTitle("Commercial Bank");

        // Enabling the back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNavigationView  = findViewById(R.id.bottom_navigation_bank);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,combankDetailsFragment).commit();

        // Setting a listener to handle item selection in the bottom navigation bar
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.details:
                        // Replacing the current fragment with the CombankDetailsFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,combankDetailsFragment).commit();
                        return true;
                    case R.id.rates:
                        // Replacing the current fragment with the CombankRatesFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,combankRatesFragment).commit();
                        return true;
                    case R.id.locations:
                        // Replacing the current fragment with the CombankLocationsFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,combankLocationFragment).commit();
                        return true;
                }

                return false;
            }
        });

    }
}