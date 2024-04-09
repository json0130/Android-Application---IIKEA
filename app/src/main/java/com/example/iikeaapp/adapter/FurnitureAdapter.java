package com.example.iikeaapp.adapter;

import android.content.Context;
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
import android.widget.Filter;
import android.widget.Filterable;

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.FurnitureViewHolder> implements Filterable{
    ArrayList<Furniture> furnitureList;
    Context context;
    private ArrayList<Furniture> furnitureListFiltered;
    public FurnitureAdapter(ArrayList<Furniture> items) {
        furnitureList = items;
    }

    public class FurnitureViewHolder extends RecyclerView.ViewHolder {
        ImageView furnitureImage;
        TextView furnitureName;
        TextView furniturePrice;

        public FurnitureViewHolder(@NonNull View itemView) {
            super(itemView);
            furnitureImage = itemView.findViewById(R.id.furniture_image);
            furnitureName = itemView.findViewById(R.id.furniture_name);
            furniturePrice = itemView.findViewById(R.id.furniture_price);
        }
    }

    @NonNull
    @Override
    public FurnitureAdapter.FurnitureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.furniture_listview_layout,parent,false);
        return new FurnitureViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FurnitureViewHolder holder, int position) {
        Furniture furnitureItem = furnitureListFiltered.get(position);
        holder.furnitureImage.setImageResource(furnitureItem.getImageResource());
        holder.furnitureName.setText(furnitureItem.getName());
        holder.furniturePrice.setText("Price: $" + furnitureItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return furnitureList.size();
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
                    List<Furniture> filteredList = new ArrayList<>();
                    for (Furniture row : furnitureList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    //furnitureListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = furnitureListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                furnitureListFiltered = (ArrayList<Furniture>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}