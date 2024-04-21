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
import androidx.appcompat.widget.SearchView;
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
    private ViewHolder viewHolder;
    private CartAdapter cartAdapter;
    private ShoppingCart shoppingCart;
    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        viewHolder = new ViewHolder();
        shoppingCart = CartManager.getInstance().getShoppingCart();

        // Initialize the RecyclerView
        viewHolder.recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the CartAdapter
        cartAdapter = new CartAdapter(this, new ArrayList<>(
                shoppingCart.getItems().entrySet()), this
        );
        viewHolder.recyclerViewCart.setAdapter(cartAdapter);

        setupCheckoutButton();
        setupSearchView();
        setupBottomNavigation();
        setupItemTouchHelper();
    }

    private void setupCheckoutButton() {
        viewHolder.checkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void setupSearchView() {
        viewHolder.searchIcon.setOnClickListener(v -> toggleSearchViewVisibility());

        viewHolder.titleTextView.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            startActivity(intent);
        });

        viewHolder.searchView.setOnCloseListener(() -> {
            viewHolder.searchView.setVisibility(View.GONE);
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
            viewHolder.searchView.startAnimation(
                    AnimationUtils.loadAnimation(CartActivity.this, R.anim.search_animation)
            );
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
            viewHolder.searchView.startAnimation(
                    AnimationUtils.loadAnimation(CartActivity.this, R.anim.search_animation)
            );
        }
    }

    private void startListActivity(String query) {
        Intent intent = new Intent(CartActivity.this, ListActivity.class);
        intent.putExtra("searchQuery", query);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        viewHolder.bottomNavigationView.setSelectedItemId(R.id.bottom_cart);
        viewHolder.bottomNavigationView.setOnItemSelectedListener(item -> {
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

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target)
            {
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

                Snackbar.make(viewHolder.itemView, "Item removed from cart",
                                Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            shoppingCart.addItem(deletedItem, deletedQuantity);
                            cartAdapter.getCartItems().add(position, new AbstractMap.SimpleEntry<>(
                                    deletedItem, deletedQuantity
                                )
                            );
                            cartAdapter.notifyItemInserted(position);
                            updateTotalPrice();
                        })
                        .show();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(viewHolder.recyclerViewCart);
    }

    public void updateTotalPrice() {
        double totalPrice = shoppingCart.getTotalCost();
        viewHolder.totalPriceTextView.setText(String.format("$%.2f", totalPrice));
        updateCartUI();
    }

    private void updateCartUI() {
        if (cartAdapter.getItemCount() == 0) {
            viewHolder.noProductMsg.setVisibility(View.VISIBLE);
            viewHolder.productWatermark.setVisibility(View.VISIBLE);
            viewHolder.recyclerViewCart.setVisibility(View.GONE);
            viewHolder.cartSummaryLayout.setVisibility(View.GONE);
            viewHolder.checkoutButton.setVisibility(View.GONE);
        } else {
            viewHolder.noProductMsg.setVisibility(View.GONE);
            viewHolder.productWatermark.setVisibility(View.GONE);
            viewHolder.recyclerViewCart.setVisibility(View.VISIBLE);
            viewHolder.cartSummaryLayout.setVisibility(View.VISIBLE);
            viewHolder.checkoutButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartData();

        // Hide the SearchView and show the title
        viewHolder.searchView.setVisibility(View.GONE);
        viewHolder.titleTextView.setVisibility(View.VISIBLE);
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

    private class ViewHolder {
        RecyclerView recyclerViewCart;
        TextView totalPriceTextView;
        TextView titleTextView;
        SearchView searchView;
        FloatingActionButton searchIcon;
        BottomNavigationView bottomNavigationView;
        MaterialButton checkoutButton;
        RelativeLayout cartSummaryLayout;
        TextView noProductMsg;
        ImageView productWatermark;

        ViewHolder() {
            recyclerViewCart = findViewById(R.id.furniture_recycler_view);
            totalPriceTextView = findViewById(R.id.total_cost_price);
            titleTextView = findViewById(R.id.title);
            searchView = findViewById(R.id.list_search_view);
            searchIcon = findViewById(R.id.search_icon);
            bottomNavigationView = findViewById(R.id.bottomNavigation);
            checkoutButton = findViewById(R.id.checkout_button);
            cartSummaryLayout = findViewById(R.id.cart_summary_layout);
            noProductMsg = findViewById(R.id.emptyListText);
            productWatermark = findViewById(R.id.furnitureWatermark);
        }
    }
}