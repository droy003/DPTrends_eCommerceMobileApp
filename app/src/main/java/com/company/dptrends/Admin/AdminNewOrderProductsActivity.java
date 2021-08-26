package com.company.dptrends.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.company.dptrends.Model.AdminOrders;
import com.company.dptrends.Model.Cart;
import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminNewOrderProductsActivity extends AppCompatActivity {

    private RecyclerView productList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartRef;
    private Button shipButton;
    private String orderID,userPhone;

    private ImageView moreLessIcon;
    private RelativeLayout relativeLayout2,relativeLayout4;
    private TextView orderNoText,orderStatusText,orderDateText,totalAmountText;
    private TextView userNameText,userPhoneText,userAddressText;
    private TextView deliveryNameText, deliveryPhoneText,deliveryAddressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders_products);

        orderID = getIntent().getStringExtra("orderID");
        userPhone = getIntent().getStringExtra("phone");
        productList = findViewById(R.id.adminProducts_list);
        productList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productList.setLayoutManager(layoutManager);

        orderNoText = findViewById(R.id.adminProducts_orderNo);
        orderStatusText = findViewById(R.id.adminProducts_orderStatus);
        orderDateText = findViewById(R.id.adminProducts_orderDate);
        totalAmountText = findViewById(R.id.adminProducts_totalAmount);
        userNameText = findViewById(R.id.adminProducts_userName);
        userPhoneText = findViewById(R.id.adminProducts_userPhone);
        userAddressText = findViewById(R.id.adminProducts_userAddress);
        deliveryNameText = findViewById(R.id.adminProducts_deliveryName);
        deliveryPhoneText = findViewById(R.id.adminProducts_deliveryPhone);
        deliveryAddressText = findViewById(R.id.adminProducts_deliveryAddress);

        relativeLayout2 = findViewById(R.id.adminProducts_relativelayout2);
        relativeLayout4 = findViewById(R.id.adminProducts_relativelayout4);
        moreLessIcon = findViewById(R.id.adminProducts_moreLessIcon);


        relativeLayout2.setOnClickListener(v->{
            if(relativeLayout4.getVisibility() == View.VISIBLE){
                TransitionManager.beginDelayedTransition(relativeLayout2,new AutoTransition());
                relativeLayout4.setVisibility(View.GONE);
                moreLessIcon.setImageResource(R.drawable.ic_baseline_expand_more_24);

            }
            else if(relativeLayout4.getVisibility() == View.GONE){
                TransitionManager.beginDelayedTransition(relativeLayout2,new AutoTransition());
                relativeLayout4.setVisibility(View.VISIBLE);
                moreLessIcon.setImageResource(R.drawable.ic_baseline_expand_less_24);
            }
        });

        shipButton = findViewById(R.id.adminProducts_ShipButton);
        shipButton.setOnClickListener(v->{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Confirm Shipping?");
            alert.setMessage("Are you sure you want to Ship this Product?");
            alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    productShipped();
                    Toast.makeText(AdminNewOrderProductsActivity.this, "Order Shipped", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminNewOrderProductsActivity.this, AdminNewOrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.show();
        });


    }

    private void productShipped() {

        /*

        DatabaseReference orderProdRef = FirebaseDatabase.getInstance().getReference().child("OrderProducts");
        DatabaseReference fromRef = orderProdRef.child("OrderPlaced").child(orderID).child("Products");
        DatabaseReference toRef = orderProdRef.child("OrderShipped").child(orderID).child("Products");

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                toRef.setValue(snapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        DatabaseReference fromInfoRef = orderRef.child("OrderPlaced").child(orderID);
        DatabaseReference toInfoRef = orderRef.child("OrderShipped").child(orderID);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String saveCurrentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentTime = timeFormat.format(calendar.getTime());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("shippedBy",Prevalent.currentOnlineUser.getPhone());
        hashMap.put("shippedOn",saveCurrentDate+"+"+saveCurrentTime);
        hashMap.put(DBNodes.dbUsers_status,DBNodes.dbOrderShippedKey);
        fromInfoRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });



        fromInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                toInfoRef.setValue(snapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AdminNewOrderProductsActivity.this,
                                "success", Toast.LENGTH_SHORT).show();
                        if(!task.isSuccessful()){
                            Toast.makeText(AdminNewOrderProductsActivity.this,
                                    task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }


                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminNewOrderProductsActivity.this,
                        error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        /*
        fromRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

         */
        FirebaseDatabase.getInstance().getReference().child(DBNodes.dbOrderUser)
                .child(userPhone).child(orderID).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        fromInfoRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        HashMap<String, Object> statusMap = new HashMap<>();
        statusMap.put(DBNodes.dbUsers_status,DBNodes.dbOrderShippedKey);
        FirebaseDatabase.getInstance().getReference().child(DBNodes.dbUsers).child(userPhone)
                .updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child(DBNodes.dbOrderUser)
                .child(userPhone).child(orderID).updateChildren(statusMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();

        orderDetails(orderID);
        cartRef= FirebaseDatabase.getInstance().getReference().child("OrderProducts")
                .child(orderID).child("Products");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartRef,Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new
                FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull CartViewHolder holder, int position, @NonNull @NotNull Cart model) {
                        holder.productName.setText(model.getProductName());
                        holder.productQuantity.setText("Quantity: "+model.getQuantity());
                        int total = (Integer.parseInt(model.getQuantity())*Integer.parseInt(model.getPrice()));
                        holder.productPrice.setText(new StringBuilder().append("Total Price: ₹ ").append(String.valueOf(total)).append(" /-").toString());
                        if(model.getImage() != null || TextUtils.isEmpty(model.getImage())){
                            Picasso.get().load(model.getImage()).into(holder.productImage);
                        }
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
        productList.setAdapter(adapter);
        adapter.startListening();
    }

    private void orderDetails(String orderID) {


        FirebaseDatabase.getInstance().getReference().child(DBNodes.dbOrders)
                .child(DBNodes.dbOrderPlacedKey).child(orderID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdminOrders orderDet = snapshot.getValue(AdminOrders.class);
                        orderNoText.setText(new StringBuilder().append("Order ID:").append(orderID).toString());
                        orderStatusText.setText(new StringBuilder().append("Order Status: ").append(orderDet.getStatus()).toString());
                        orderDateText.setText(new StringBuilder().append("Order Date: ").append(orderDet.getDate()).toString());
                        totalAmountText.setText(new StringBuilder().append("Total Amount: ₹ ").append(orderDet.getTotalAmount()).toString());
                        userNameText.setText(new StringBuilder().append("User Name: ").append(orderDet.getUserName()).toString());
                        userPhoneText.setText(new StringBuilder().append("User Phone: ").append(orderDet.getUserPhone()).toString());
                        userAddressText.setText(new StringBuilder().append("User Address: ").append(orderDet.getAddress()).toString());
                        deliveryNameText.setText(new StringBuilder().append("Delivery User Name: ").append(orderDet.getDeliveryName()).toString());
                        deliveryPhoneText.setText(new StringBuilder().append("Delivery Phone: ").append(orderDet.getDeliveryPhone()).toString());
                        deliveryAddressText.setText(new StringBuilder().append("Delivery Address: ").append(orderDet.getDeliveryAddress()).toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}