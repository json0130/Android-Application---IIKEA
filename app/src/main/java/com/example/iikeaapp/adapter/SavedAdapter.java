package com.example.iikeaapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.activities.DetailActivity;
import com.example.iikeaapp.activities.SaveActivity;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.SavedFurniture;
import com.example.iikeaapp.manager.SavedManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedViewHolder> {

    private List<Map.Entry<FurnitureModel, Integer>> savedItems;
    private Context context;
    private int requestCode;
    private SavedFurniture savedFurniture;

    private static final int REQUEST_CODE_SAVE_ACTIVITY = 100;

    public SavedAdapter(Context context, int requestCode) {
        this.savedFurniture = SavedManager.getInstance().getSavedFurniture();
        this.savedItems = new ArrayList<>(savedFurniture.getItems().entrySet());
        this.context = context;
        this.requestCode = requestCode;
    }

    @NonNull
    @Override
    public SavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.furniture_saved_layout, parent, false);
        return new SavedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedViewHolder holder, int position) {
        Map.Entry<FurnitureModel, Integer> entry = savedItems.get(position);
        FurnitureModel furniture = entry.getKey();

        holder.furnitureNameTextView.setText(furniture.getFurnitureName());
        holder.furniturePriceTextView.setText(String.format("$%.2f", furniture.getPrice()));

        holder.savedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedFurniture.removeItem(furniture);
                savedItems.remove(entry);
                notifyDataSetChanged();

                // Set the result intent with the removed item
                Intent resultIntent = new Intent();
                resultIntent.putExtra("removedItem", furniture);
                ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return savedItems.size();
    }

    public class SavedViewHolder extends RecyclerView.ViewHolder {
        TextView furnitureNameTextView;
        TextView furniturePriceTextView;
        ImageView savedButton;

        public SavedViewHolder(@NonNull View itemView) {
            super(itemView);
            furnitureNameTextView = itemView.findViewById(R.id.furniture_item_title);
            furniturePriceTextView = itemView.findViewById(R.id.furniture_item_price);
            savedButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}