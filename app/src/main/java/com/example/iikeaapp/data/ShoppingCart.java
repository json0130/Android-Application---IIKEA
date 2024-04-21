package com.example.iikeaapp.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart implements Serializable {
    private Map<FurnitureModel, Integer> items = new HashMap<>();

    public void addItem(FurnitureModel item, int quantity) {
        // add to cart
        if (items.containsKey(item)) {
            int currentQuantity = items.get(item);
            items.put(item, currentQuantity + quantity);
        } else {
            items.put(item, quantity);
        }
    }

    public void removeItem(FurnitureModel item) {
        items.remove(item);
    }

    public void updateQuantity(FurnitureModel item, int quantity) {
        if (quantity > 0) {
            items.put(item, quantity);
        } else {
            removeItem(item);
        }
    }

    public Map<FurnitureModel, Integer> getItems() {
        return items;
    }

    public double getTotalCost() {
        double totalCost = 0;
        for (Map.Entry<FurnitureModel, Integer> entry : items.entrySet()) {
            totalCost += entry.getKey().getPrice() * entry.getValue();
        }
        return totalCost;
    }
    public void clearCart() {
        items.clear();
    }
}