package com.company.dptrends.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.dptrends.Interface.ItemClickListener;
import com.company.dptrends.R;

public class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ItemClickListener itemClickListener;
    public TextView categoryName;

    public CategoriesViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.category_layout_text);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


}
