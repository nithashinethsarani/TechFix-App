package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techfix_app.R;
import com.example.techfix_app.models.RepairImage;

import java.io.File;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private List<RepairImage> imageList;

    public GalleryAdapter(List<RepairImage> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery_image, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        RepairImage image = imageList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(new File(image.getImagePath()))   // local file path load karanawa
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgRepair);

        holder.tvCaption.setText(image.getCaption());
        holder.tvCategory.setText(image.getDeviceCategory());
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRepair;
        TextView tvCaption, tvCategory;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRepair = itemView.findViewById(R.id.imgRepairItem);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}