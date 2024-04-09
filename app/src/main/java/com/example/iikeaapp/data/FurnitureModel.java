package com.example.iikeaapp.data;

public class FurnitureModel {
    String furnitureName;
    String category;
    int price;
    String description;
    String[] imageResources;

    public FurnitureModel(String furnitureName, String category, int price, String description, String[] imageResources) {
        this.furnitureName = furnitureName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageResources = imageResources;
    }

    public String getFurnitureName() {
        return furnitureName;
    }

    public String getCategory() {
        return category;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String[] getImageResources() {
        return imageResources;
    }
}
