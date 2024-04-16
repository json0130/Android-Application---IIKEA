package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.iikeaapp.R;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SettingActivity extends AppCompatActivity {
    private SwitchCompat themeSwitch;
    private TextView titleTextView;
    private androidx.appcompat.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));

        themeSwitch = findViewById(R.id.theme_switch);
        ThemeManager ThemeHelper;
        themeSwitch.setChecked(ThemeManager.getNightMode(this));

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeManager.setNightMode(this, isChecked);
        });

        // Setup SearchView
        setupSearchView();

        // Set up the bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_setting) {
                return true;
            } else if (item.getItemId() == R.id.bottom_cart) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.bottom_save) {
                Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }else if (item.getItemId() == R.id.bottom_home) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupSearchView() {


        // Setup SearchView
        FloatingActionButton searchIcon = findViewById(R.id.search_icon);
        titleTextView = findViewById(R.id.title);
        searchView = findViewById(R.id.list_search_view);

        searchIcon.setOnClickListener(v -> {
            // Toggle the visibility of the SearchView and title
            if (searchView.getVisibility() == View.VISIBLE) {
                searchView.setVisibility(View.GONE);
                titleTextView.setVisibility(View.VISIBLE);
            } else {
                titleTextView.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                searchView.setIconified(false);
                searchView.startAnimation(AnimationUtils.loadAnimation(SettingActivity.this, R.anim.search_animation));
            }
        });

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Expand the search bar and hide the title with animation
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Collapse the search bar and show the title with animation
                searchView.setVisibility(View.GONE);
                titleTextView.setVisibility(View.VISIBLE);
                searchView.startAnimation(AnimationUtils.loadAnimation(SettingActivity.this, R.anim.search_animation));
                return false;
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Start ListActivity with the search query
                Intent intent = new Intent(SettingActivity.this, ListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Hide the SearchView and show the title
        searchView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.VISIBLE);
    }
}