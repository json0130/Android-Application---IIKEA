package com.example.iikeaapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.SavedAdapter;
import com.example.iikeaapp.adapter.ViewPagerAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.SavedFurniture;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.SavedManager;
import com.example.iikeaapp.manager.CartManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailActivity extends AppCompatActivity {
    private ShoppingCart shoppingCart;

    private SavedFurniture savedFurniture;
    ViewPager mViewPager;
    TextView furnitureItemTitle, itemPrice, itemDescription;
    ImageView backButton, saveButton;

    SavedAdapter savedAdapter;

    private boolean isSaved = false;
    private static final int REQUEST_CODE_SAVE_ACTIVITY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        savedAdapter = new SavedAdapter(this, REQUEST_CODE_SAVE_ACTIVITY);

        // Get the ShoppingCart instance from the CartManager
        savedFurniture = SavedManager.getInstance().getSavedFurniture();
        shoppingCart = CartManager.getInstance().getShoppingCart();

        mViewPager = findViewById(R.id.viewPager);
        furnitureItemTitle = findViewById(R.id.furniture_item_title);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.save_button);

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

        // Add to shopping cart
        FloatingActionButton addToCartButton = findViewById(R.id.add_to_shopping_cart_btn);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FurnitureModel item = getFurnitureItem();
                if (item != null) {
                    shoppingCart.addItem(item, 1);
                    Toast.makeText(DetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FurnitureModel item = getFurnitureItem();
                if (item != null) {
                    if (isSaved) {
                        savedFurniture.removeItem(item);
                        isSaved = false;
                        saveButton.setImageResource(R.drawable.baseline_favorite_border_24);
                        Toast.makeText(DetailActivity.this, "Item removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        savedFurniture.addItem(item, 1);
                        isSaved = true;
                        saveButton.setImageResource(R.drawable.baseline_favorite_24);
                        Toast.makeText(DetailActivity.this, "Item added to favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_save) {
                Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, REQUEST_CODE_SAVE_ACTIVITY);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            } else if (item.getItemId() == R.id.bottom_home) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            } else if (item.getItemId() == R.id.bottom_cart) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            }
            return false;
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

        isSaved = savedFurniture.getItems().containsKey(furnitureModel);
        saveButton.setImageResource(isSaved ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SAVE_ACTIVITY && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("removedItem")) {
                FurnitureModel removedItem = (FurnitureModel) data.getSerializableExtra("removedItem");
                FurnitureModel currentItem = getFurnitureItem();
                if (currentItem != null && currentItem.equals(removedItem)) {
                    isSaved = false;
                    saveButton.setImageResource(R.drawable.baseline_favorite_border_24);
                }
            }
        }
    }
}

