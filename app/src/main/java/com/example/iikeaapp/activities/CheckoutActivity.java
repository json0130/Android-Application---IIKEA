package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iikeaapp.R;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CheckoutActivity extends AppCompatActivity {
    private ShoppingCart shoppingCart;
    private TextView titleTextView;
    private androidx.appcompat.widget.SearchView searchView;

    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        MaterialButton checkOutButton = findViewById(R.id.checkout_button);
        checkOutButton.setEnabled(false); // Initially disable the checkout button

        // Add TextWatcher to the EditText views to enable/disable the checkout button
        addTextWatcherToEditText(R.id.editText1, checkOutButton);
        addTextWatcherToEditText(R.id.editText2, checkOutButton);
        addTextWatcherToEditText(R.id.editText3, checkOutButton);
        addTextWatcherToEditText(R.id.ccv_edit, checkOutButton);
        addTextWatcherToEditText(R.id.editText4, checkOutButton);

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));

        totalPriceTextView = findViewById(R.id.total_cost_price);

        shoppingCart = CartManager.getInstance().getShoppingCart();
        checkOutButton.setOnClickListener(v -> {
            if (areRequiredFieldsFilled()) {
                Intent intent = new Intent(CheckoutActivity.this, ThankYouActivity.class);
                startActivity(intent);

                // Clear the shopping cart
                shoppingCart.clearCart();
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
                searchView.startAnimation(AnimationUtils.loadAnimation(CheckoutActivity.this, R.anim.search_animation));
            }
        });

        titleTextView.setOnClickListener(v -> {
            // Expand the search bar and hide the title with animation
            Intent intent = new Intent(CheckoutActivity.this, CartActivity.class);
            startActivity(intent);
        });

        searchView.setOnCloseListener(() -> {
            // Collapse the search bar and show the title with animation
            searchView.setVisibility(View.GONE);
            titleTextView.setVisibility(View.VISIBLE);
            searchView.startAnimation(AnimationUtils.loadAnimation(CheckoutActivity.this, R.anim.search_animation));
            return false;
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Start ListActivity with the search query
                Intent intent = new Intent(CheckoutActivity.this, ListActivity.class);
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
        bottomNavigationView.setSelectedItemId(0);
        bottomNavigationView.setOnItemSelectedListener(item -> {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTotalPrice();

        // Hide the SearchView and show the title
        searchView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.VISIBLE);
    }

    private boolean areRequiredFieldsFilled() {
        String email = ((TextView) findViewById(R.id.editText1)).getText().toString().trim();
        String phone = ((TextView) findViewById(R.id.editText2)).getText().toString().trim();
        String cardNumber = ((TextView) findViewById(R.id.editText3)).getText().toString().trim();
        String ccv = ((TextView) findViewById(R.id.ccv_edit)).getText().toString().trim();
        String address = ((TextView) findViewById(R.id.editText4)).getText().toString().trim();

        return !email.isEmpty() && !phone.isEmpty() && !cardNumber.isEmpty() && !ccv.isEmpty() && !address.isEmpty();
    }

    private void addTextWatcherToEditText(int editTextId, MaterialButton checkOutButton) {
        TextView editText = findViewById(editTextId);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkOutButton.setEnabled(areRequiredFieldsFilled());
            }
        });
    }
}