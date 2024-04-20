package com.example.iikeaapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.iikeaapp.R;


public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    String[] imageUrls;
    LayoutInflater mLayoutInflater;

    public ViewPagerAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.furniture_item, container, false);
        ImageView imageView = itemView.findViewById(R.id.pagerImageView);

        Glide.with(context)
                .load(imageUrls[position])
                .placeholder(R.drawable.image_placeholder)
                .into(imageView);

        imageView.setOnClickListener(v -> {
            // expand image
            showImageDialog(imageUrls[position]);
        });

        container.addView(itemView);

        return itemView;
    }

    private void showImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_fullscreen);

        ImageView image = dialog.findViewById(R.id.fullscreen_image);
        Glide.with(context).load(imageUrl).into(image);

        dialog.show();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}

