package com.example.iikeaapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.adapter.FurnitureAdapter;
import com.example.iikeaapp.R;
import com.example.iikeaapp.data.FurnitureModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import android.widget.Toast;

public class ListActivity extends AppCompatActivity implements FurnitureAdapter.OnItemClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.bottom_save) {
                    startActivity(new Intent(getApplicationContext(), SaveActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
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
    }

    @Override
    public void onItemClick(FurnitureModel funiture) {
        Intent intent = new Intent(ListActivity.this, DetailActivity.class);
        intent.putExtra("furnitureName", funiture.getFurnitureName());
        intent.putExtra("category", funiture.getCategory());
        intent.putExtra("price", funiture.getPrice());
        intent.putExtra("description", funiture.getDescription());
        intent.putExtra("imageResources", funiture.getImageResources());
        startActivity(intent);
    }
}
