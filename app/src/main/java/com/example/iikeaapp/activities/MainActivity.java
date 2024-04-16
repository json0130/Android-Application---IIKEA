package com.example.iikeaapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.Furniture_HorizontalRecyclerViewAdapter;
import com.example.iikeaapp.adapter.Furniture_VerticalRecyclerViewAdapter;
import com.example.iikeaapp.data.DataProvider;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.manager.Saved;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    // top picks recycler view init
    private RecyclerView recyclerViewTopPicks;
    private Saved saved;
    private ShoppingCart shoppingCart;
    private TextView titleTextView;
    private androidx.appcompat.widget.SearchView searchView;
    SwitchCompat switchMode;
    boolean nightmode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private SwitchCompat themeSwitch;
    private Timer autoScrollTimer;
    private boolean isAutoScrolling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_top_picks_recyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));

        startAutoScrolling();

        // Get the ShoppingCart instance from the CartManager
        saved = Saved.getInstance();
        shoppingCart = CartManager.getInstance().getShoppingCart();

        // init recycler views
        initRecyclerView();

        // Setup SearchView
        setupSearchView();

        // Set up the bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
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
            }else if (item.getItemId() == R.id.bottom_setting) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void initRecyclerView() {
        recyclerViewTopPicks = findViewById(R.id.main_top_picks_recyclerView);
        ArrayList<FurnitureModel> furnitureModels = DataProvider.getInstance(this).getFurnitureModels();
        Collections.sort(furnitureModels, (a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()));
        Furniture_HorizontalRecyclerViewAdapter adapter = new Furniture_HorizontalRecyclerViewAdapter(this, furnitureModels);
        recyclerViewTopPicks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopPicks.setAdapter(adapter);
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
        FloatingActionButton searchIcon = findViewById(R.id.search_icon);
        titleTextView = findViewById(R.id.textView);
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
                searchView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.search_animation));
            }
        });

        searchView.setOnCloseListener(() -> {
            // Collapse the search bar and show the title with animation
            searchView.setVisibility(View.GONE);
            titleTextView.setVisibility(View.VISIBLE);
            searchView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.search_animation));
            return false;
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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

    @Override
    protected void onResume() {
        super.onResume();

        initRecyclerView();

        // Hide the SearchView and show the title
        searchView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.VISIBLE);

        // Start auto-scrolling
        startAutoScrolling();
    }

    private void startAutoScrolling() {
        if (!isAutoScrolling) {
            isAutoScrolling = true;
            autoScrollTimer = new Timer();
            autoScrollTimer.scheduleAtFixedRate(new AutoScrollTask(), 0, 4000); // Scroll every 3 seconds
        }
    }

    private class AutoScrollTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(() -> {
                RecyclerView recyclerViewTopPicks = findViewById(R.id.main_top_picks_recyclerView);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewTopPicks.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition == layoutManager.getItemCount() - 1) {
                    recyclerViewTopPicks.smoothScrollToPosition(0);
                } else {
                    recyclerViewTopPicks.smoothScrollToPosition(lastVisibleItemPosition + 1);
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

}