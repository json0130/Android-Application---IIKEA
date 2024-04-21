package com.example.iikeaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.data.FurnitureModel;

import java.util.ArrayList;
import java.util.HashMap;

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.FurnitureViewHolder> implements Filterable {
    private ArrayList<FurnitureModel> furnitureList;
    private ArrayList<FurnitureModel> furnitureListFiltered;
    private OnItemClickListener listener;

    public FurnitureAdapter(HashMap<String, FurnitureModel> furnitureMap, OnItemClickListener listener) {
        furnitureList = new ArrayList<>(furnitureMap.values());
        furnitureListFiltered = furnitureList;
        this.listener = listener;
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
        FurnitureModel furnitureItem = furnitureListFiltered.get(position);
        holder.furnitureName.setText(furnitureItem.getFurnitureName());
        holder.furniturePrice.setText(String.format("Price: $%.2f", furnitureItem.getPrice()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(furnitureItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return furnitureListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    furnitureListFiltered = furnitureList;
                } else {
                    ArrayList<FurnitureModel> filteredList = new ArrayList<>();
                    for (FurnitureModel row : furnitureList) {
                        if (row.getFurnitureName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    furnitureListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = furnitureListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                furnitureListFiltered = (ArrayList<FurnitureModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class FurnitureViewHolder extends RecyclerView.ViewHolder {
        ImageView furnitureImage;
        TextView furnitureName;
        TextView furniturePrice;

        FurnitureViewHolder(@NonNull View itemView) {
            super(itemView);
            furnitureImage = itemView.findViewById(R.id.furniture_image);
            furnitureName = itemView.findViewById(R.id.furniture_name);
            furniturePrice = itemView.findViewById(R.id.furniture_price);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(FurnitureModel furniture);
    }
}