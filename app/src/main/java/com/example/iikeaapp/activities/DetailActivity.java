package com.example.iikeaapp.activities;

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
import com.example.iikeaapp.adapter.ViewPagerAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.ShoppingCart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailActivity extends AppCompatActivity {
    private ShoppingCart shoppingCart;
    ViewPager mViewPager;
    TextView furnitureItemTitle, itemPrice, itemDescription;
    ImageView backButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mViewPager = findViewById(R.id.viewPager);
        furnitureItemTitle = findViewById(R.id.furniture_item_title);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.save_button);

        FurnitureModel furnitureModel = (FurnitureModel) getIntent().getSerializableExtra("FurnitureModel");
        // Update UI elements with data from FurnitureModel
        if (furnitureModel != null) {
            furnitureItemTitle.setText(furnitureModel.getFurnitureName());
            itemPrice.setText("$" + furnitureModel.getPrice());
            itemDescription.setText(furnitureModel.getDescription());

            ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(DetailActivity.this, furnitureModel.getImageResources());
            mViewPager.setAdapter(mViewPagerAdapter);
        }

        backButton.setOnClickListener(view -> {
            finish();
        });

        // Add to shopping cart
        FloatingActionButton addToCartButton = findViewById(R.id.add_to_shopping_cart_btn);

        // Create an instance of the ShoppingCart
        shoppingCart = new ShoppingCart();
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FurnitureModel item = getFurnitureItem();
                shoppingCart.addItem(item, 1);
                Toast.makeText(DetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the click listener for the "View Cart" button
        Button viewCartButton = findViewById(R.id.view_cart_button);
        viewCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CartActivity.class);
                intent.putExtra("shoppingCart", shoppingCart);
                startActivity(intent);
            }
        });
    }

    private FurnitureModel getFurnitureItem() {
        // Retrieve the data passed from the previous activity
        Intent intent = getIntent();
        if (intent != null) {
            String furnitureName = intent.getStringExtra("furnitureName");
            String category = intent.getStringExtra("category");
            int price = intent.getIntExtra("price", 0);
            String description = intent.getStringExtra("description");
            String[] imageResources = intent.getStringArrayExtra("imageResources");

            // Create a new FurnitureModel object with the retrieved data
            FurnitureModel item = new FurnitureModel(furnitureName, category, price, description, imageResources);

            return item;
        }

        return null;
    }
}

