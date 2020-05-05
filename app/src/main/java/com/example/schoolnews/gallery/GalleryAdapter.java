package com.example.schoolnews.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.schoolnews.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryHolder> {

    private Context context;
    private List<String> urls;

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_gallery, parent, false);
        context = parent.getContext();
        return new GalleryHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {
        Picasso.get()
                .load(urls.get(position))
                .error(R.drawable.birthday)
                .into(holder.getImageView());
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

}