package com.example.iikeaapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iikeaapp.R;
import com.example.iikeaapp.activities.DetailActivity;
import com.example.iikeaapp.data.FurnitureModel;

import java.util.ArrayList;

public class Furniture_VerticalRecyclerViewAdapter extends RecyclerView.Adapter<Furniture_VerticalRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<FurnitureModel> furnitureModels;

    public Furniture_VerticalRecyclerViewAdapter(Context context, ArrayList<FurnitureModel> furnitureModels) {
        this.context = context;
        this.furnitureModels = furnitureModels;
    }

    @NonNull
    @Override
    public Furniture_VerticalRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row_list_activity, parent, false);

        return new Furniture_VerticalRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Furniture_VerticalRecyclerViewAdapter.MyViewHolder holder, int position) {
        FurnitureModel model = furnitureModels.get(position);
        holder.textViewTitle.setText(model.getFurnitureName());
        holder.textViewPrice.setText("$" + model.getPrice());

        Glide.with(context)
                .load(model.getImageResources()[0])
                .placeholder(R.drawable.image_placeholder)
                .into(holder.imageView);

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("furnitureName", model.getFurnitureName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return furnitureModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_thumbnail);
            textViewTitle = itemView.findViewById(R.id.item_title);
            textViewPrice = itemView.findViewById(R.id.price_text);
        }
    }
}
