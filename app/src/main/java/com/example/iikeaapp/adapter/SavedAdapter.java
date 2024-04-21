package com.example.iikeaapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iikeaapp.R;
import com.example.iikeaapp.activities.SaveActivity;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.manager.Saved;

import java.util.ArrayList;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(FurnitureModel item);
    }

    public ArrayList<FurnitureModel> savedItems;
    private Context context;
    private OnItemClickListener listener;

    public SavedAdapter(Context context, OnItemClickListener listener) {
        this.savedItems = new ArrayList<>(Saved.getInstance().getSavedItems());
        this.context = context;
        this.listener = listener;
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

        Glide.with(context)
                .load(furniture.getImageResources()[0])
                .placeholder(R.drawable.image_placeholder)
                .into(holder.imageView);

        holder.savedButton.setOnClickListener(v -> {
            // Start the slide-out animation
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
            holder.itemView.startAnimation(animation);

            // Remove the item from the list after the animation ends
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Saved.removeItem(furniture);
                savedItems.remove(position);
                notifyItemRemoved(position);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("removedItem", furniture);
                ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
                updateEmptyView();
            }, animation.getDuration());
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(furniture);
            }
        });
    }

    @Override
    public int getItemCount() {
        return savedItems.size();
    }

    public void updateData() {
        savedItems = new ArrayList<>(Saved.getInstance().getSavedItems());
    }

    private void updateEmptyView() {
        if (context instanceof SaveActivity) {
            ((SaveActivity) context).updateEmptyView();
        }
    }

    public static class SavedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView furnitureNameTextView;
        TextView furniturePriceTextView;
        ImageView savedButton;

        public SavedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.view);
            furnitureNameTextView = itemView.findViewById(R.id.furniture_item_title);
            furniturePriceTextView = itemView.findViewById(R.id.furniture_total_price);
            savedButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}