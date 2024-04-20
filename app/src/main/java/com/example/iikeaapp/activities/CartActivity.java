package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.CartAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnItemClickListener {
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private ShoppingCart shoppingCart;
    private TextView totalPriceTextView;
    private TextView titleTextView;
    private androidx.appcompat.widget.SearchView searchView;
    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 1;
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
        cartAdapter = new CartAdapter(this, new ArrayList<>(shoppingCart.getItems().entrySet()), this);
        recyclerViewCart.setAdapter(cartAdapter);

        // Set up the checkout button click listener
        MaterialButton checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
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

        titleTextView.setOnClickListener(v -> {
            // Expand the search bar and hide the title with animation
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            startActivity(intent);
        });

        searchView.setOnCloseListener(() -> {
            // Collapse the search bar and show the title with animation
            searchView.setVisibility(View.GONE);
            titleTextView.setVisibility(View.VISIBLE);
            searchView.startAnimation(AnimationUtils.loadAnimation(CartActivity.this, R.anim.search_animation));
            return false;
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

        // Set up ItemTouchHelper for swipe-to-delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Map.Entry<FurnitureModel, Integer> entry = cartAdapter.getCartItems().get(position);
                FurnitureModel deletedItem = entry.getKey();
                int deletedQuantity = entry.getValue();
                shoppingCart.removeItem(deletedItem);
                cartAdapter.getCartItems().remove(position);
                cartAdapter.notifyItemRemoved(position);
                updateTotalPrice();

                Snackbar.make(recyclerViewCart, "Item removed from cart", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            shoppingCart.addItem(deletedItem, deletedQuantity);
                            cartAdapter.getCartItems().add(position, new AbstractMap.SimpleEntry<>(deletedItem, deletedQuantity));
                            cartAdapter.notifyItemInserted(position);
                            updateTotalPrice();
                        })
                        .show();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewCart);
    }

    public void updateTotalPrice() {
        double totalPrice = shoppingCart.getTotalCost();
        totalPriceTextView.setText(String.format("$%.2f", totalPrice));
        updateCartUI();
    }

    private void updateCartUI() {
        RelativeLayout cartSummaryLayout = findViewById(R.id.cart_summary_layout);
        TextView noProductMsg = findViewById(R.id.emptyListText);
        Button placeOrderButton = findViewById(R.id.checkout_button);
        ImageView productWatermark = findViewById(R.id.furnitureWatermark);
        if (cartAdapter.getItemCount() == 0) {
            noProductMsg.setVisibility(View.VISIBLE);
            productWatermark.setVisibility(View.VISIBLE);
            recyclerViewCart.setVisibility(View.GONE);
            cartSummaryLayout.setVisibility(View.GONE);
            placeOrderButton.setVisibility(View.GONE);
        } else {
            noProductMsg.setVisibility(View.GONE);
            productWatermark.setVisibility(View.GONE);
            recyclerViewCart.setVisibility(View.VISIBLE);
            cartSummaryLayout.setVisibility(View.VISIBLE);
            placeOrderButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartData();

        // Hide the SearchView and show the title
        searchView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(FurnitureModel furniture) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("furnitureName", furniture.getFurnitureName());
        startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAIL_ACTIVITY_REQUEST_CODE) {
            refreshCartData();
        }
    }

    private void refreshCartData() {
        cartAdapter.cartItems = new ArrayList<>(shoppingCart.getItems().entrySet());
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }
}