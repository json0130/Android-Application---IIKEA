package com.example.iikeaapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.Furniture_HorizontalRecyclerViewAdapter;
import com.example.iikeaapp.adapter.ViewPagerAdapter;
import com.example.iikeaapp.data.DataProvider;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.example.iikeaapp.manager.Saved;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    ArrayList<FurnitureModel> furnitureModels = new ArrayList<>();
    private ShoppingCart shoppingCart;
    private int quantity = 1;
    private ViewHolder vh;


    private static class ViewHolder {
        public final ViewPager mViewPager;
        public final TextView furnitureItemTitle, itemPrice, itemDescription, quantityTextView,
                decrementButton, incrementButton, titleTextView;
        public final androidx.appcompat.widget.SearchView searchView;
        public final TabLayout tabLayout;
        public final ImageView saveHeart;
        MaterialButton addToCartButton;
        FloatingActionButton searchIcon;
        BottomNavigationView bottomNavigationView;
        RecyclerView recyclerViewRelatedItems;


        public ViewHolder(Activity activity) {
            searchView = activity.findViewById(R.id.list_search_view);
            titleTextView = activity.findViewById(R.id.title);
            mViewPager = activity.findViewById(R.id.viewPager);
            tabLayout = activity.findViewById(R.id.tabDots);
            furnitureItemTitle = activity.findViewById(R.id.furniture_item_title);
            itemPrice = activity.findViewById(R.id.item_price);
            itemDescription = activity.findViewById(R.id.item_description);
            saveHeart = activity.findViewById(R.id.save_heart);
            quantityTextView = activity.findViewById(R.id.quantity_text_view);
            decrementButton = activity.findViewById(R.id.decrement_button);
            incrementButton = activity.findViewById(R.id.increment_button);
            addToCartButton = activity.findViewById(R.id.add_to_cart_btn);
            searchIcon = activity.findViewById(R.id.search_icon);
            bottomNavigationView = activity.findViewById(R.id.bottomNavigation);
            recyclerViewRelatedItems = activity.findViewById(R.id.related_items_recyclerView);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        vh = new ViewHolder(this);

        furnitureModels = DataProvider.getInstance(this).getFurnitureModels();

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));
        // Get the ShoppingCart instance from the CartManager
        shoppingCart = CartManager.getInstance().getShoppingCart();

        // took out find by id's into the vh here
        vh.tabLayout.setupWithViewPager(vh.mViewPager, true);

        // Retrieve the FurnitureModel object from the Intent
        String furnitureName = getIntent().getStringExtra("furnitureName");
        FurnitureModel furnitureModel = DataProvider.getInstance(this)
                .getFurnitureByName(furnitureName);

        // If the FurnitureModel object is available, use it directly
        if (furnitureModel != null) {
            furnitureModel.incrementViewCount();
            updateUIWithFurnitureModel(furnitureModel);
        } else {
            // Otherwise, create a new FurnitureModel object from the individual data fields
            furnitureModel = getFurnitureItem();
            if (furnitureModel != null) {
                updateUIWithFurnitureModel(furnitureModel);
            }
        }

        updateHeartIcon(vh.saveHeart, furnitureModel);
        FurnitureModel finalFurnitureModel = furnitureModel;
        vh.saveHeart.setOnClickListener(v -> {
            if (Saved.isSaved(finalFurnitureModel)) {
                Saved.removeItem(finalFurnitureModel);
                vh.saveHeart.setImageResource(R.drawable.heart_background);
            } else {
                Saved.addItem(finalFurnitureModel);
                vh.saveHeart.setImageResource(R.drawable.heart_red_fill);
            }
            Toast.makeText(this, Saved.isSaved(finalFurnitureModel) ? "Added to Favorites" : "Removed from Favorites", Toast.LENGTH_SHORT).show();
        });


        // Set the initial quantity text
        vh.quantityTextView.setText(String.valueOf(this.quantity));

        // Set click listeners for increment and decrement buttons
        vh.decrementButton.setOnClickListener(v -> {
            if (this.quantity > 1) {
                this.quantity--;
                vh.quantityTextView.setText(String.valueOf(this.quantity));
            }
        });

        vh.incrementButton.setOnClickListener(v -> {
            this.quantity++;
            vh.quantityTextView.setText(String.valueOf(this.quantity));
        });

        // Add to shopping cart
        FurnitureModel finalFurnitureModel1 = furnitureModel;
        vh.addToCartButton.setOnClickListener(v -> {
            if (finalFurnitureModel1 != null) {
                shoppingCart.addItem(finalFurnitureModel1, this.quantity);
                Toast.makeText(DetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        });

        vh.searchIcon.setOnClickListener(v -> {
            // Toggle the visibility of the SearchView and title
            if (vh.searchView.getVisibility() == View.VISIBLE) {
                vh.searchView.setVisibility(View.GONE);
                vh.titleTextView.setVisibility(View.VISIBLE);
            } else {
                vh.titleTextView.setVisibility(View.GONE);
                vh.searchView.setVisibility(View.VISIBLE);
                vh.searchView.setIconified(false);
                vh.searchView.startAnimation(AnimationUtils.loadAnimation(DetailActivity.this, R.anim.search_animation));
            }
        });

        vh.searchView.setOnCloseListener(() -> {
            // Collapse the search bar and show the title with animation
            vh.searchView.setVisibility(View.GONE);
            vh.titleTextView.setVisibility(View.VISIBLE);
            vh.searchView.startAnimation(AnimationUtils.loadAnimation(DetailActivity.this, R.anim.search_animation));
            return false;
        });

        vh.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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

        vh.titleTextView.setOnClickListener(v -> {
            // Expand the search bar and hide the title with animation
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // nav bar
        vh.bottomNavigationView.setSelectedItemId(0);
        vh.bottomNavigationView.setOnItemSelectedListener(item -> {
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
                Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }else if (item.getItemId() == R.id.bottom_setting) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void updateHeartIcon(ImageView saveHeart, FurnitureModel furnitureModel) {
        if (Saved.isSaved(furnitureModel)) {
            saveHeart.setImageResource(R.drawable.heart_red_fill);
        } else {
            saveHeart.setImageResource(R.drawable.heart_background);
        }
    }

    private FurnitureModel getFurnitureItem() {
        // Retrieve the FurnitureModel object from the Intent
        String furnitureName = getIntent().getStringExtra("furnitureName");
        return DataProvider.getInstance(this).getFurnitureByName(furnitureName);
    }

    private void updateUIWithFurnitureModel(FurnitureModel furnitureModel) {
        vh.furnitureItemTitle.setText(furnitureModel.getFurnitureName());
        vh.itemPrice.setText(String.format("$%.2f", furnitureModel.getPrice()));
        vh.itemDescription.setText(furnitureModel.getDescription());

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(DetailActivity.this, furnitureModel.getImageResources());
        vh.mViewPager.setAdapter(mViewPagerAdapter);

        // Setup related items RecyclerView
        setUpRelatedItemsRecyclerView(furnitureModel);
    }

    private void setUpRelatedItemsRecyclerView(FurnitureModel currentFurniture) {

        // Filter furniture models to only include items of the same category as the current item
        ArrayList<FurnitureModel> relatedModels = new ArrayList<>();
        for (FurnitureModel model : furnitureModels) {
            if (model.getCategory().equalsIgnoreCase(currentFurniture.getCategory()) && !model.getFurnitureName().equals(currentFurniture.getFurnitureName())) {
                relatedModels.add(model);
            }
        }

        // Set up the RecyclerView with the filtered list
        Furniture_HorizontalRecyclerViewAdapter fAdapter = new Furniture_HorizontalRecyclerViewAdapter(this, relatedModels);
        vh.recyclerViewRelatedItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        vh.recyclerViewRelatedItems.setAdapter(fAdapter);
    }
}
