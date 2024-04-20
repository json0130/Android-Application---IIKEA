package com.example.iikeaapp.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
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
import com.example.iikeaapp.activities.CartActivity;
import com.example.iikeaapp.data.FurnitureModel;
import com.example.iikeaapp.data.ShoppingCart;
import com.example.iikeaapp.manager.CartManager;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(FurnitureModel item);
    }

    public List<Map.Entry<FurnitureModel, Integer>> cartItems;
    private Context context;
    private ShoppingCart shoppingCart;
    private OnItemClickListener listener;

    public CartAdapter(Context context, List<Map.Entry<FurnitureModel, Integer>> cartItems, OnItemClickListener listener) {
        this.shoppingCart = CartManager.getInstance().getShoppingCart();
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.furniture_shopping_view_row, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Map.Entry<FurnitureModel, Integer> entry = cartItems.get(position);
        FurnitureModel furniture = entry.getKey();
        int quantity = entry.getValue();

        holder.furnitureNameTextView.setText(furniture.getFurnitureName());
        holder.quantityTextView.setText(String.valueOf(quantity));
        holder.totalPriceTextView.setText(String.format("$%.2f", furniture.getPrice() * quantity));

        // Load the furniture image if available
        Log.d("debug", context.toString());
        Log.d("debug", Arrays.toString(furniture.getImageResources()));
        Log.d("debug", holder.furnitureImageView.toString());
        Glide.with(context)
                .load(furniture.getImageResources()[0])
                .placeholder(R.drawable.image_placeholder)
                .into(holder.furnitureImageView);

        holder.plusButton.setOnClickListener(v -> {
            int newQuantity = quantity + 1;
            shoppingCart.updateQuantity(furniture, newQuantity);
            cartItems.set(position, new AbstractMap.SimpleEntry<>(furniture, newQuantity));
            notifyItemChanged(position);
            updateTotalPrice();
        });

        holder.minusButton.setOnClickListener(v -> {
            if (quantity > 1) {
                int newQuantity = quantity - 1;
                shoppingCart.updateQuantity(furniture, newQuantity);
                cartItems.set(position, new AbstractMap.SimpleEntry<>(furniture, newQuantity));
                notifyItemChanged(position);
                updateTotalPrice();
            } else {
                removeItem(holder, position);
            }
        });

        holder.removeButton.setOnClickListener(v -> removeItem(holder, position));
    }

    private void removeItem(CartViewHolder holder, int position) {
        // Start the slide-out animation
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
        holder.itemView.startAnimation(animation);

        // Remove the item from the list after the animation ends
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FurnitureModel furniture = cartItems.get(position).getKey();
            shoppingCart.removeItem(furniture);
            cartItems.remove(position);
            notifyItemRemoved(position);
            updateTotalPrice();
        }, animation.getDuration());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<Map.Entry<FurnitureModel, Integer>> getCartItems() {
        return cartItems;
    }

    private void updateTotalPrice() {
        if (context instanceof CartActivity) {
            ((CartActivity) context).updateTotalPrice();
        }
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView furnitureNameTextView;
        TextView quantityTextView;
        TextView totalPriceTextView;
        ImageView furnitureImageView;
        TextView plusButton;
        TextView minusButton;
        ImageView removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            furnitureNameTextView = itemView.findViewById(R.id.furniture_item_title);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            totalPriceTextView = itemView.findViewById(R.id.furniture_total_price);
            furnitureImageView = itemView.findViewById(R.id.furniture_image);
            plusButton = itemView.findViewById(R.id.plus_sign);
            minusButton = itemView.findViewById(R.id.minus_sign);
            removeButton = itemView.findViewById(R.id.remove_button);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(cartItems.get(position).getKey());
                }
            });
        }
    }
}