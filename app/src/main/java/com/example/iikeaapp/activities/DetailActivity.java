package com.example.iikeaapp.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.ViewPagerAdapter;
import com.example.iikeaapp.data.FurnitureModel;

public class DetailActivity extends AppCompatActivity {
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
    }
}

