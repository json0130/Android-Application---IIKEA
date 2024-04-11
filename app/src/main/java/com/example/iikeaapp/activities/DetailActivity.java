package com.example.iikeaapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.ViewPagerAdapter;

public class DetailActivity extends AppCompatActivity {
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mViewPager = findViewById(R.id.viewPager);

        String[] imageURLSs = getIntent().getStringArrayExtra("imageUrls");

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(DetailActivity.this, imageURLSs);
        mViewPager.setAdapter(mViewPagerAdapter);
    }
}

