package com.example.iikeaapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.ImageViewerAdapter;

public class DetailActivity extends AppCompatActivity {
    ViewPager viewPager;
    int[] imageFolder = {R.drawable.a1,R.drawable.a2};

    ImageViewerAdapter imageViewerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.furniture_detail_view_layout);
        viewPager = (ViewPager)findViewById(R.id.furniture_item_image);
        imageViewerAdapter = new ImageViewerAdapter(DetailActivity.this, imageFolder);
        viewPager.setAdapter(imageViewerAdapter);
    }
}
