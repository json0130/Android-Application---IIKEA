package com.example.iikeaapp.Helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iikeaapp.adapter.ItemTouchHelperAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final ItemTouchHelperAdapter mAdapter;

    public SwipeToDeleteCallback(ItemTouchHelperAdapter adapter) {
        super(0, ItemTouchHelper.RIGHT);
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Not needed for swipe-to-remove functionality
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.onItemDismiss(position);
    }
}