package com.example.ensem_map;


import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;

/**
 * class pour l'affichage des images des plans dans le viewPager
 * A FAIRE : commenter les fonctions
 *           charger les cartes avec le chemin tracés depuis le stockage externe après une recherche
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static final int[] images = {R.drawable.rdj,R.drawable.rdc,R.drawable.premier, R.drawable.deuxieme,R.drawable.troisieme};

    public MyAdapter() {}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(Route.etagesPresents.contains(position-1)){
            holder.imageView.setImage(ImageSource.uri(File.DirectoryPath+"/"+File.etagesNames[position]));
        }else {
            holder.imageView.setImage(ImageSource.resource(images[position]));
        }
        if(position == 1){
            //zoom sur l'emplacement de la tablette dans l'ensem
            holder.imageView.setScaleAndCenter(0.5F, new PointF(2000, 5500));
        }
    }

    @Override
    public int getItemCount() {
        return  images.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SubsamplingScaleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivPlan);
        }
    }
}

