package com.example.schoolnews.gallery;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolnews.R;

class GalleryHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public GalleryHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById( R.id.image_gallery);
    }

    public ImageView getImageView() {
        return imageView;
    }
}
