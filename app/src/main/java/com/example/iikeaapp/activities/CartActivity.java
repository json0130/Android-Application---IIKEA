package com.example.iikeaapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.CartAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.example.iikeaapp.manager.Saved;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnItemClickListener {
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private ShoppingCart shoppingCart;
    private TextView totalPriceTextView;
    private TextView titleTextView;
    private androidx.appcompat.widget.SearchView searchView;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        totalPriceTextView = findViewById(R.id.total_cost_price);

        shoppingCart = CartManager.getInstance().getShoppingCart();

        // Initialize the RecyclerView
        recyclerViewCart = findViewById(R.id.furniture_recycler_view);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the CartAdapter
        cartAdapter = new CartAdapter(this, this);
        recyclerViewCart.setAdapter(cartAdapter);

        // Set up the checkout button click listener
        MaterialButton checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
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
                searchView.startAnimation(AnimationUtils.loadAnimation(CartActivity.this, R.anim.search_animation));
            }
        });

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Expand the search bar and hide the title with animation
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Collapse the search bar and show the title with animation
                searchView.setVisibility(View.GONE);
                titleTextView.setVisibility(View.VISIBLE);
                searchView.startAnimation(AnimationUtils.loadAnimation(CartActivity.this, R.anim.search_animation));
                return false;
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Start ListActivity with the search query
                Intent intent = new Intent(CartActivity.this, ListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Button shopMoreButton = findViewById(R.id.browseButton);
        shopMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });


        // Set up the bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_cart);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_cart) {
                return true;
            } else if (item.getItemId() == R.id.bottom_home) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.bottom_save) {
                Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
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

    public void updateTotalPrice() {
        double totalPrice = shoppingCart.getTotalCost();
        totalPriceTextView.setText(String.format("$%.2f", totalPrice));
        // You can also update any other UI elements related to the total price here
        // empty list msg
        RelativeLayout cartSummaryLayout = findViewById(R.id.cart_summary_layout);
        TextView noProductMsg = findViewById(R.id.emptyListText);
        Button placeOrderButton = findViewById(R.id.checkout_button);
        Button browseButton = findViewById(R.id.browseButton);
        if (cartAdapter.getItemCount() == 0) {
            noProductMsg.setVisibility(View.VISIBLE);
            recyclerViewCart.setVisibility(View.GONE);
            cartSummaryLayout.setVisibility(View.GONE);
            placeOrderButton.setVisibility(View.GONE);
            browseButton.setVisibility(View.VISIBLE);
        } else {
            noProductMsg.setVisibility(View.GONE);
            recyclerViewCart.setVisibility(View.VISIBLE);
            cartSummaryLayout.setVisibility(View.VISIBLE);
            placeOrderButton.setVisibility(View.VISIBLE);
            browseButton.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();

        // Hide the SearchView and show the title
        searchView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(FurnitureModel furniture) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("furnitureName", furniture.getFurnitureName());
        startActivity(intent);
    }
}