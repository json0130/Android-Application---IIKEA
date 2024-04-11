package com.example.iikeaapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.ViewPagerAdapter;

public class DetailActivity extends AppCompatActivity {
    ViewPager mViewPager;
    int[] images = {R.drawable.a1, R.drawable.a2, R.drawable.a3};

    ViewPagerAdapter mViewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mViewPagerAdapter = new ViewPagerAdapter(DetailActivity.this, images);
        mViewPager.setAdapter(mViewPagerAdapter);
    }
}

