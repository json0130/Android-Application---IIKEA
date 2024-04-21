package com.example.iikeaapp.manager;

import com.example.iikeaapp.data.ShoppingCart;

public class CartManager {
    private static CartManager instance;
    private ShoppingCart shoppingCart;

    private CartManager() {
        shoppingCart = new ShoppingCart();
    }

    public static CartManager getInstance() {
        // singleton pattern
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }
}