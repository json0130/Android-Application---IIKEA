package com.example.iikeaapp.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DataProvider {
    private static DataProvider instance;
    private ArrayList<FurnitureModel> furnitureModels;
    private Context context;

    private DataProvider(Context context) {
        this.context = context;
        this.furnitureModels = loadFurnitureData();
    }

    public static synchronized DataProvider getInstance(Context context) {
        if (instance == null) {
            instance = new DataProvider(context.getApplicationContext());
        }
        return instance;
    }

    public ArrayList<FurnitureModel> getFurnitureModels() {
        return furnitureModels;
    }

    private ArrayList<FurnitureModel> loadFurnitureData() {
        ArrayList<FurnitureModel> models = new ArrayList<>();
        try {
            String json = loadJSONfromAssets();
            JSONArray productArray = new JSONObject(json).getJSONArray("products");
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject productDetail = productArray.getJSONObject(i);
                models.add(new FurnitureModel(
                        productDetail.getString("name"),
                        productDetail.getString("category"),
                        productDetail.getDouble("price"),
                        productDetail.getString("description"),
                        new String[]{
                                productDetail.getJSONObject("imageResources").getString("image1"),
                                productDetail.getJSONObject("imageResources").getString("image2"),
                                productDetail.getJSONObject("imageResources").getString("image3")
                        }
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return models;
    }

    private String loadJSONfromAssets() {
        try {
            InputStream is = context.getAssets().open("catalogue.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public FurnitureModel getFurnitureByName(String name) {
        for (FurnitureModel model : furnitureModels) {
            if (model.getFurnitureName().equals(name)) {
                return model;
            }
        }
        return null;
    }
}
