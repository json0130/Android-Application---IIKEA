package com.example.iikeaapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iikeaapp.R;
import com.example.iikeaapp.data.FurnitureColour;
import com.example.iikeaapp.data.FurnitureTag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.EnumMap;

public class FilterPage extends Fragment {

    public static final String TAG = "FilterPage";
    private ViewHolder vh;
    private OptionsChangedCallback onOptionChangedCallback;

    public interface OptionsChangedCallback {
        void onOptionsChanged(FilterOptions options);
    }

    public static class FilterOptions {
        public final EnumMap<FurnitureColour, Boolean> colours;
        public final EnumMap<FurnitureTag, Boolean> tags;

        private FilterOptions(EnumMap<FurnitureColour, Boolean> colours, EnumMap<FurnitureTag, Boolean> tags) {
            this.colours = colours;
            this.tags = tags;
        }
    }

    private static class ViewHolder {
        public final ChipGroup colorSelection;
        public final ChipGroup tagSelection;
        public final RangeSlider priceSlider;

        ViewHolder(View view) {
            colorSelection = view.findViewById(R.id.filter_color_group);
            tagSelection = view.findViewById(R.id.filter_tag_group);
            priceSlider = view.findViewById(R.id.filter_price_slider);
            priceSlider.setValues(0.1f, 0.9f);
        }
    }

    public FilterOptions getFilterOptions() {
        if (vh != null) {
            EnumMap<FurnitureColour, Boolean> colours = new EnumMap<>(FurnitureColour.class);
            EnumMap<FurnitureTag, Boolean> tags = new EnumMap<>(FurnitureTag.class);

            for (int i = 0; i < vh.colorSelection.getChildCount(); i++) {
                Chip chip = (Chip) vh.colorSelection.getChildAt(i);
                colours.put(FurnitureColour.valueOf(chip.getText().toString().toUpperCase()), chip.isChecked());
            }

            for (int i = 0; i < vh.tagSelection.getChildCount(); i++) {
                Chip chip = (Chip) vh.tagSelection.getChildAt(i);
                tags.put(FurnitureTag.valueOf(chip.getText().toString().toUpperCase()), chip.isChecked());
            }

            return new FilterOptions(colours, tags);
        }

        return null;
    }

    public void setOnOptionChangedCallback(OptionsChangedCallback onOptionChangedCallback) {
        this.onOptionChangedCallback = onOptionChangedCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.filter_listview_layout, container, false);
        vh = new ViewHolder(v);

        // Initialize color chips
        initializeColorChips();

        // Rest of your code

        return v;
    }

    private void initializeColorChips() {
        String[] colors = {"Red", "Blue", "Green", "Yellow", "Black", "White"};

        for (String color : colors) {
            Chip chip = new Chip(getContext());
            chip.setText(color);
            chip.setCheckable(true);
            chip.setChecked(false); // Set default selection state

            // Create a new instance of the listener for each chip
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (onOptionChangedCallback != null) {
                        onOptionChangedCallback.onOptionsChanged(getFilterOptions());
                    }
                }
            });

            vh.colorSelection.addView(chip);
        }
    }
}
