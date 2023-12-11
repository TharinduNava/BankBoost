package com.example.bankboost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    AboutFragment aboutFragment = new AboutFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Hiding the Activity's action bar
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigationView  = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        // Setting a listener to handle item selection in the bottom navigation bar
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        // Replacing the current fragment with the HomeFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                        return true;
                    case R.id.profile:
                        // Replacing the current fragment with the ProfileFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,profileFragment).commit();
                        return true;
                    case R.id.about:
                        // Replacing the current fragment with the AboutFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,aboutFragment).commit();
                        return true;
                }

                return false;
            }
        });

    }
}