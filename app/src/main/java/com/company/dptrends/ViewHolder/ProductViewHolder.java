package com.company.dptrends.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.dptrends.Interface.ItemClickListener;
import com.company.dptrends.R;

import org.jetbrains.annotations.NotNull;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView textProductName, textProductDescription,textProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;


    public ProductViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.product_image);
        textProductName = itemView.findViewById(R.id.product_name);
        textProductDescription = itemView.findViewById(R.id.product_description);
        textProductPrice = itemView.findViewById(R.id.product_price);
    }
    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
