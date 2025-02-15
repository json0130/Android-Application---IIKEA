package com.example.iikeaapp.manager;

import com.example.iikeaapp.data.FurnitureModel;

import java.util.ArrayList;

public class Saved {
    private static Saved instance;
    private static ArrayList<FurnitureModel> savedItems = new ArrayList<>();

    public static Saved getInstance() {
        // singleton pattern
        if (instance == null) {
            instance = new Saved();
        }
        return instance;
    }

    public static void addItem(FurnitureModel item) {
        savedItems.add(item);
    }
    public static void removeItem(FurnitureModel item) {
        savedItems.remove(item);
    }

    public static boolean isSaved(FurnitureModel item) {
        for (FurnitureModel savedItem : savedItems) {
            if (savedItem.getFurnitureName().equalsIgnoreCase(item.getFurnitureName())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<FurnitureModel> getSavedItems() {
        return savedItems;
    }
}