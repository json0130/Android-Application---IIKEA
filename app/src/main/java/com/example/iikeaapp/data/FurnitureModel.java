package com.example.iikeaapp.data;

import java.io.Serializable;

// this one uses json
public class FurnitureModel implements Serializable{
    String furnitureName;
    String category;
    double price;
    String description;
    String[] imageResources;
    private boolean isSaved;

    public FurnitureModel(String furnitureName, String category, double price, String description, String[] imageResources) {
        this.furnitureName = furnitureName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageResources = imageResources;
        this.isSaved = false;
    }

    public String getFurnitureName() {
        return furnitureName;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String[] getImageResources() {
        return imageResources;
    }

    public boolean isSaved() {
        return isSaved;
    }

    // Add the setSaved() method
    public void setSaved(boolean saved) {
        isSaved = saved;
    }
}
