package com.company.dptrends.ui.Orders;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.dptrends.Admin.AdminShippedOrderActivity;
import com.company.dptrends.Admin.AdminShippedOrderProductsActivity;
import com.company.dptrends.Model.UserOrders;
import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.AdminOrderViewHolder;
import com.company.dptrends.ViewHolder.UserOrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.zip.Inflater;

public class OrdersFragment extends Fragment {


    private RecyclerView userOrderList;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference userOrderRef;

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        userOrderList = view.findViewById(R.id.orderFragment_list);
        layoutManager =new LinearLayoutManager(getActivity());
        userOrderList.setLayoutManager(layoutManager);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        userOrderRef = FirebaseDatabase.getInstance().getReference().child(DBNodes.dbOrderUser).child(Prevalent.currentOnlineUser.getPhone());

        FirebaseRecyclerOptions<UserOrders> options = new FirebaseRecyclerOptions.Builder<UserOrders>()
                .setQuery(userOrderRef.orderByChild("revKey"),UserOrders.class)
                .build();

        FirebaseRecyclerAdapter<UserOrders, UserOrderViewHolder> adapter = new FirebaseRecyclerAdapter<UserOrders, UserOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserOrderViewHolder holder, int position, @NonNull UserOrders model) {
                String oID = getRef(position).getKey();
                holder.orderNoText.setText(new StringBuilder().append("Order# ").append(oID).toString());
                holder.statusText.setText(new StringBuilder().append(model.getStatus()));
                holder.priceText.setText(new StringBuilder().append(getString(R.string.price_rs)).append(model.getTotalAmount()).append(getString(R.string.price_end)).toString());
                holder.addressText.setText(new StringBuilder().append("Delivery Address: ").append(model.getDeliveryAddress()).toString());
                holder.orderDate.setText(new StringBuilder().append("Ordered On: ").append(model.getDate()).toString());
                if(model.getStatus().equals("OrderDelivered")){
                    holder.orderDate.setText(new StringBuilder().append("Delivered On: ").append(model.getDeliveredOn().substring(0, 11)).toString());
                }
                else{
                    holder.orderDate.setText(new StringBuilder().append("Ordered On: ").append(model.getDate()).toString());
                }
                holder.showProductsBtn.setOnClickListener(v->{

                    Bundle args = new Bundle();
                    args.putString("orderID",oID);
                    Navigation.findNavController(getView()).navigate(R.id.nav_order_products,args);

                });
            }

            @NonNull
            @Override
            public UserOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.user_order_layout,parent,false);
                return new UserOrderViewHolder(v);
            }
        };

        userOrderList.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    public void onStop() {
        super.onStop();

    }
}