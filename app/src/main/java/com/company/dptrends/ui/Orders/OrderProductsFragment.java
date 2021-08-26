package com.company.dptrends.ui.Orders;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.dptrends.Model.Cart;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class OrderProductsFragment extends Fragment {

    private DatabaseReference orderProductsRef;
    private String orderID="";
    private RecyclerView orderProductList;
    RecyclerView.LayoutManager layoutManager1;

    public static OrderProductsFragment newInstance() {
        return new OrderProductsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_products, container, false);

        orderID = getArguments().getString("orderID");
        orderProductList = view.findViewById(R.id.orderProductsFragment_List);
        layoutManager1 = new LinearLayoutManager(getActivity());
        orderProductList.setLayoutManager(layoutManager1);



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();


        orderProductsRef= FirebaseDatabase.getInstance().getReference().child("OrderProducts")
                .child(orderID).child("Products");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(orderProductsRef,Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new
                FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull CartViewHolder holder, int position, @NonNull @NotNull Cart model) {
                        holder.productName.setText(model.getProductName());
                        if(model.getImage() != null || TextUtils.isEmpty(model.getImage())){
                            Picasso.get().load(model.getImage()).into(holder.productImage);
                        }
                        holder.productQuantity.setText("Quantity: "+model.getQuantity());
                        int total = (Integer.parseInt(model.getQuantity())*Integer.parseInt(model.getPrice()));
                        holder.productPrice.setText(new StringBuilder().append("Total Price: ₹ ").append(String.valueOf(total)).append(" /-").toString());
                        holder.moreVert.setVisibility(View.GONE);
                    }

                    @NonNull
                    @NotNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder cartViewHolder = new CartViewHolder(view);
                        return cartViewHolder;


                    }

                };
        orderProductList.setAdapter(adapter);
        adapter.startListening();

    }


}