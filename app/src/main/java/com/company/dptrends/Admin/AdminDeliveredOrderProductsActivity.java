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

public class AdminDeliveredOrderProductsActivity extends AppCompatActivity {

    private RecyclerView productList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartRef;

    private Button deleteButton;
    private String orderID;

    private ImageView moreLessIcon;
    private RelativeLayout relativeLayout2,relativeLayout4;
    private TextView orderNoText,orderStatusText,orderDateText,totalAmountText;
    private TextView userNameText,userPhoneText,userAddressText;
    private TextView deliveryNameText, deliveryPhoneText,deliveryAddressText;
    private TextView shippedBy,shippedDate,deliveredBy,deliveredDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delivered_order_products);

        orderID = getIntent().getStringExtra("orderID");
        productList = findViewById(R.id.adminDeliveredProducts_list);
        productList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productList.setLayoutManager(layoutManager);


        orderNoText = findViewById(R.id.adminDeliveredProducts_orderNo);
        orderStatusText = findViewById(R.id.adminDeliveredProducts_orderStatus);
        orderDateText = findViewById(R.id.adminDeliveredProducts_orderDate);
        totalAmountText = findViewById(R.id.adminDeliveredProducts_totalAmount);
        userNameText = findViewById(R.id.adminDeliveredProducts_userName);
        userPhoneText = findViewById(R.id.adminDeliveredProducts_userPhone);
        userAddressText = findViewById(R.id.adminDeliveredProducts_userAddress);
        deliveryNameText = findViewById(R.id.adminDeliveredProducts_deliveryName);
        deliveryPhoneText = findViewById(R.id.adminDeliveredProducts_deliveryPhone);
        deliveryAddressText = findViewById(R.id.adminDeliveredProducts_deliveryAddress);
        shippedBy = findViewById(R.id.adminDeliveredProducts_shippedBy);
        shippedDate = findViewById(R.id.adminDeliveredProducts_shippedDate);
        deliveredBy = findViewById(R.id.adminDeliveredProducts_deliveredBy);
        deliveredDate = findViewById(R.id.adminDeliveredProducts_deliveredDate);

        relativeLayout2 = findViewById(R.id.adminDeliveredProducts_relativelayout2);
        relativeLayout4 = findViewById(R.id.adminDeliveredProducts_relativelayout4);
        moreLessIcon = findViewById(R.id.adminDeliveredProducts_moreLessIcon);


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


        deleteButton = findViewById(R.id.adminDeliveredProducts_deleteButton);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(v-> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Confirm Delete?");
            alert.setMessage("Are you sure You want to delete this Order?");
            alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //deleteOrder();
                    Toast.makeText(AdminDeliveredOrderProductsActivity.this, "You cannot delete any delivered order.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminDeliveredOrderProductsActivity.this, AdminDeliveredOrderActivity.class);
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

    private void deleteOrder() {

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("OrderDelivered").child(orderID);
        DatabaseReference orderProductsRef = FirebaseDatabase.getInstance().getReference().child("OrderProducts").child("OrderDelivered").child(orderID);
        orderRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                orderProductsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AdminDeliveredOrderProductsActivity.this, "Order Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

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
                        holder.productPrice.setText(new StringBuilder().append(getString(R.string.price_rs)).append(String.valueOf(total)).append(getString(R.string.price_end)).toString());
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
                .child(DBNodes.dbOrderDeliveredKey).child(orderID)
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
                        if(orderDet.getShippedBy()!=null){
                            shippedBy.setText(new StringBuilder().append("Shipped By: ").append(orderDet.getShippedBy()).toString());
                        }
                        if(orderDet.getShippedOn()!=null){
                            shippedDate.setText(new StringBuilder().append("Shipped On: ").append(orderDet.getShippedOn()).toString());
                        }
                        if(orderDet.getDeliveredBy()!=null){
                            deliveredBy.setText(new StringBuilder().append("Delivered By: ").append(orderDet.getDeliveredBy()).toString());
                        }
                        if(orderDet.getDeliveredOn()!=null){
                            deliveredDate.setText(new StringBuilder().append("Delivered On: ").append(orderDet.getDeliveredOn()).toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}