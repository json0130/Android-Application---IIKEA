package com.example.iikeaapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.iikeaapp.R;
<<<<<<< HEAD
import com.example.iikeaapp.adapter.SavedAdapter;
import com.example.iikeaapp.data.SavedFurniture;
import com.example.iikeaapp.manager.SavedManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SaveActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCart;
    private SavedAdapter savedAdapter;
    private SavedFurniture savedFurniture;
=======
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SaveActivity extends AppCompatActivity {

    private ShoppingCart shoppingCart;
>>>>>>> main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

<<<<<<< HEAD
        savedFurniture = SavedManager.getInstance().getSavedFurniture();

        // Initialize the RecyclerView
        recyclerViewCart = findViewById(R.id.favourite_recycler_view);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the SavedAdapter
        savedAdapter = new SavedAdapter(this);
        recyclerViewCart.setAdapter(savedAdapter);
=======
        shoppingCart = CartManager.getInstance().getShoppingCart();
>>>>>>> main

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
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            } else if (item.getItemId() == R.id.bottom_cart) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            }
            return false;
        });
    }
}