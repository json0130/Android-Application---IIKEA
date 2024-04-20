package com.example.iikeaapp.data;

public enum FurnitureTag {
    LIGHTING("Lighting"),
    OUTDOOR("Outdoor"),
    OFFICE("Office"),
    LIVING("Living");

    public final String tagName;

    FurnitureTag(String tagName) {
        this.tagName = tagName;
    }
}
