package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.Furniture_HorizontalRecyclerViewAdapter;
import com.example.iikeaapp.adapter.ViewPagerAdapter;
import com.example.iikeaapp.data.DataProvider;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.example.iikeaapp.manager.Saved;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    ArrayList<FurnitureModel> furnitureModels = new ArrayList<>();
    private ShoppingCart shoppingCart;
    ViewPager mViewPager;
    TextView furnitureItemTitle, itemPrice, itemDescription;
    ImageView backButton;
    Chip saveChip;
    private TextView titleTextView;
    private androidx.appcompat.widget.SearchView searchView;
    private int quantity = 1;
    private static final int REQUEST_CODE_SAVE_ACTIVITY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        furnitureModels = DataProvider.getInstance(this).getFurnitureModels();
        Log.d("debug", furnitureModels.toString());

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));
        // Get the ShoppingCart instance from the CartManager
        shoppingCart = CartManager.getInstance().getShoppingCart();

        mViewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);
        furnitureItemTitle = findViewById(R.id.furniture_item_title);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        ImageView saveHeart = findViewById(R.id.save_heart);

        // Retrieve the FurnitureModel object from the Intent
        String furnitureName = getIntent().getStringExtra("furnitureName");
        FurnitureModel furnitureModel = DataProvider.getInstance(this).getFurnitureByName(furnitureName);

        // If the FurnitureModel object is available, use it directly
        if (furnitureModel != null) {
            furnitureModel.incrementViewCount();
            updateUIWithFurnitureModel(furnitureModel);
        } else {
            // Otherwise, create a new FurnitureModel object from the individual data fields
            furnitureModel = getFurnitureItem();
            if (furnitureModel != null) {
                updateUIWithFurnitureModel(furnitureModel);
            }
        }

        updateHeartIcon(saveHeart, furnitureModel);
        FurnitureModel finalFurnitureModel = furnitureModel;
        saveHeart.setOnClickListener(v -> {
            if (Saved.isSaved(finalFurnitureModel)) {
                Saved.removeItem(finalFurnitureModel);
                saveHeart.setImageResource(R.drawable.ic_heart_outline);
            } else {
                Saved.addItem(finalFurnitureModel);
                saveHeart.setImageResource(R.drawable.ic_heart_filled);
            }
            Toast.makeText(this, Saved.isSaved(finalFurnitureModel) ? "Added to Favorites" : "Removed from Favorites", Toast.LENGTH_SHORT).show();
        });

        // Find the views for quantity selection
        TextView quantityTextView = findViewById(R.id.quantity_text_view);
        TextView decrementButton = findViewById(R.id.decrement_button);
        TextView incrementButton = findViewById(R.id.increment_button);

        // Set the initial quantity text
        quantityTextView.setText(String.valueOf(this.quantity));

        // Set click listeners for increment and decrement buttons
        decrementButton.setOnClickListener(v -> {
            if (this.quantity > 1) {
                this.quantity--;
                quantityTextView.setText(String.valueOf(this.quantity));
            }
        });

        incrementButton.setOnClickListener(v -> {
            this.quantity++;
            quantityTextView.setText(String.valueOf(this.quantity));
        });

        // Add to shopping cart
        FloatingActionButton addToCartButton = findViewById(R.id.add_to_shopping_cart_btn);
        FurnitureModel finalFurnitureModel1 = furnitureModel;
        addToCartButton.setOnClickListener(v -> {
            if (finalFurnitureModel1 != null) {
                shoppingCart.addItem(finalFurnitureModel1, this.quantity);
                Toast.makeText(DetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
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
                searchView.startAnimation(AnimationUtils.loadAnimation(DetailActivity.this, R.anim.search_animation));
            }
        });

        searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Collapse the search bar and show the title with animation
                searchView.setVisibility(View.GONE);
                titleTextView.setVisibility(View.VISIBLE);
                searchView.startAnimation(AnimationUtils.loadAnimation(DetailActivity.this, R.anim.search_animation));
                return false;
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Start ListActivity with the search query
                Intent intent = new Intent(DetailActivity.this, ListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Expand the search bar and hide the title with animation
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(0);
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

    private void updateHeartIcon(ImageView saveHeart, FurnitureModel furnitureModel) {
        if (Saved.isSaved(furnitureModel)) {
            saveHeart.setImageResource(R.drawable.ic_heart_filled);
        } else {
            saveHeart.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    private FurnitureModel getFurnitureItem() {
        // Retrieve the FurnitureModel object from the Intent
        String furnitureName = getIntent().getStringExtra("furnitureName");
        return DataProvider.getInstance(this).getFurnitureByName(furnitureName);
    }

    private void updateUIWithFurnitureModel(FurnitureModel furnitureModel) {
        furnitureItemTitle.setText(furnitureModel.getFurnitureName());
        itemPrice.setText("$" + furnitureModel.getPrice());
        itemDescription.setText(furnitureModel.getDescription());

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(DetailActivity.this, furnitureModel.getImageResources());
        mViewPager.setAdapter(mViewPagerAdapter);

        // Setup related items RecyclerView
        setUpRelatedItemsRecyclerView(furnitureModel);
    }

    private void setUpRelatedItemsRecyclerView(FurnitureModel currentFurniture) {
        RecyclerView recyclerViewRelatedItems = findViewById(R.id.related_items_recyclerView);

        // Filter furniture models to only include items of the same category as the current item
        ArrayList<FurnitureModel> relatedModels = new ArrayList<>();
        for (FurnitureModel model : furnitureModels) {
            if (model.getCategory().equalsIgnoreCase(currentFurniture.getCategory()) && !model.getFurnitureName().equals(currentFurniture.getFurnitureName())) {
                relatedModels.add(model);
            }
        }

        // Set up the RecyclerView with the filtered list
        Furniture_HorizontalRecyclerViewAdapter fAdapter = new Furniture_HorizontalRecyclerViewAdapter(this, relatedModels);
        recyclerViewRelatedItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRelatedItems.setAdapter(fAdapter);
    }
}
