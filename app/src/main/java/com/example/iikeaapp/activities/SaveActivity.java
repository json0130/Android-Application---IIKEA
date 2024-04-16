package com.example.iikeaapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.FurnitureAdapter;
import com.example.iikeaapp.adapter.SavedAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SaveActivity extends AppCompatActivity implements SavedAdapter.OnItemClickListener {
    private RecyclerView recyclerViewSaved;
    private SavedAdapter savedAdapter;
    private TextView titleTextView;
    private androidx.appcompat.widget.SearchView searchView;
    private ImageView backIcon;

    private static final int REQUEST_CODE_DETAIL_ACTIVITY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);


        // Initialize the RecyclerView
        recyclerViewSaved = findViewById(R.id.saved_recycler_view);
        recyclerViewSaved.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the SavedAdapter
        savedAdapter = new SavedAdapter(this, this);
        recyclerViewSaved.setAdapter(savedAdapter);

        // empty list msg
        TextView noProductMsg = findViewById(R.id.emptyListText);
        if (savedAdapter.getItemCount() == 0) {
            noProductMsg.setVisibility(View.VISIBLE);
        } else {
            noProductMsg.setVisibility(View.INVISIBLE);
        }

        backIcon = findViewById(R.id.back_icon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Expand the search bar and hide the title with animation
                Intent intent = new Intent(SaveActivity.this, MainActivity.class);
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
                searchView.startAnimation(AnimationUtils.loadAnimation(SaveActivity.this, R.anim.search_animation));
            }
        });

        searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Collapse the search bar and show the title with animation
                searchView.setVisibility(View.GONE);
                titleTextView.setVisibility(View.VISIBLE);
                searchView.startAnimation(AnimationUtils.loadAnimation(SaveActivity.this, R.anim.search_animation));
                return false;
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Start ListActivity with the search query
                Intent intent = new Intent(SaveActivity.this, ListActivity.class);
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
        bottomNavigationView.setSelectedItemId(R.id.bottom_save);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_save) {
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

    // Update the savedAdapter when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        updateAdapterData();
    }

    @Override
    public void onItemClick(FurnitureModel furniture) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("FurnitureModel", furniture);
        startActivity(intent);
    }

    private void updateAdapterData() {
        savedAdapter.updateData();
        savedAdapter.notifyDataSetChanged();

        // Hide the SearchView and show the title
        searchView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.VISIBLE);
    }
}