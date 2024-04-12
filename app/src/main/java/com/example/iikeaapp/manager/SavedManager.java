package com.example.iikeaapp.manager;

import com.example.iikeaapp.data.SavedFurniture;

public class SavedManager {
    private static SavedManager instance;
    private SavedFurniture savedFurniture;

    private SavedManager() {
        savedFurniture = new SavedFurniture();
    }

    public static SavedManager getInstance() {
        if (instance == null) {
            instance = new SavedManager();
        }
        return instance;
    }

    public SavedFurniture getSavedFurniture() {
        return savedFurniture;
    }
}