package com.example.iikeaapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.Furniture_RecyclerViewAdapter;
import com.example.iikeaapp.adapter.Category_RecyclerViewAdapter;
import com.example.iikeaapp.data.CategoryModel;
import com.example.iikeaapp.data.FurnitureModel;

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

    // categories recycler view init
    ArrayList<CategoryModel> categoryModels = new ArrayList<>();

    // private LinearLayout mainLayout;
    // private Button changeLayoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_top_picks_recyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // init recycler views
        RecyclerView recyclerViewTopPicks = findViewById(R.id.main_top_picks_recyclerView);
        RecyclerView recyclerViewCategories = findViewById(R.id.main_categories_recyclerView);

        setUpFurnitureModels();
        setUpCategoryModels();

        // top picks
        Furniture_RecyclerViewAdapter fAdapter = new Furniture_RecyclerViewAdapter(this, furnitureModels);
        recyclerViewTopPicks.setAdapter(fAdapter);
        recyclerViewTopPicks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // categories
        Category_RecyclerViewAdapter adapter = new Category_RecyclerViewAdapter(this, categoryModels);
        recyclerViewCategories.setAdapter(adapter);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpFurnitureModels() {
        // String[] furnitureNames = getResources().getStringArray(R.array.furnitures);
        ArrayList<String> furnitureNames = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Integer> prices = new ArrayList<>();
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
                prices.add(productDetail.getInt("price"));
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

    private void setUpCategoryModels() {
        String[] categoryNames = getResources().getStringArray(R.array.categories);

        for (int i = 0; i<categoryNames.length; i++) {
            categoryModels.add(new CategoryModel(categoryNames[i],
                    R.drawable.baseline_arrow_forward_ios_24));
        }
    }

    public void bestSellingClicked(View v){
        setContentView(R.layout.activity_list);
    }
}