package com.example.bankboost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BOC extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    // Fragments for different sections of the Bank of Ceylon Activity
    BOCDetailsFragment bocDetailsFragment = new BOCDetailsFragment();
    BOCRatesFragment bocRatesFragment = new BOCRatesFragment();
    BOCLocationFragment bocLocationFragment = new BOCLocationFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boc);

        // Setting the title for the Activity's action bar
        getSupportActionBar().setTitle("Bank of Ceylon");

        // Enabling the back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNavigationView = findViewById(R.id.bottom_navigation_bank);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, bocDetailsFragment).commit();

        // Setting a listener to handle item selection in the bottom navigation bar
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.details:
                        // Replacing the current fragment with the BOCDetailsFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, bocDetailsFragment).commit();
                        return true;
                    case R.id.rates:
                        // Replacing the current fragment with the BOCRatesFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, bocRatesFragment).commit();
                        return true;
                    case R.id.locations:
                        // Replacing the current fragment with the BOCLocationFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, bocLocationFragment).commit();
                        return true;
                }

                return false;
            }
        });

    }
}