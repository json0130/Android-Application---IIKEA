package com.example.iikeaapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.adapter.FurnitureAdapter;
import com.example.iikeaapp.adapter.Furniture_VerticalRecyclerViewAdapter;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.SavedFurniture;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;
import com.example.iikeaapp.manager.SavedManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;


public class ListActivity extends AppCompatActivity implements FurnitureAdapter.OnItemClickListener {
    ArrayList<FurnitureModel> furnitureModels = new ArrayList<>();

    private SavedFurniture savedFurniture;

    private ShoppingCart shoppingCart;

    @Override
    public void onItemClick(FurnitureModel furniture) {
        Intent intent = new Intent(ListActivity.this, DetailActivity.class);
        intent.putExtra("FurnitureModel", furniture);
        startActivity(intent);
    }
//    @Override
//    public void onItemClick (FurnitureModel funiture){
//        Intent intent = new Intent(ListActivity.this, DetailActivity.class);
//        intent.putExtra("furnitureName", funiture.getFurnitureName());
//        intent.putExtra("category", funiture.getCategory());
//        intent.putExtra("price", funiture.getPrice());
//        intent.putExtra("description", funiture.getDescription());
//        intent.putExtra("imageResources", funiture.getImageResources());
//        startActivity(intent);
//    }

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

        savedFurniture = SavedManager.getInstance().getSavedFurniture();
        shoppingCart = CartManager.getInstance().getShoppingCart();

        setUpFurnitureModels();

        // init recycler views
        RecyclerView recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateAdapter(furnitureModels);

        // Setup SearchView
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.list_search_view);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFurnitureBySearchQuery(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFurnitureBySearchQuery(newText);
                return true;
            }
        });

        String category = getIntent().getStringExtra("category");
        String searchQuery = getIntent().getStringExtra("searchQuery");
        if (category != null || searchQuery != null) {
            ArrayList<FurnitureModel> filteredModels = filterModels(category, searchQuery);
            updateAdapter(filteredModels);
        }

        // nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.bottom_save) {
                    Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                } else if (item.getItemId() == R.id.bottom_home) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                } else if (item.getItemId() == R.id.bottom_cart) {
                    Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                }
                return false;
            });

        MaterialButton filterbutton = findViewById(R.id.listview_filter_button);
        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ListActivity.this);
                View view = LayoutInflater.from(ListActivity.this).inflate(R.layout.filter_listview_layout, null);
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Toast.makeText(ListActivity.this, "Bottom Sheet dismissed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        MaterialButton sortButton = findViewById(R.id.listview_sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ListActivity.this);
                View view = LayoutInflater.from(ListActivity.this).inflate(R.layout.sort_listview_layout, null);
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();

                RadioGroup sortGroup = view.findViewById(R.id.sort_group);
                sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.sort_high_low) {
                            sortFurnitureByPrice(true);
                        } else if (checkedId == R.id.sort_low_high) {
                            sortFurnitureByPrice(false);
                        }
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
    }

    private void sortFurnitureByPrice(boolean highestToLowest) {
        if (highestToLowest) {
            Collections.sort(furnitureModels, (o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
        } else {
            Collections.sort(furnitureModels, (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
        }
        updateAdapter(furnitureModels);
    }

    private void updateAdapter(ArrayList<FurnitureModel> models) {
        Furniture_VerticalRecyclerViewAdapter adapter = new Furniture_VerticalRecyclerViewAdapter(this, models);
        RecyclerView recyclerView = findViewById(R.id.furniture_recycler_view);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<FurnitureModel> filterModels(String category, String searchQuery) {
        ArrayList<FurnitureModel> filteredModels = new ArrayList<>();
        for (FurnitureModel model : furnitureModels) {
            boolean matchesCategory = category == null || model.getCategory().equalsIgnoreCase(category);
            boolean matchesSearch = searchQuery == null || model.getFurnitureName().toLowerCase().contains(searchQuery.toLowerCase());
            if (matchesCategory && matchesSearch) {
                filteredModels.add(model);
            }
        }
        return filteredModels;
    }

    private void filterFurnitureBySearchQuery(String query) {
        ArrayList<FurnitureModel> filteredModels = new ArrayList<>();
        for (FurnitureModel model : furnitureModels) {
            if (model.getFurnitureName().toLowerCase().contains(query.toLowerCase())) {
                filteredModels.add(model);
            }
        }
        updateAdapter(filteredModels);
    }

    private void setUpFurnitureModels() {
        // String[] furnitureNames = getResources().getStringArray(R.array.furnitures);
        ArrayList<String> furnitureNames = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> image1URLs = new ArrayList<>();
        ArrayList<String> image2URLs = new ArrayList<>();
        ArrayList<String> image3URLs = new ArrayList<>();


        try {
            JSONObject obj = new JSONObject(loadJSONfromAssets());

            JSONArray productArray = obj.getJSONArray("products");

            // load products from JSON to arraylists
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject productDetail = productArray.getJSONObject(i);

                furnitureNames.add(productDetail.getString("name"));
                categories.add(productDetail.getString("category"));
                prices.add(productDetail.getDouble("price"));
                descriptions.add(productDetail.getString("description"));

                // nested values
                JSONObject imageResources = productDetail.getJSONObject("imageResources");
                image1URLs.add(imageResources.getString("image1"));
                image2URLs.add(imageResources.getString("image2"));
                image3URLs.add(imageResources.getString("image3"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < furnitureNames.size(); i++) {
            String[] tempImages = {image1URLs.get(i), image2URLs.get(i), image3URLs.get(i)};
            FurnitureModel temp = new FurnitureModel(furnitureNames.get(i), categories.get(i), prices.get(i), descriptions.get(i), tempImages);
            furnitureModels.add(temp);
        }
        savedFurniture.addOnSavedFurnitureChangeListener(new SavedFurniture.OnSavedFurnitureChangeListener() {
            @Override
            public void onSavedFurnitureChanged() {
                for (FurnitureModel model : furnitureModels) {
                    model.setSaved(savedFurniture.getItems().containsKey(model));
                }
                updateAdapter(furnitureModels);
            }
        });
    }

    private String loadJSONfromAssets() {
        String json;

        try {
            InputStream is = getAssets().open("catalogue.json");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return json;
    }
}
