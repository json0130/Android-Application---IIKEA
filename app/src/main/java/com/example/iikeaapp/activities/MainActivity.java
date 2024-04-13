package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.Furniture_RecyclerViewAdapter;
import com.example.iikeaapp.adapter.Category_RecyclerViewAdapter;
import com.example.iikeaapp.data.CategoryModel;
import com.example.iikeaapp.data.FurnitureModel;
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
        shoppingCart = CartManager.getInstance().getShoppingCart();

        // init recycler views
        RecyclerView recyclerViewTopPicks = findViewById(R.id.main_top_picks_recyclerView);
        setUpFurnitureModels();

        // top picks
        Furniture_RecyclerViewAdapter fAdapter = new Furniture_RecyclerViewAdapter(this, furnitureModels);
        recyclerViewTopPicks.setAdapter(fAdapter);
        recyclerViewTopPicks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_save) {
                startActivity(new Intent(getApplicationContext(), SaveActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_home) {
                return true;
            } else if (item.getItemId() == R.id.bottom_cart) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
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

    public void topPicksClicked(View v){
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        startActivity(intent);
    }
}