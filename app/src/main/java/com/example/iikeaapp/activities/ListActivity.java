package com.example.iikeaapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iikeaapp.adapter.FurnitureAdapter;
import com.example.iikeaapp.data.DataProvider;
import com.example.iikeaapp.R;
import com.example.iikeaapp.data.Furniture;
import java.util.HashMap;
import android.widget.Filter;
import android.widget.Filterable;

public class ListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static class ViewHolder {
        public final RecyclerView items;
        public final Button filterButton;
        public final Button sortButton;
        public final SearchView searchView;

        public ViewHolder(Activity activity) {
            items = activity.findViewById(R.id.furniture_recycler_view);
            filterButton = activity.findViewById(R.id.list_filter_button);
            sortButton = activity.findViewById(R.id.list_sort_button);
            searchView = activity.findViewById(R.id.list_search_view);
        }
    }
    private ViewHolder vh;
    private RecyclerView recyclerView;
    private FurnitureAdapter adapter;
    private HashMap<String, Furniture> furnitureMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        furnitureMap = DataProvider.getFurnitureItems();

        vh = new ViewHolder(this);

        recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FurnitureAdapter(furnitureMap);
        recyclerView.setAdapter(adapter);

        // Searching
        vh.searchView.setOnQueryTextListener(this);
        String search = getIntent().getStringExtra("INTENT_SEARCH_FILTER");
        if(search != null) {
            vh.searchView.setQuery(search, true);
            vh.searchView.setQueryHint(search);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Not needed in this case, because you're filtering instantly with each text change
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Filter the adapter with the new query
        adapter.getFilter().filter(newText);
        return true;
    }
}
