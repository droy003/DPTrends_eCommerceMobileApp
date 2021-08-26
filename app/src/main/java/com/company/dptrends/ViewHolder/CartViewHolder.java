package com.company.dptrends.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.dptrends.Interface.ItemClickListener;
import com.company.dptrends.R;

import org.jetbrains.annotations.NotNull;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productName,productPrice,productQuantity;
    private ItemClickListener itemClickListener;
    public ImageView productImage,moreVert;

    public CartViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.cartitems_productname_txt);
        productPrice = itemView.findViewById(R.id.cartitems_productprice_txt);
        productQuantity = itemView.findViewById(R.id.cartitems_productquantity_txt);
        productImage = itemView.findViewById(R.id.cartitems_imageView);
        moreVert = itemView.findViewById(R.id.cartitems_more_vert);

    }


    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
