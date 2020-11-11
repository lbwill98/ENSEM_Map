package com.example.ensem_map;


import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    int[] images;

    public MyAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImage(ImageSource.resource(images[position]));
        switch (position) {
            case 1:
                //zoom sur l'emplacement de la tablette dans l'ensem
                holder.imageView.setScaleAndCenter(0.5F, new PointF(2000, 5500));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return  images.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SubsamplingScaleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (SubsamplingScaleImageView)itemView.findViewById(R.id.ivPlan);
        }
    }
}

