package com.example.iikeaapp.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class FurnitureModel implements Serializable{
    String furnitureName;
    String category;
    double price;
    String description;
    String[] imageResources;
    private boolean isSaved;
    private int viewCount;

    public FurnitureModel(String furnitureName, String category, double price, String description, String[] imageResources) {
        this.furnitureName = furnitureName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageResources = imageResources;
        this.isSaved = false;
        this.viewCount = 0;
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

    public int getViewCount() {
        return viewCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FurnitureModel that = (FurnitureModel) o;
        return Double.compare(that.price, price) == 0 &&
                isSaved == that.isSaved &&
                Objects.equals(furnitureName, that.furnitureName) &&
                Objects.equals(category, that.category) &&
                Objects.equals(description, that.description) &&
                Arrays.equals(imageResources, that.imageResources);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(furnitureName, category, price, description, isSaved);
        result = 31 * result + Arrays.hashCode(imageResources);
        return result;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
