package com.example.weatherapp.java;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.weatherapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 🇰🇭 Cambodia Weather App - Main Activity
 * 
 * Handles navigation between fragments using bottom navigation
 */
public class MainActivityJava extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        
        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Handle navigation
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_hourly) {
                fragment = new HourlyFragment();
            } else if (itemId == R.id.nav_daily) {
                fragment = new FiveDayFragment();
            } else if (itemId == R.id.nav_map) {
                fragment = new MapFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
