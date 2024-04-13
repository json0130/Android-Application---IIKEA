package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.Furniture_HorizontalRecyclerViewAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.SavedFurniture;
import com.example.iikeaapp.manager.SavedManager;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    private SavedFurniture savedFurniture;

    private ShoppingCart shoppingCart;

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
        savedFurniture = SavedManager.getInstance().getSavedFurniture();
        shoppingCart = CartManager.getInstance().getShoppingCart();

        // init recycler views
        RecyclerView recyclerViewTopPicks = findViewById(R.id.main_top_picks_recyclerView);
        setUpFurnitureModels();

        // top picks
        Furniture_HorizontalRecyclerViewAdapter fAdapter = new Furniture_HorizontalRecyclerViewAdapter(this, furnitureModels);
        recyclerViewTopPicks.setAdapter(fAdapter);
        recyclerViewTopPicks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Setup SearchView
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.list_search_view);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Start ListActivity with the search query
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // nav bar
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
        // String[] furnitureNames = getResources().getStringArray(R.array.furnitures);
        ArrayList<String> furnitureNames = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> image1URLs = new ArrayList<>();
        ArrayList<String> image2URLs = new ArrayList<>();
        ArrayList<String> image3URLs = new ArrayList<>();


        try {
            JSONObject obj = new JSONObject(loadJSONfromAssets());

            JSONArray productArray = obj.getJSONArray("products");

            // load products from JSON to arraylists
            for (int i = 0; i<productArray.length(); i++) {
                JSONObject productDetail = productArray.getJSONObject(i);

                furnitureNames.add(productDetail.getString("name"));
                categories.add(productDetail.getString("category"));
                prices.add(productDetail.getDouble("price"));
                descriptions.add(productDetail.getString("description"));

                // nested values
                JSONObject imageResources = productDetail.getJSONObject("imageResources");
                image1URLs.add(imageResources.getString("image1"));
                image2URLs.add(imageResources.getString("image2"));
                image3URLs.add(imageResources.getString("image3"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i<furnitureNames.size(); i++) {
            String[] tempImages = {image1URLs.get(i), image2URLs.get(i), image3URLs.get(i)};
            FurnitureModel temp = new FurnitureModel(furnitureNames.get(i), categories.get(i), prices.get(i), descriptions.get(i), tempImages);
            furnitureModels.add(temp);
        }
    }

    private String loadJSONfromAssets() {
        String json;

        try {
            InputStream is = getAssets().open("catalogue.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return json;
    }

    public void categoryClicked(View v){
        String category = (String) v.getTag();

        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}