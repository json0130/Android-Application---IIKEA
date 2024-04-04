package com.example.iikeaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.data.Furniture;

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.FurnitureViewHolder> {
    private final List<Furniture> furnitureList;
    public FurnitureAdapter(HashMap<String, Furniture> furnitureMap) {
        furnitureList = new ArrayList<>(furnitureMap.values());
    }

    public static class FurnitureViewHolder extends RecyclerView.ViewHolder {
        public ImageView furnitureImage;
        public TextView furnitureName;
        public TextView furniturePrice;

        public FurnitureViewHolder(@NonNull View itemView) {
            super(itemView);
            furnitureImage = itemView.findViewById(R.id.furniture_image);
            furnitureName = itemView.findViewById(R.id.furniture_name);
            furniturePrice = itemView.findViewById(R.id.furniture_price);
        }
    }

    @NonNull
    @Override
    public FurnitureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.furniture_listview_layout, parent, false);
        return new FurnitureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FurnitureViewHolder holder, int position) {
        Furniture furnitureItem = furnitureList.get(position);
        holder.furnitureImage.setImageResource(furnitureItem.getImageResource());
        holder.furnitureName.setText(furnitureItem.getName());
        holder.furniturePrice.setText("Price: $" + furnitureItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return furnitureList.size();
    }
}