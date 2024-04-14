package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.FurnitureAdapter;
import com.example.iikeaapp.adapter.Furniture_VerticalRecyclerViewAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.manager.CartManager;
import com.example.iikeaapp.manager.Saved;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ListActivity extends AppCompatActivity implements FurnitureAdapter.OnItemClickListener {
    private ArrayList<FurnitureModel> furnitureModels = new ArrayList<>();
    private Saved saved;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        saved = Saved.getInstance();
        cartManager = CartManager.getInstance();

        setUpFurnitureModels();
        initRecyclerView();
        setupSearchView();
        setupNavigation();
        setupButtonListeners();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateAdapter(furnitureModels);
    }

    private void setupSearchView() {
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.list_search_view);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFurnitureBySearchQuery(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFurnitureBySearchQuery(newText);
                return true;
            }
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_save) {
                startActivity(new Intent(getApplicationContext(), SaveActivity.class));
                return true;
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            } else if (itemId == R.id.bottom_cart) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                return true;
            }
            return false;
        });
    }


    private void setupButtonListeners() {
        findViewById(R.id.listview_filter_button).setOnClickListener(this::showFilterDialog);
        findViewById(R.id.listview_sort_button).setOnClickListener(this::showSortDialog);
    }

    private void showFilterDialog(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View filterView = LayoutInflater.from(this).inflate(R.layout.filter_listview_layout, null);
        bottomSheetDialog.setContentView(filterView);
        setupChips(filterView);
        setupPriceSlider(filterView);
        bottomSheetDialog.show();
    }

    private void showSortDialog(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sortView = LayoutInflater.from(this).inflate(R.layout.sort_listview_layout, null);
        bottomSheetDialog.setContentView(sortView);
        setupSortGroup(sortView);
        bottomSheetDialog.show();
    }

    private void setupPriceSlider(View view) {
        RangeSlider priceSlider = view.findViewById(R.id.max_price_slider);
        double maxPrice = getMaxPrice();
        priceSlider.setValueFrom(0f);
        priceSlider.setValueTo((float) maxPrice);
        priceSlider.setValues((float) maxPrice);
        priceSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) filterFurnitureByMaxPrice(value);
        });
    }

    private void setupChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.filter_tag_group);
        // Initialize active filters with all categories if all chips are initially checked.
        Set<String> activeFilters = new HashSet<>();
        int chipCount = chipGroup.getChildCount();
        for (int i = 0; i < chipCount; i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                activeFilters.add(chip.getText().toString().toUpperCase());
            }
            // Listen to changes on each chip.
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String category = buttonView.getText().toString().toUpperCase();
                if (isChecked) {
                    activeFilters.add(category);
                } else {
                    activeFilters.remove(category);
                }
                filterFurnitureByCategories(activeFilters);
            });
        }
    }

    private void filterFurnitureByCategories(Set<String> categories) {
        ArrayList<FurnitureModel> filteredModels = new ArrayList<>();
        for (FurnitureModel model : furnitureModels) {
            if (categories.isEmpty() || categories.contains(model.getCategory().toUpperCase())) {
                filteredModels.add(model);
            }
        }
        updateAdapter(filteredModels);
    }



    private void setupSortGroup(View view) {
        RadioGroup sortGroup = view.findViewById(R.id.sort_group);
        sortGroup.setOnCheckedChangeListener((group, checkedId) -> sortFurnitureByPrice(checkedId == R.id.sort_high_low));
    }

    private double getMaxPrice() {
        double maxPrice = 0;
        for (FurnitureModel model : furnitureModels) {
            maxPrice = Math.max(maxPrice, model.getPrice());
        }
        return maxPrice;
    }

    private void filterFurnitureByMaxPrice(float maxPrice) {
        ArrayList<FurnitureModel> filteredModels = new ArrayList<>();
        for (FurnitureModel model : furnitureModels) {
            if (model.getPrice() <= maxPrice) filteredModels.add(model);
        }
        updateAdapter(filteredModels);
    }

    private void filterFurnitureByCategory(String category) {
        ArrayList<FurnitureModel> filteredModels = new ArrayList<>();
        for (FurnitureModel model : furnitureModels) {
            if (model.getCategory().equalsIgnoreCase(category)) {
                filteredModels.add(model);
            }
        }
        updateAdapter(filteredModels);
    }

    private void sortFurnitureByPrice(boolean highestToLowest) {
        Collections.sort(furnitureModels, (o1, o2) -> Double.compare(highestToLowest ? o2.getPrice() : o1.getPrice(), highestToLowest ? o1.getPrice() : o2.getPrice()));
        updateAdapter(furnitureModels);
    }

    private void updateAdapter(ArrayList<FurnitureModel> models) {
        Furniture_VerticalRecyclerViewAdapter adapter = new Furniture_VerticalRecyclerViewAdapter(this, models);
        ((RecyclerView) findViewById(R.id.furniture_recycler_view)).setAdapter(adapter);
    }

    private ArrayList<FurnitureModel> filterModels(String category, String searchQuery) {
        ArrayList<FurnitureModel> filteredModels = new ArrayList<>();
        for (FurnitureModel model : furnitureModels) {
            if ((category == null || model.getCategory().equalsIgnoreCase(category)) &&
                    (searchQuery == null || model.getFurnitureName().toLowerCase().contains(searchQuery.toLowerCase()))) {
                filteredModels.add(model);
            }
        }
        return filteredModels;
    }

    private void filterFurnitureBySearchQuery(String query) {
        updateAdapter(filterModels(null, query));
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

    @Override
    public void onItemClick(FurnitureModel furniture) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("FurnitureModel", furniture);
        startActivity(intent);
    }
}
