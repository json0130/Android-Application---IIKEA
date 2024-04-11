package com.example.iikeaapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_save);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_save) {
                return true;
            } else if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_cart) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
                return true;
            }
            return false;
        });

        MaterialButton filterbutton = findViewById(R.id.listview_filter_button);
        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ListActivity.this);
                View view = LayoutInflater.from(ListActivity.this).inflate(R.layout.filter_listview_layout,null);
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Toast.makeText(ListActivity.this,"Bottom Sheet dismissed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        MaterialButton sortbutton = findViewById(R.id.listview_sort_button);
        sortbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ListActivity.this);
                View view = LayoutInflater.from(ListActivity.this).inflate(R.layout.sort_listview_layout,null);
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Toast.makeText(ListActivity.this,"Bottom Sheet dismissed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        furnitureMap = DataProvider.getFurnitureItems();

        vh = new ViewHolder(this);

        recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FurnitureAdapter(furnitureMap);
        recyclerView.setAdapter(adapter);
    }
}
