package com.example.iikeaapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.manager.Saved;

import java.util.ArrayList;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedViewHolder> {

    private ArrayList<FurnitureModel> savedItems;
    private Context context;
    private int requestCode;

    private static final int REQUEST_CODE_SAVE_ACTIVITY = 100;

    public SavedAdapter(Context context, int requestCode) {
        this.savedItems = new ArrayList<>(Saved.getInstance().getSavedItems());
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
        FurnitureModel furniture = savedItems.get(position);

        holder.furnitureNameTextView.setText(furniture.getFurnitureName());
        holder.furniturePriceTextView.setText(String.format("$%.2f", furniture.getPrice()));

        holder.savedButton.setOnClickListener(v -> {
            Saved.removeItem(furniture);
            savedItems.remove(position);
            notifyItemRemoved(position);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("removedItem", furniture);
            ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
        });
    }

    @Override
    public int getItemCount() {
        return savedItems.size();
    }

    public void updateData() {
        savedItems = new ArrayList<>(Saved.getInstance().getSavedItems());
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
