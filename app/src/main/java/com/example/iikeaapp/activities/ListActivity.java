package com.example.iikeaapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.FurnitureAdapter;
import com.example.iikeaapp.adapter.Furniture_VerticalRecyclerViewAdapter;
import com.example.iikeaapp.data.DataProvider;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.Saved;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class ListActivity extends AppCompatActivity implements FurnitureAdapter.OnItemClickListener {
    private String currentSearchQuery = "";
    private Set<String> currentCategories = new HashSet<>();
    private double currentMaxPrice = Double.MAX_VALUE;
    private boolean sortHighestToLowest = false;
    ArrayList<FurnitureModel> furnitureModels = new ArrayList<>();
    private Set<String> visibleCategories = new HashSet<>();
    private Saved saved;
    private ShoppingCart shoppingCart;
    private TextView titleTextView;
    private androidx.appcompat.widget.SearchView searchView;
    private ImageView searchIcon;

    private ImageView backIcon;

    private static class ViewHolder {
        public final RecyclerView items;
        public final Button filterButton;
        public final Button sortButton;
        public final SearchView searchView;

        public ViewHolder(Activity activity) {
            items = activity.findViewById(R.id.furniture_recycler_view);
            filterButton = activity.findViewById(R.id.listview_filter_button);
            sortButton = activity.findViewById(R.id.listview_sort_button);
            searchView = activity.findViewById(R.id.list_search_view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));
        furnitureModels = DataProvider.getInstance(this).getFurnitureModels();

        // Initialize necessary components and listeners
        initRecyclerView();
        setupSearchView();
        setupNavigation();
        setupButtonListeners();

        // Load furniture data and apply initial filters
        updateAdapter();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateAdapter();
    }

    private void setupSearchView() {
        // Setup SearchView
        backIcon = findViewById(R.id.back_icon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Expand the search bar and hide the title with animation
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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
                searchView.startAnimation(AnimationUtils.loadAnimation(ListActivity.this, R.anim.search_animation));
            }
        });

        searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Collapse the search bar and show the title with animation
                searchView.setVisibility(View.GONE);
                titleTextView.setVisibility(View.VISIBLE);
                searchView.startAnimation(AnimationUtils.loadAnimation(ListActivity.this, R.anim.search_animation));
                return false;
            }
        });

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

        String category = getIntent().getStringExtra("category");
        String searchQuery = getIntent().getStringExtra("searchQuery");
        if (category != null) {
            currentCategories.add(category);
            updateAdapter();
        }
        if (searchQuery != null) {
            currentSearchQuery = searchQuery;
            updateAdapter();
        }
    }

    private void setupNavigation() {
        // nav bar
        // Set up the bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
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
            }else if (item.getItemId() == R.id.bottom_setting) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
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
            if (fromUser) {
                currentMaxPrice = value;
                updateAdapter();
            }
        });
    }

    private void setupChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.filter_tag_group);
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setChecked(currentCategories.contains(chip.getText().toString().toUpperCase()));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String category = buttonView.getText().toString().toUpperCase();
                if (isChecked) {
                    currentCategories.add(category);
                } else {
                    currentCategories.remove(category);
                }
                updateAdapter();
            });
        }
    }

    private void setupSortGroup(View view) {
        RadioGroup sortGroup = view.findViewById(R.id.sort_group);
        sortGroup.check(sortHighestToLowest ? R.id.sort_high_low : R.id.sort_low_high);
        sortGroup.setOnCheckedChangeListener((group, checkedId) -> {
            sortHighestToLowest = checkedId == R.id.sort_high_low;
            updateAdapter();
        });
    }

    private double getMaxPrice() {
        double maxPrice = 0;
        for (FurnitureModel model : furnitureModels) {
            maxPrice = Math.max(maxPrice, model.getPrice());
        }
        return maxPrice;
    }

    private void updateAdapter() {
        ArrayList<FurnitureModel> filteredModels = new ArrayList<>();

        // Filter by category if specified
        for (FurnitureModel model : furnitureModels) {
            if (currentCategories.isEmpty() || currentCategories.contains(model.getCategory().toUpperCase())) {
                filteredModels.add(model);
            }
        }

        // Further filter by search query if non-empty
        if (!currentSearchQuery.isEmpty()) {
            filteredModels = new ArrayList<>(filteredModels); // Make a copy to avoid concurrent modification
            for (Iterator<FurnitureModel> it = filteredModels.iterator(); it.hasNext(); ) {
                FurnitureModel model = it.next();
                if (!model.getFurnitureName().toLowerCase().contains(currentSearchQuery.toLowerCase())) {
                    it.remove();
                }
            }
        }

        // Further filter by max price
        double maxPrice = currentMaxPrice;
        filteredModels = new ArrayList<>(filteredModels); // Make a copy to avoid concurrent modification
        for (Iterator<FurnitureModel> it = filteredModels.iterator(); it.hasNext(); ) {
            FurnitureModel model = it.next();
            if (model.getPrice() > maxPrice) {
                it.remove();
            }
        }

        // Sort the final list
        if (sortHighestToLowest) {
            Collections.sort(filteredModels, (a, b) -> Double.compare(b.getPrice(), a.getPrice()));
        } else {
            Collections.sort(filteredModels, (a, b) -> Double.compare(a.getPrice(), b.getPrice()));
        }

        // empty list msg
        TextView noProductMsg = findViewById(R.id.emptyListText);
        if (filteredModels.isEmpty()) {
            noProductMsg.setVisibility(View.VISIBLE);
        } else {
            noProductMsg.setVisibility(View.INVISIBLE);
        }

        // Set the adapter with the filtered and sorted list
        Furniture_VerticalRecyclerViewAdapter adapter = new Furniture_VerticalRecyclerViewAdapter(this, filteredModels);
        RecyclerView recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setAdapter(adapter);
    }

    private void filterFurnitureBySearchQuery(String query) {
        currentSearchQuery = query;
        updateAdapter();
    }

    @Override
    public void onItemClick(FurnitureModel furniture) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("FurnitureModel", furniture);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Hide the SearchView and show the title
        searchView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.VISIBLE);
    }
}
