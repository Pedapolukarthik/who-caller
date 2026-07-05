/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.androplaza.whocaller.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private final List<String> tags;
    private final OnTagClickListener onTagClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    Context context;

    public TagAdapter(Context context, List<String> tags, OnTagClickListener onTagClickListener) {
        this.tags = tags;
        this.onTagClickListener = onTagClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String tag = tags.get(position);
        holder.bind(tag, position);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        private final Chip tagChip;

        TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagChip = itemView.findViewById(R.id.tagChip);
            if (tagChip == null) {
                Log.e("TagAdapter", "Error: Chip view not found!");
            }
        }

        void bind(String tag, int position) {
            if (tagChip != null) {
                tagChip.setText(tag);
                tagChip.setChipIcon(getIcon(context, tag));

                tagChip.setChecked(position == selectedPosition);
                tagChip.setOnClickListener(v -> {
                    int previousPosition = selectedPosition;
                    selectedPosition = getBindingAdapterPosition();
                    notifyItemChanged(previousPosition);
                    notifyItemChanged(selectedPosition);
                    onTagClickListener.onTagClick(tag);
                });
            }
        }
    }


    public static Drawable getIcon(Context context, String tag) {
        Drawable drawable = null;
        switch (tag) {
            case "Sports And Recreation":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_sports_cricket_24);
                return drawable;
            case "Transportation / Automotive":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_local_taxi_24);
                return drawable;
            case "Nightlife & Drinks":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_local_bar_24);
                return drawable;
            case "Travel & Tourism":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_airplanemode_active_24);
                return drawable;
            case "Hotels & Accommodation":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_hotel_24);
                return drawable;
            case "Education":
                drawable = ContextCompat.getDrawable(context, R.drawable.edu);
                return drawable;
            case "Entertainment & Arts":
                drawable = ContextCompat.getDrawable(context, R.drawable.media);
                return drawable;
            case "Finance & Insurance":
                drawable = ContextCompat.getDrawable(context, R.drawable.money);
                return drawable;
            case "Health & Wellness":
                drawable = ContextCompat.getDrawable(context, R.drawable.health_care);
                return drawable;
            case "Restaurants & Cafés":
                drawable = ContextCompat.getDrawable(context, R.drawable.restaurant);
                return drawable;
            case "Services":
                drawable = ContextCompat.getDrawable(context, R.drawable.repair_tool);
                return drawable;
            case "Shopping & Convenience Stores":
                drawable = ContextCompat.getDrawable(context, R.drawable.store);
                return drawable;
            case "Legal":
                drawable = ContextCompat.getDrawable(context, R.drawable.auction);
                return drawable;
            case "Beauty And Personal Care":
                drawable = ContextCompat.getDrawable(context, R.drawable.makeup);
                return drawable;
            case "Property":
                drawable = ContextCompat.getDrawable(context, R.drawable.apartment);
                return drawable;
            case "Religious Place":
                drawable = ContextCompat.getDrawable(context, R.drawable.pray);
                return drawable;
            case "":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_person_24);
                return drawable;
        }

        return null;
    }

    public interface OnTagClickListener {
        void onTagClick(String tag);
    }
}
