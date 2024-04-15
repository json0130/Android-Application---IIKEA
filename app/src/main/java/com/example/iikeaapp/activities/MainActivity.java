package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.example.iikeaapp.adapter.Furniture_VerticalRecyclerViewAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.manager.Saved;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // top picks recycler view init
    ArrayList<FurnitureModel> furnitureModels = new ArrayList<>();
    private Saved saved;
    private ShoppingCart shoppingCart;
    private TextView titleTextView;
    private androidx.appcompat.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_top_picks_recyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the ShoppingCart instance from the CartManager
        saved = Saved.getInstance();
        shoppingCart = CartManager.getInstance().getShoppingCart();

        // init recycler views
        RecyclerView recyclerViewTopPicks = findViewById(R.id.main_top_picks_recyclerView);
        setUpFurnitureModels();

        // top picks
        Furniture_HorizontalRecyclerViewAdapter fAdapter = new Furniture_HorizontalRecyclerViewAdapter(this, furnitureModels);
        recyclerViewTopPicks.setAdapter(fAdapter);
        recyclerViewTopPicks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Setup SearchView
        setupSearchView();

        // Set up the bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                return true;
            } else if (item.getItemId() == R.id.bottom_cart) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            } else if (item.getItemId() == R.id.bottom_save) {
                Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            }
            return false;
        });
    }

    private void setUpFurnitureModels() {
        furnitureModels = loadFurnitureData();
    }

    private ArrayList<FurnitureModel> loadFurnitureData() {
        ArrayList<FurnitureModel> models = new ArrayList<>();
        try {
            JSONArray productArray = new JSONObject(loadJSONfromAssets()).getJSONArray("products");
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject productDetail = productArray.getJSONObject(i);
                models.add(new FurnitureModel(
                        productDetail.getString("name"),
                        productDetail.getString("category"),
                        productDetail.getDouble("price"),
                        productDetail.getString("description"),
                        new String[]{
                                productDetail.getJSONObject("imageResources").getString("image1"),
                                productDetail.getJSONObject("imageResources").getString("image2"),
                                productDetail.getJSONObject("imageResources").getString("image3")
                        }
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return models;
    }

    private String loadJSONfromAssets() {
        try {
            InputStream is = getAssets().open("catalogue.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void categoryClicked(View v) {
        String category = (String) v.getTag();

        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
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

        // Hide the SearchView and show the title
        searchView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.VISIBLE);
    }
}