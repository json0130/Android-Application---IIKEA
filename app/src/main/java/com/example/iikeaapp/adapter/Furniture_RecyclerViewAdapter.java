package com.example.iikeaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iikeaapp.R;
import com.example.iikeaapp.data.FurnitureModel;

import java.util.ArrayList;

public class Furniture_RecyclerViewAdapter extends RecyclerView.Adapter<Furniture_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<FurnitureModel> furnitureModels;

    public Furniture_RecyclerViewAdapter(Context context, ArrayList<FurnitureModel> furnitureModels) {
        this.context = context;
        this.furnitureModels = furnitureModels;
    }

    @NonNull
    @Override
    public Furniture_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row_top_picks, parent, false);

        return new Furniture_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Furniture_RecyclerViewAdapter.MyViewHolder holder, int position) {
        FurnitureModel model = furnitureModels.get(position);
        holder.textView.setText(model.getFurnitureName());

        Glide.with(context)
                .load(model.getImageResources()[0])
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return furnitureModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView3);
            textView = itemView.findViewById(R.id.textView6);
        }
    }
}
