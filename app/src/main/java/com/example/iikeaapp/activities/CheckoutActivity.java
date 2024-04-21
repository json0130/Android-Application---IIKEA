package com.example.iikeaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.example.iikeaapp.manager.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CheckoutActivity extends AppCompatActivity {
    private ShoppingCart shoppingCart;
    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        viewHolder = new ViewHolder();
        viewHolder.checkOutButton.setEnabled(false); // Initially disable the checkout button

        // Add TextWatcher to the EditText views to enable/disable the checkout button
        addTextWatcherToEditText(R.id.editText1, viewHolder.checkOutButton);
        addTextWatcherToEditText(R.id.editText2, viewHolder.checkOutButton);
        addTextWatcherToEditText(R.id.editText3, viewHolder.checkOutButton);
        addTextWatcherToEditText(R.id.ccv_edit, viewHolder.checkOutButton);
        addTextWatcherToEditText(R.id.editText4, viewHolder.checkOutButton);

        // Apply the current theme mode
        ThemeManager.setNightMode(this, ThemeManager.getNightMode(this));

        shoppingCart = CartManager.getInstance().getShoppingCart();
        viewHolder.checkOutButton.setOnClickListener(v -> {
            if (areRequiredFieldsFilled()) {
                Intent intent = new Intent(CheckoutActivity.this, ThankYouActivity.class);
                startActivity(intent);

                // Clear the shopping cart
                shoppingCart.clearCart();
            }
        });

        setupSearchView();
        setupBottomNavigation();
    }

    private void setupSearchView() {
        viewHolder.searchIcon.setOnClickListener(v -> toggleSearchViewVisibility());

        viewHolder.titleTextView.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, CartActivity.class);
            startActivity(intent);
        });

        viewHolder.searchView.setOnCloseListener(() -> {
            viewHolder.searchView.setVisibility(View.GONE);
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
            viewHolder.searchView.startAnimation(AnimationUtils.loadAnimation(CheckoutActivity.this, R.anim.search_animation));
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
        if (viewHolder.searchView.getVisibility() == View.VISIBLE) {
            viewHolder.searchView.setVisibility(View.GONE);
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.titleTextView.setVisibility(View.GONE);
            viewHolder.searchView.setVisibility(View.VISIBLE);
            viewHolder.searchView.setIconified(false);
            viewHolder.searchView.startAnimation(AnimationUtils.loadAnimation(CheckoutActivity.this, R.anim.search_animation));
        }
    }

    private void startListActivity(String query) {
        Intent intent = new Intent(CheckoutActivity.this, ListActivity.class);
        intent.putExtra("searchQuery", query);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        viewHolder.bottomNavigationView.setSelectedItemId(0);
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
        viewHolder.totalPriceTextView.setText(String.format("$%.2f", totalPrice));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTotalPrice();

        // Hide the SearchView and show the title
        viewHolder.searchView.setVisibility(View.GONE);
        viewHolder.titleTextView.setVisibility(View.VISIBLE);
    }

    private boolean areRequiredFieldsFilled() {
        String email = viewHolder.emailField.getText().toString().trim();
        String phone = viewHolder.phoneField.getText().toString().trim();
        String cardNumber = viewHolder.cardField.getText().toString().trim();
        String ccv = viewHolder.ccvField.getText().toString().trim();
        String address = viewHolder.addressField.getText().toString().trim();

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

    private class ViewHolder {
        TextView titleTextView;
        SearchView searchView;
        FloatingActionButton searchIcon;
        BottomNavigationView bottomNavigationView;
        MaterialButton checkOutButton;
        TextView totalPriceTextView;
        TextView emailField, phoneField, cardField, ccvField, addressField;

        ViewHolder() {
            searchIcon = findViewById(R.id.search_icon);
            titleTextView = findViewById(R.id.title);
            searchView = findViewById(R.id.list_search_view);
            bottomNavigationView = findViewById(R.id.bottomNavigation);
            checkOutButton = findViewById(R.id.checkout_button);
            totalPriceTextView = findViewById(R.id.total_cost_price);
            emailField = findViewById(R.id.editText1);
            phoneField = findViewById(R.id.editText2);
            cardField = findViewById(R.id.editText3);
            ccvField = findViewById(R.id.ccv_edit);
            addressField = findViewById(R.id.editText4);
        }
    }
}