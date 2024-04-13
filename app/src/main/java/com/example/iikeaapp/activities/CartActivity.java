package com.example.iikeaapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.CartAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private ShoppingCart shoppingCart;

    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        totalPriceTextView = findViewById(R.id.total_cost_price);
        updateTotalPrice();

        shoppingCart = CartManager.getInstance().getShoppingCart();

        // Initialize the RecyclerView
        recyclerViewCart = findViewById(R.id.furniture_recycler_view);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the CartAdapter
        cartAdapter = new CartAdapter(this);
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

        // Set up the bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_cart);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_cart) {
                return true;
            } else if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_save) {
                startActivity(new Intent(getApplicationContext(), SaveActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateTotalPrice() {
        double totalPrice = shoppingCart.getTotalCost();
        totalPriceTextView.setText(String.format("Total Price: $%.2f", totalPrice));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }
}