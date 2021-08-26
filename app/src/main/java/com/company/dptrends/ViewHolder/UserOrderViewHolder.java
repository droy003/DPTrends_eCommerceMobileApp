package com.company.dptrends.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.dptrends.Interface.ItemClickListener;
import com.company.dptrends.R;

public class UserOrderViewHolder extends RecyclerView.ViewHolder implements ItemClickListener {

    public TextView orderNoText,statusText, priceText,addressText,orderDate,deliverDate;
    public Button showProductsBtn;
    private ItemClickListener itemClickListener;
    public UserOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderNoText =itemView.findViewById(R.id.userOrderLayout_orderNo);
        statusText = itemView.findViewById(R.id.userOrderLayout_status);
        priceText = itemView.findViewById(R.id.userOrderLayout_price);
        addressText = itemView.findViewById(R.id.userOrderLayout_address);
        orderDate = itemView.findViewById(R.id.userOrderLayout_orderDate);
        //deliverDate = itemView.findViewById(R.id.userOrderLayout_deliverDate);
        showProductsBtn = itemView.findViewById(R.id.userOrderLayout_showProduct_btn);

    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
