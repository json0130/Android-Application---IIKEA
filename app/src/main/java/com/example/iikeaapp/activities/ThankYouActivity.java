package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ThankYouActivity extends AppCompatActivity {

    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));

        viewHolder = new ViewHolder();

        // init
        setupSearchView();
        setupBottomNavigation();
    }

    private void setupSearchView() {
        viewHolder.searchIcon.setOnClickListener(v -> toggleSearchViewVisibility());

        viewHolder.titleTextView.setOnClickListener(v -> {
            Intent intent = new Intent(ThankYouActivity.this, MainActivity.class);
            startActivity(intent);
        });

        viewHolder.searchView.setOnCloseListener(() -> {
            viewHolder.searchView.setVisibility(View.GONE);
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
            viewHolder.searchView.startAnimation(AnimationUtils.loadAnimation(ThankYouActivity.this, R.anim.search_animation));
            return false;
        });

        viewHolder.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startListActivity(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void toggleSearchViewVisibility() {
        // watermark image visibility
        if (viewHolder.searchView.getVisibility() == View.VISIBLE) {
            viewHolder.searchView.setVisibility(View.GONE);
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.titleTextView.setVisibility(View.GONE);
            viewHolder.searchView.setVisibility(View.VISIBLE);
            viewHolder.searchView.setIconified(false);
            viewHolder.searchView.startAnimation(AnimationUtils.loadAnimation(ThankYouActivity.this, R.anim.search_animation));
        }
    }

    private void startListActivity(String query) {
        Intent intent = new Intent(ThankYouActivity.this, ListActivity.class);
        intent.putExtra("searchQuery", query);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        viewHolder.bottomNavigationView.setSelectedItemId(0);
        viewHolder.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
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
            } else if (item.getItemId() == R.id.bottom_setting) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private class ViewHolder {
        TextView titleTextView;
        SearchView searchView;
        FloatingActionButton searchIcon;
        BottomNavigationView bottomNavigationView;

        ViewHolder() {
            searchIcon = findViewById(R.id.search_icon);
            titleTextView = findViewById(R.id.title);
            searchView = findViewById(R.id.list_search_view);
            bottomNavigationView = findViewById(R.id.bottomNavigation);
        }
    }
}