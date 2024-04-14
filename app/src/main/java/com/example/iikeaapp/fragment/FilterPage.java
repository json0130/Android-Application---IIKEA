package com.example.iikeaapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

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
        public final EnumMap<FurnitureTag, Boolean> tags;

        private FilterOptions(EnumMap<FurnitureTag, Boolean> tags) {
            this.tags = tags;
        }
    }

    private static class ViewHolder {
        public final ChipGroup tagSelection;
        public final RangeSlider priceSlider;

        ViewHolder(View view) {
            tagSelection = view.findViewById(R.id.filter_tag_group);
            priceSlider = view.findViewById(R.id.max_price_slider);
            priceSlider.setValues(0.1f, 0.9f);
        }
    }

    public FilterOptions getFilterOptions() {
        if (vh != null) {
            EnumMap<FurnitureTag, Boolean> tags = new EnumMap<>(FurnitureTag.class);

            for (int i = 0; i < vh.tagSelection.getChildCount(); i++) {
                Chip chip = (Chip) vh.tagSelection.getChildAt(i);
                tags.put(FurnitureTag.valueOf(chip.getText().toString().toUpperCase()), chip.isChecked());
            }

            return new FilterOptions(tags);
        }

        return null;
    }

    public void setOnOptionChangedCallback(OptionsChangedCallback onOptionChangedCallback) {
        this.onOptionChangedCallback = onOptionChangedCallback;
    }
}
