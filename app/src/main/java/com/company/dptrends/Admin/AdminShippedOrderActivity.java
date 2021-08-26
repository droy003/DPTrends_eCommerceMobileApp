package com.company.dptrends.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.company.dptrends.Model.AdminOrders;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.AdminOrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class AdminShippedOrderActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference orderRef;
    private Button placedOrder, deliveredOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_shipped_products);

        placedOrder = findViewById(R.id.adminShippedOrder_OrderPlaced_btn);
        placedOrder.setOnClickListener(v->{
            Intent i = new Intent(AdminShippedOrderActivity.this, AdminNewOrderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        deliveredOrder=findViewById(R.id.adminSippedOrder_DeliveredProducts_btn);
        deliveredOrder.setOnClickListener(v->{
            Intent i = new Intent(AdminShippedOrderActivity.this, AdminDeliveredOrderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        orderList = findViewById(R.id.adminShippedOrder_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("OrderShipped");
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(orderRef.orderByChild("revKey"),AdminOrders.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull AdminOrderViewHolder holder, int position, @NonNull @NotNull AdminOrders model) {
                        holder.name.setText("Name: "+model.getUserName());
                        holder.phone.setText("Phone: "+model.getUserPhone());
                        holder.datetime.setText("Date: "+model.getDate());
                        String oID = getRef(position).getKey();
                        holder.orderNo.setText("Order No: "+ oID);
                        holder.showProductsBtn.setOnClickListener(v->{

                            Intent i = new Intent(AdminShippedOrderActivity.this, AdminShippedOrderProductsActivity.class);
                            i.putExtra("phone",model.getUserPhone());
                            i.putExtra("orderID",oID);
                            startActivity(i);
                        });

                    }

                    @NonNull
                    @NotNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_order_layout,parent,false);
                        return new AdminOrderViewHolder(view);
                    }
                };
        orderList.setAdapter(adapter);
        adapter.startListening();
    }
}