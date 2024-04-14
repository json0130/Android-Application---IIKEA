package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.ViewPagerAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.manager.Saved;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    //private CartManager shoppingCart;

    ViewPager mViewPager;
    TextView furnitureItemTitle, itemPrice, itemDescription;
    ImageView backButton;
    Chip saveChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //shoppingCart = CartManager.getInstance();

        mViewPager = findViewById(R.id.viewPager);
        furnitureItemTitle = findViewById(R.id.furniture_item_title);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        backButton = findViewById(R.id.backButton);
        saveChip = findViewById(R.id.save_chip);

        FurnitureModel furnitureModel = (FurnitureModel) getIntent().getSerializableExtra("FurnitureModel");

        if (furnitureModel != null) {
            updateUIWithFurnitureModel(furnitureModel);
        }

        backButton.setOnClickListener(view -> finish());

//        FloatingActionButton addToCartButton = findViewById(R.id.add_to_shopping_cart_btn);
//        addToCartButton.setOnClickListener(v -> {
//            if (furnitureModel != null) {
//                shoppingCart.addItem(furnitureModel, 1);
//                Toast.makeText(DetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
//            }
//        });

        setupSaveChip(furnitureModel);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        setupNavigationBar(bottomNavigationView);
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

    private void updateUIWithFurnitureModel(FurnitureModel furnitureModel) {
        furnitureItemTitle.setText(furnitureModel.getFurnitureName());
        itemPrice.setText("$" + furnitureModel.getPrice());
        itemDescription.setText(furnitureModel.getDescription());

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(this, furnitureModel.getImageResources());
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private void setupNavigationBar(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = null;
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_save) {
                intent = new Intent(getApplicationContext(), SaveActivity.class);
            } else if (itemId == R.id.bottom_home) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            } else if (itemId == R.id.bottom_cart) {
                intent = new Intent(getApplicationContext(), CartActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            }
            return false;
        });
    }

}
