package com.example.iikeaapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.Furniture_HorizontalRecyclerViewAdapter;
import com.example.iikeaapp.data.DataProvider;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ViewHolder viewHolder;
    private Timer autoScrollTimer;
    private boolean isAutoScrolling = false;
    private ViewHolder vh;

    private static class ViewHolder {
        RecyclerView recyclerViewTopPicks;
        BottomNavigationView bottomNavigationView;
        FloatingActionButton searchIcon;
        TextView titleTextView;
        androidx.appcompat.widget.SearchView searchView;
        public ViewHolder(Activity activity) {
            recyclerViewTopPicks = activity.findViewById(R.id.main_top_picks_recyclerView);
            bottomNavigationView = activity.findViewById(R.id.bottomNavigation);
            searchIcon = activity.findViewById(R.id.search_icon);
            titleTextView = activity.findViewById(R.id.textView);
            searchView = activity.findViewById(R.id.list_search_view);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vh = new ViewHolder(this);

        ViewCompat.setOnApplyWindowInsetsListener(vh.recyclerViewTopPicks, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));

        viewHolder = new ViewHolder();
        startAutoScrolling();

        // init recycler views
        initRecyclerView();

        // Setup SearchView
        setupSearchView();

        // Set up the bottom navigation bar
        setupBottomNavigation();
    }

    private void initRecyclerView() {
        // top picks recycler view init
        ArrayList<FurnitureModel> furnitureModels = DataProvider.getInstance(this).getFurnitureModels();
        Collections.sort(furnitureModels, (a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()));
        Furniture_HorizontalRecyclerViewAdapter adapter = new Furniture_HorizontalRecyclerViewAdapter(this, furnitureModels);
        vh.recyclerViewTopPicks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        vh.recyclerViewTopPicks.setAdapter(adapter);

    }

    public void categoryClicked(View v) {
        String category = (String) v.getTag();

        // Load the animation
        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_categroy);

        // Set an animation listener to start the activity when the animation ends
        blinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });

        // Start the animation on the clicked view
        v.startAnimation(blinkAnimation);
        Log.d("debug", category);
    }

    private void setupSearchView() {
        // Setup SearchView
        vh.searchIcon.setOnClickListener(v -> {
            // Toggle the visibility of the SearchView and title
            if (vh.searchView.getVisibility() == View.VISIBLE) {
                vh.searchView.setVisibility(View.GONE);
                vh.titleTextView.setVisibility(View.VISIBLE);
            } else {
                vh.titleTextView.setVisibility(View.GONE);
                vh.searchView.setVisibility(View.VISIBLE);
                vh.searchView.setIconified(false);
                vh.searchView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.search_animation));
            }
        });

        vh.searchView.setOnCloseListener(() -> {
            // Collapse the search bar and show the title with animation
            vh.searchView.setVisibility(View.GONE);
            vh.titleTextView.setVisibility(View.VISIBLE);
            vh.searchView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.search_animation));
            return false;
        });

        vh.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupBottomNavigation(){
        // Set up the bottom navigation bar
        vh.bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        vh.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
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

    @Override
    protected void onResume() {
        super.onResume();

        initRecyclerView();

        // Hide the SearchView and show the title
        vh.searchView.setVisibility(View.GONE);
        vh.titleTextView.setVisibility(View.VISIBLE);

        // Start auto-scrolling
        if (!isAutoScrolling) {
            startAutoScrolling();
        }
    }

    private void startAutoScrolling() {
        if (!isAutoScrolling) {
            isAutoScrolling = true;
            autoScrollTimer = new Timer();
            autoScrollTimer.schedule(() -> {
                    runOnUiThread(() -> {
                        autoScrollTimer.scheduleAtFixedRate(new AutoScrollTask(), 0, 4000); // Scroll every 4 seconds
                    });
            }, 4000);
        }
    }

    private class AutoScrollTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(() -> {
                LinearLayoutManager layoutManager = (LinearLayoutManager) vh.recyclerViewTopPicks.getLayoutManager();
                if (layoutManager != null) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition == layoutManager.getItemCount() - 1) {
                        vh.recyclerViewTopPicks.smoothScrollToPosition(0);
                    } else {
                        vh.recyclerViewTopPicks.smoothScrollToPosition(lastVisibleItemPosition + 1);
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScrolling();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoScrolling();
    }

    private void stopAutoScrolling() {
        if (isAutoScrolling) {
            isAutoScrolling = false;
            if (autoScrollTimer != null) {
                autoScrollTimer.cancel();
                autoScrollTimer.purge();
            }
        }
    }

    private class ViewHolder {
        RecyclerView recyclerViewTopPicks;
        TextView titleTextView;
        androidx.appcompat.widget.SearchView searchView;
        FloatingActionButton searchIcon;
        BottomNavigationView bottomNavigationView;

        ViewHolder() {
            recyclerViewTopPicks = findViewById(R.id.main_top_picks_recyclerView);
            titleTextView = findViewById(R.id.textView);
            searchView = findViewById(R.id.list_search_view);
            searchIcon = findViewById(R.id.search_icon);
            bottomNavigationView = findViewById(R.id.bottomNavigation);
        }
    }
}