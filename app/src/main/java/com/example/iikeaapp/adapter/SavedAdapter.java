package com.example.iikeaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.SavedFurniture;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.SavedManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedViewHolder> {

    private List<Map.Entry<FurnitureModel, Integer>> cartItems;
    private Context context;
    private SavedFurniture savedFurniture;

    public SavedAdapter(Context context) {
        this.savedFurniture = SavedManager.getInstance().getSavedFurniture();
        this.cartItems = new ArrayList<>(savedFurniture.getItems().entrySet());
        this.context = context;
    }

    @NonNull
    @Override
    public SavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.furniture_saved_layout, parent, false);
        return new SavedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedViewHolder holder, int position) {
        Map.Entry<FurnitureModel, Integer> entry = cartItems.get(position);
        FurnitureModel furniture = entry.getKey();
        int quantity = entry.getValue();

        holder.furnitureNameTextView.setText(furniture.getFurnitureName());
        holder.furniturePriceTextView.setText(String.format("$%.2f", furniture.getPrice()));
        holder.quantityTextView.setText(String.valueOf(quantity));
        holder.totalPriceTextView.setText(String.format("$%.2f", furniture.getPrice() * quantity));

        // Add click listeners or other functionality as needed
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class SavedViewHolder extends RecyclerView.ViewHolder {
        TextView furnitureNameTextView;
        TextView furniturePriceTextView;
        TextView quantityTextView;
        TextView totalPriceTextView;

        public SavedViewHolder(@NonNull View itemView) {
            super(itemView);
            furnitureNameTextView = itemView.findViewById(R.id.furniture_item_title);
            furniturePriceTextView = itemView.findViewById(R.id.furniture_item_price);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            totalPriceTextView = itemView.findViewById(R.id.furniture_total_price);
        }
    }
}