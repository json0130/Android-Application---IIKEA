package com.example.iikeaapp.data;

public class CategoryModel {
    String categoryName;
    int image;

    public CategoryModel(String categoryName, int image) {
        this.categoryName = categoryName;
        this.image = image;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getImage() {
        return image;
    }
}
