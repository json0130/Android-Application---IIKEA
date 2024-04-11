package com.example.iikeaapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static class ViewHolder {
        public final RecyclerView items;
        public final Button filterButton;
        public final Button sortButton;
        public final SearchView searchView;

        public ViewHolder(Activity activity) {
            items = activity.findViewById(R.id.furniture_recycler_view);
            filterButton = activity.findViewById(R.id.listview_filter_button);
            sortButton = activity.findViewById(R.id.listview_sort_button);
            searchView = activity.findViewById(R.id.list_search_view);
        }
    }
    private ViewHolder vh;
    private RecyclerView recyclerView;
    private FurnitureAdapter adapter;
    private HashMap<String, Furniture> furnitureMap;
  
    Button filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterButton = findViewById(R.id.listview_filter_button);
        setContentView(R.layout.activity_list);
        furnitureMap = DataProvider.getFurnitureItems();

        vh = new ViewHolder(this);

        recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Need to check if we are using the hash map or arraylist for data?
        //adapter = new FurnitureAdapter(furnitureMap);
        //recyclerView.setAdapter(adapter);

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

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = dialog.findViewById(R.id.layoutShorts);
        LinearLayout liveLayout = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(ListActivity.this,"Upload a Video is clicked",Toast.LENGTH_SHORT).show();

            }
        });

        shortsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(ListActivity.this,"Create a short is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        liveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(ListActivity.this,"Go live is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void showFilterBottomSheet() {
        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_listview_layout, null);

        // Create a BottomSheetDialog instance
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Set the content view of the BottomSheetDialog
        bottomSheetDialog.setContentView(bottomSheetView);

        // Show the BottomSheetDialog
        bottomSheetDialog.show();
    }

//    private void onFilterButtonClicked(View v) {
//        // Create an instance of the filter fragment
//        FilterPage filterPage = new FilterPage();
//
//        // Set up the callback for receiving filter options
//        filterPage.setOnOptionChangedCallback(new FilterPage.OptionsChangedCallback() {
//            @Override
//            public void onOptionsChanged(FilterPage.FilterOptions options) {
//                // Handle the filter options here
//                // For example, you can apply filtering to your RecyclerView adapter
//            }
//        });
//
//        // Get the fragment manager and start a transaction
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//        // Add the filter fragment to the container
//        transaction.add(R.id.fil, filterPage);
//
//        // Show the container
//        findViewById(R.id.filter_container).setVisibility(View.VISIBLE);
//
//        // Commit the transaction
//        transaction.commit();
//    }

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
