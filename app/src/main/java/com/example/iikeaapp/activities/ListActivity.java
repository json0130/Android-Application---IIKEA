package com.example.iikeaapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.adapter.FurnitureAdapter;
import com.example.iikeaapp.data.DataProvider;
import com.example.iikeaapp.R;
import com.example.iikeaapp.data.Furniture;

import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    private static class ViewHolder {
        public final RecyclerView items;
        public final Button filterButton;
        public final Button sortButton;

        public ViewHolder(Activity activity) {
            items = activity.findViewById(R.id.furniture_recycler_view);
            filterButton = activity.findViewById(R.id.list_filter_button);
            sortButton = activity.findViewById(R.id.list_sort_button);
        }
    }

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HashMap<String, Furniture> furnitureMap = DataProvider.getFurnitureItems();

        recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FurnitureAdapter(furnitureMap));
    }
}