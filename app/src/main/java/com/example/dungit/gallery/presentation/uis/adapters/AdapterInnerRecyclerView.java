package com.example.dungit.gallery.presentation.uis.adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.GlideApp;
import com.example.dungit.gallery.presentation.MyAppGlideModule;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.activities.MainActivity;
import com.example.dungit.gallery.presentation.uis.activities.PreviewPhotoActivity;

import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/18/2018.
 */

public  class AdapterInnerRecyclerView extends RecyclerView.Adapter<AdapterInnerRecyclerView.InnerViewHolder> {

    private ArrayList<Photo> data;
    private Context context;

    public AdapterInnerRecyclerView(Context context, ArrayList<Photo> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterInnerRecyclerView.InnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_img_item,
                parent, false);
        return new InnerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull InnerViewHolder holder, int position) {
        final Photo curPhoto = data.get(position);

        GlideApp.with(context).load(curPhoto.getUrl())
                .placeholder(R.drawable.place_holder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(holder.ivItem);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(context, PreviewPhotoActivity.class);
                int imgPostion = 0;
                if(context instanceof  MainActivity){
                    MainActivity mainActivity = (MainActivity) context;
                    ArrayList<Photo> photos = mainActivity.getArrListPhoto();
                    PreviewPhotoActivity.setPhotos(photos);
                    imgPostion=  photos.indexOf(data.get(position));
                    imgPostion = imgPostion >=0 ? imgPostion : 0;
                }else{
                    PreviewPhotoActivity.setPhotos(data);
                    imgPostion=position;
                }
                intent.putExtra(PreviewPhotoActivity.IMG_POSITION,imgPostion);
                try {
                    context.startActivity(intent);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class InnerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        private ImageView ivItem;
        private ItemClickListener itemClickListener;

        public InnerViewHolder(View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.iv_item);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }
    }
}