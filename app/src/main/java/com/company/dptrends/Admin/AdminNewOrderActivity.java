package com.company.dptrends.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.company.dptrends.Model.AdminOrders;
import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.AdminOrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference orderRef;
    private Button showProductsBtn;
    private Button shippedOrder, deliveredOrder;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        loadingBar = new ProgressDialog(this);
        shippedOrder = findViewById(R.id.adminOrder_ShippedProducts_btn);
        shippedOrder.setOnClickListener(v->{
            Intent i = new Intent(AdminNewOrderActivity.this, AdminShippedOrderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        deliveredOrder=findViewById(R.id.adminOrder_DeliveredProducts_btn);
        deliveredOrder.setOnClickListener(v->{
            Intent i = new Intent(AdminNewOrderActivity.this, AdminDeliveredOrderActivity.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        orderList = findViewById(R.id.adminOrder_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("OrderPlaced");

            FirebaseRecyclerOptions<AdminOrders> options =
                    new FirebaseRecyclerOptions.Builder<AdminOrders>()
                            .setQuery(orderRef.orderByChild("revKey"),AdminOrders.class)
                            .build();


            FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder> adapter =
                    new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull @NotNull AdminOrderViewHolder holder, int position, @NonNull @NotNull AdminOrders model) {
                            String oID = getRef(position).getKey();
                            holder.name.setText("Name: "+model.getUserName());
                            holder.phone.setText("Phone: "+model.getUserPhone());
                            holder.datetime.setText("Date: "+model.getDate());
                            holder.orderNo.setText("Order No: "+ oID);
                            holder.showProductsBtn.setOnClickListener(v->{

                                Intent i = new Intent(AdminNewOrderActivity.this, AdminNewOrderProductsActivity.class);
                                i.putExtra("phone",model.getUserPhone());
                                i.putExtra("orderID",oID);
                                startActivity(i);
                            });
                            holder.itemView.setOnClickListener(v->{
                                CharSequence options[] = new CharSequence[]{
                                        "Confirm Delete",
                                        "Cancel"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                                builder.setTitle("Delete Order?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i==0){
                                            //String oID = getRef(position).getKey();
                                            RemoveOrder(oID,model.getUserPhone());
                                            Toast.makeText(AdminNewOrderActivity.this, "Order Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(i==1){
                                            dialogInterface.dismiss();
                                        }
                                    }
                                });
                                builder.show();
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

    private void RemoveOrder(String oID,String phone) {
        loadingBar.setTitle("Deleting Order...");
        loadingBar.setMessage("Wait while processing......");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        DatabaseReference orderProductRef = FirebaseDatabase.getInstance().getReference().child(DBNodes.dbOrderProducts).child(oID)
                .child(DBNodes.dbProductsKey);

        orderProductRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, Object> quantityMap = new HashMap<>();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    int quantCart = Integer.parseInt(snapshot1.child("quantity").getValue().toString());

                    DatabaseReference prodQuantRef = FirebaseDatabase.getInstance().getReference().child(DBNodes.dbProducts)
                            .child(Objects.requireNonNull(snapshot1.child("productID").getValue()).toString());

                    prodQuantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            int quantProd = Integer.parseInt(snapshot2.child("quantity").getValue().toString());
                            int leftQuant = quantProd+quantCart;
                            quantityMap.put("quantity", String.valueOf(leftQuant));
                            prodQuantRef.updateChildren(quantityMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        HashMap<String, Object> statusUpdateOrderUser = new HashMap<>();
        statusUpdateOrderUser.put("status", "OrderCancelled");
        FirebaseDatabase.getInstance().getReference().child(DBNodes.dbOrderUser).child(phone)
                .child(oID).updateChildren(statusUpdateOrderUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                HashMap<String, Object> statusUpdateUser = new HashMap<>();
                statusUpdateUser.put("status", "NewOrder");
                FirebaseDatabase.getInstance().getReference().child(DBNodes.dbUsers).child(phone)
                        .updateChildren(statusUpdateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if(task.isSuccessful()){
                            orderRef.child(oID).removeValue();
                        }
                    }
                });
            }
        });


    }
}