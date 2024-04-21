package com.example.iikeaapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.SavedAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.manager.Saved;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class SaveActivity extends AppCompatActivity implements SavedAdapter.OnItemClickListener {
    private ViewHolder viewHolder;
    private SavedAdapter savedAdapter;

    private static final int REQUEST_CODE_DETAIL_ACTIVITY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));

        viewHolder = new ViewHolder();

        // Initialize the RecyclerView
        viewHolder.recyclerViewSaved.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the SavedAdapter
        savedAdapter = new SavedAdapter(this, this);
        viewHolder.recyclerViewSaved.setAdapter(savedAdapter);

        // initialising
        updateEmptyView();
        setupSearchView();
        setupBottomNavigation();
        setupItemTouchHelper();
    }

    private void setupSearchView() {
        viewHolder.searchIcon.setOnClickListener(v -> toggleSearchViewVisibility());

        viewHolder.titleTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SaveActivity.this, MainActivity.class);
            startActivity(intent);
        });

        viewHolder.searchView.setOnCloseListener(() -> {
            viewHolder.searchView.setVisibility(View.GONE);
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
            viewHolder.searchView.startAnimation(AnimationUtils.loadAnimation(SaveActivity.this, R.anim.search_animation));
            return false;
        });

        viewHolder.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startListActivity(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void toggleSearchViewVisibility() {
        // watermark image visibility
        if (viewHolder.searchView.getVisibility() == View.VISIBLE) {
            viewHolder.searchView.setVisibility(View.GONE);
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.titleTextView.setVisibility(View.GONE);
            viewHolder.searchView.setVisibility(View.VISIBLE);
            viewHolder.searchView.setIconified(false);
            viewHolder.searchView.startAnimation(AnimationUtils.loadAnimation(SaveActivity.this, R.anim.search_animation));
        }
    }

    private void startListActivity(String query) {
        Intent intent = new Intent(SaveActivity.this, ListActivity.class);
        intent.putExtra("searchQuery", query);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        viewHolder.bottomNavigationView.setSelectedItemId(R.id.bottom_save);
        viewHolder.bottomNavigationView.setOnItemSelectedListener(item -> {
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
                return true;
            } else if (item.getItemId() == R.id.bottom_setting) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                FurnitureModel deletedItem = savedAdapter.savedItems.get(position);
                Saved.removeItem(deletedItem);
                savedAdapter.savedItems.remove(position);
                savedAdapter.notifyItemRemoved(position);
                updateEmptyView();

                Snackbar.make(viewHolder.itemView, "Item removed from saved", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            Saved.addItem(deletedItem);
                            savedAdapter.savedItems.add(position, deletedItem);
                            savedAdapter.notifyItemInserted(position);
                            updateEmptyView();
                        })
                        .show();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(viewHolder.recyclerViewSaved);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdapterData();
        updateEmptyView();
    }

    @Override
    public void onItemClick(FurnitureModel furniture) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("furnitureName", furniture.getFurnitureName());
        startActivity(intent);
    }

    public void updateEmptyView() {
        if (savedAdapter.savedItems.isEmpty()) {
            viewHolder.productWatermark.setVisibility(View.VISIBLE);
            viewHolder.noProductMsg.setVisibility(View.VISIBLE);
        } else {
            viewHolder.productWatermark.setVisibility(View.GONE);
            viewHolder.noProductMsg.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAIL_ACTIVITY && resultCode == Activity.RESULT_OK) {
            updateAdapterData();
            updateEmptyView();
        }
    }

    private void updateAdapterData() {
        savedAdapter.updateData();
        savedAdapter.notifyDataSetChanged();

        // Hide the SearchView and show the title
        viewHolder.searchView.setVisibility(View.GONE);
        viewHolder.titleTextView.setVisibility(View.VISIBLE);
    }

    private class ViewHolder {
        RecyclerView recyclerViewSaved;
        TextView titleTextView;
        SearchView searchView;
        FloatingActionButton searchIcon;
        BottomNavigationView bottomNavigationView;
        TextView noProductMsg;
        ImageView productWatermark;

        ViewHolder() {
            recyclerViewSaved = findViewById(R.id.saved_recycler_view);
            titleTextView = findViewById(R.id.title);
            searchView = findViewById(R.id.list_search_view);
            searchIcon = findViewById(R.id.search_icon);
            bottomNavigationView = findViewById(R.id.bottomNavigation);
            noProductMsg = findViewById(R.id.emptyListText);
            productWatermark = findViewById(R.id.furnitureWatermark);
        }
    }
}