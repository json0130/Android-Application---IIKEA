package com.example.iikeaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.R;
import com.example.iikeaapp.data.FurnitureModel;

import java.util.ArrayList;

public class Card_RecyclerViewAdapter extends RecyclerView.Adapter<Card_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<FurnitureModel> furnitures;

    public Card_RecyclerViewAdapter(Context context, ArrayList<FurnitureModel> furnitures) {
        this.context = context;
        this.furnitures = furnitures;
    }

    @NonNull
    @Override
    public Card_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.furniture_shopping_view_row, parent, false);

        return new Card_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Card_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(furnitures.get(position).getFurnitureName());
        //holder.price.set(furnitures.get(position).getPrice());
        //holder.imageView.setImageResource(furnitures.get(position).getImageResources());
    }

    @Override
    public int getItemCount() {
        return furnitures.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        TextView price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.furniture_image);
            textView = itemView.findViewById(R.id.furniture_item_title);
            price = itemView.findViewById(R.id.furniture_item_price);
        }
    }
}
