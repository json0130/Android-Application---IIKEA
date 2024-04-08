package com.example.iikeaapp.data;

import java.util.Optional;

public enum FurnitureColour {
    RED("Red"),
    BLUE("Blue"),
    GREEN("Green"),
    YELLOW("Yellow"),
    BLACK("Black"),
    WHITE("White");

    public final String colourName;

    FurnitureColour(String colourName) {
        this.colourName = colourName;
    }

    public static Optional<FurnitureColour> parseString(String string) {
        String lowerCaseString = string.toLowerCase();
        for (FurnitureColour colour : values()) {
            if (colour.colourName.toLowerCase().equals(lowerCaseString)) {
                return Optional.of(colour);
            }
        }
        return Optional.empty();
    }
}
