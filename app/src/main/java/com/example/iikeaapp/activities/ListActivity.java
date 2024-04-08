package com.example.iikeaapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iikeaapp.adapter.FurnitureAdapter;
import com.example.iikeaapp.data.DataProvider;
import com.example.iikeaapp.R;
import com.example.iikeaapp.data.Furniture;
import com.example.iikeaapp.fragment.FilterPage;

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
    private Button filterButton;

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

        // filtering
        // Set up click listener for the filter button
        vh.filterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show the filter fragment
                    onFilterButtonClicked(v);
                }
        });
    }

    private void onFilterButtonClicked(View v) {
        // Create an instance of the filter fragment
        FilterPage filterPage = new FilterPage();

        // Set up the callback for receiving filter options
        filterPage.setOnOptionChangedCallback(new FilterPage.OptionsChangedCallback() {
            @Override
            public void onOptionsChanged(FilterPage.FilterOptions options) {
                // Handle the filter options here
                // For example, you can apply filtering to your RecyclerView adapter
            }
        });

        // Get the fragment manager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Add the filter fragment to the container
        transaction.add(R.id.filter_container, filterPage);

        // Show the container
        findViewById(R.id.filter_container).setVisibility(View.VISIBLE);

        // Commit the transaction
        transaction.commit();
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
