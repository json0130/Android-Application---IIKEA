package com.example.iikeaapp.data;

import java.util.HashMap;
public class DataProvider {
    public static HashMap<String, Furniture> getFurnitureItems() {
        HashMap<String, Furniture> furnitureItems = new HashMap<>();
//        furnitureItems.put("sofa", new Furniture("Sofa", R.drawable.sofa_image, 599.99));
//        furnitureItems.put("dining_table", new Furniture("Dining Table", R.drawable.table_image, 399.99));
//        furnitureItems.put("armchair", new Furniture("Armchair", R.drawable.armchair_image, 249.99));
        return furnitureItems;
    }
}