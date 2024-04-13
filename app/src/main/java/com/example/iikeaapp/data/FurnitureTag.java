package com.example.iikeaapp.data;

import java.util.Optional;

public enum FurnitureTag {
    LIGHTING("Lighting"),
    OUTDOOR("Outdoor"),
    OFFICE("Office"),
    LIVING("Living");

    public final String tagName;

    FurnitureTag(String tagName) {
        this.tagName = tagName;
    }

    public static Optional<FurnitureTag> parseString(String string) {
        String lowerCaseString = string.toLowerCase();
        for (FurnitureTag tag : values()) {
            if (tag.tagName.toLowerCase().equals(lowerCaseString)) {
                return Optional.of(tag);
            }
        }
        return Optional.empty();
    }
}
