package com.company.dptrends.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.dptrends.Interface.ItemClickListener;
import com.company.dptrends.R;

import org.jetbrains.annotations.NotNull;

public class AdminOrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView name,phone,orderNo,datetime;
    private ItemClickListener itemClickListener;
    public Button showProductsBtn;

    public AdminOrderViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.adminOrderLayout_UserName);
        phone = itemView.findViewById(R.id.adminOrderLayout_userPhone);
        orderNo = itemView.findViewById(R.id.adminOrderLayout_orderNo);
        datetime = itemView.findViewById(R.id.adminOrderLayout_dateTime);
        showProductsBtn = itemView.findViewById(R.id.adminOrderLayout_showProduct_btn);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
