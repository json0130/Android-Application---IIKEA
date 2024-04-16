package com.example.iikeaapp.activities;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.ViewPagerAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.manager.CartManager;
import com.example.iikeaapp.manager.Saved;
import com.example.iikeaapp.data.ShoppingCart;

import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
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

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));
        // Get the ShoppingCart instance from the CartManager
        shoppingCart = CartManager.getInstance().getShoppingCart();

        mViewPager = findViewById(R.id.viewPager);
        furnitureItemTitle = findViewById(R.id.furniture_item_title);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        backButton = findViewById(R.id.back_icon);
        saveChip = findViewById(R.id.save_chip);

        // Retrieve the FurnitureModel object from the Intent
        FurnitureModel furnitureModel = (FurnitureModel) getIntent().getSerializableExtra("FurnitureModel");

        // If the FurnitureModel object is available, use it directly
        if (furnitureModel != null) {
            updateUIWithFurnitureModel(furnitureModel);
        } else {
            // Otherwise, create a new FurnitureModel object from the individual data fields
            furnitureModel = getFurnitureItem();
            if (furnitureModel != null) {
                updateUIWithFurnitureModel(furnitureModel);
            }
        }

        backButton.setOnClickListener(view -> {
            finish();
        });

        setupSaveChip(furnitureModel);

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
        addToCartButton.setOnClickListener(v -> {
            FurnitureModel item = getFurnitureItem();
            if (item != null) {
                shoppingCart.addItem(item, this.quantity);
                Toast.makeText(DetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
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

        // nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_save) {
                Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, REQUEST_CODE_SAVE_ACTIVITY);
                return true;
            } else if (item.getItemId() == R.id.bottom_home) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.bottom_cart) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupSaveChip(FurnitureModel furnitureModel) {
        saveChip.setChecked(Saved.isSaved(furnitureModel));
        ArrayList<FurnitureModel> savedItems = Saved.getInstance().getSavedItems();
        saveChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Saved.addItem(furnitureModel);
                Log.d("debug", savedItems.toString());
                Toast.makeText(DetailActivity.this, "Item saved to favorites", Toast.LENGTH_SHORT).show();
            } else {
                Saved.removeItem(furnitureModel);
                Log.d("debug", savedItems.toString());
                Toast.makeText(DetailActivity.this, "Item removed from favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private FurnitureModel getFurnitureItem() {
        // Retrieve the FurnitureModel object from the Intent
        FurnitureModel furnitureModel = (FurnitureModel) getIntent().getSerializableExtra("FurnitureModel");
        return furnitureModel;
    }

    private void updateUIWithFurnitureModel(FurnitureModel furnitureModel) {
        furnitureItemTitle.setText(furnitureModel.getFurnitureName());
        itemPrice.setText("$" + furnitureModel.getPrice());
        itemDescription.setText(furnitureModel.getDescription());

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(DetailActivity.this, furnitureModel.getImageResources());
        mViewPager.setAdapter(mViewPagerAdapter);
    }
}
