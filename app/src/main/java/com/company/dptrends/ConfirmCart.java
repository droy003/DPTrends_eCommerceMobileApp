package com.company.dptrends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ConfirmCart extends AppCompatActivity {

    private Button confirmButton;
    private EditText deliveryNameEditText,deliveryPhoneEditText, deliveryAddressEditText;
    private CheckBox deliveryPaymentOption;
    private TextView userName,userPhone, totalAmountTxt;
    private String totalAmount="";
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_cart);

        deliveryNameEditText = findViewById(R.id.confirmCart_DeliveryName_EditText);
        deliveryPhoneEditText = findViewById(R.id.confirmCart_deliveryPhone_editText);
        deliveryAddressEditText = findViewById(R.id.confirmCart_deliveryAddress_EditText);

        userName = findViewById(R.id.confirmCart_userName_txt);
        userPhone = findViewById(R.id.confirmCart_userPhone_txt);
        totalAmountTxt = findViewById(R.id.confirmCart_totalAmount_txt);

        deliveryPaymentOption = findViewById(R.id.confirmCart_COD_checkbox);
        confirmButton = findViewById(R.id.cartConfirm_confirmButton);

        loadingBar = new ProgressDialog(this);

        totalAmount = getIntent().getStringExtra("totalPrice");
        totalAmountTxt.setText(new StringBuilder().append(getString(R.string.price_rs)).append(totalAmount).append(getString(R.string.price_end)).toString());
        userName.setText(Prevalent.currentOnlineUser.getName());
        userPhone.setText(Prevalent.currentOnlineUser.getPhone());
        deliveryPhoneEditText.setText(Prevalent.currentOnlineUser.getPhone());
        deliveryNameEditText.setText(Prevalent.currentOnlineUser.getName());
        if(Prevalent.currentOnlineUser.getAddress() != null){
            deliveryAddressEditText.setText(Prevalent.currentOnlineUser.getAddress());
        }
        confirmButton.setOnClickListener(v->{
            check();
        });

    }

    private void check() {
        if(!deliveryPaymentOption.isChecked())
        {
            Toast.makeText(this, "Please Choose payment option", Toast.LENGTH_SHORT).show();
        }
        else if(deliveryPhoneEditText.getText().toString().length() != 10){
            Toast.makeText(this, "Please Enter a valid 10 digit delivery phone no", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(deliveryNameEditText.getText().toString())){
            Toast.makeText(this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
        }
        else if(Prevalent.currentOnlineUser.getAddress() == null && TextUtils.isEmpty(deliveryAddressEditText.getText())){
            Toast.makeText(this, "Please Enter a delivery Address. Or Add Address in Settings.", Toast.LENGTH_LONG).show();
        }
        else{

            loadingBar.setTitle("Placing Order");
            loadingBar.setMessage("Please wait while processing");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final String saveCurrentDate,saveCurrentTime,saveCurrentDate1,saveCurrentTime1,orderID;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat dateFormat1= new SimpleDateFormat("yyyyMMdd");
        saveCurrentDate = dateFormat.format(calendar.getTime());
        saveCurrentDate1 = dateFormat1.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat timeFormat1 = new SimpleDateFormat("HHmmss");
        saveCurrentTime = timeFormat.format(calendar.getTime());
        saveCurrentTime1 = timeFormat1.format(calendar.getTime());

        orderID="ID"+saveCurrentDate1+saveCurrentTime1;

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child(DBNodes.dbOrders);
        final DatabaseReference orderUserRef = FirebaseDatabase.getInstance().getReference()
                .child(DBNodes.dbOrderUser);

        final HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("userName",Prevalent.currentOnlineUser.getName());
        hashMap.put("userPhone",Prevalent.currentOnlineUser.getPhone());
        if(Prevalent.currentOnlineUser.getAddress() == null){
            hashMap.put("address",deliveryAddressEditText.getText().toString());
        }
        else if(Prevalent.currentOnlineUser.getAddress() != null){
            hashMap.put("address",Prevalent.currentOnlineUser.getAddress());
        }
        hashMap.put("deliveryName",deliveryNameEditText.getText().toString());
        hashMap.put("deliveryPhone",deliveryPhoneEditText.getText().toString());
        hashMap.put("deliveryAddress",deliveryAddressEditText.getText().toString());
        hashMap.put("date",saveCurrentDate);
        hashMap.put("time",saveCurrentTime);
        hashMap.put("discount","");
        hashMap.put("status",DBNodes.dbOrderPlacedKey);
        hashMap.put("totalAmount",totalAmount);
        final long value = -1*Long.parseLong(saveCurrentDate1 + saveCurrentTime1);
        hashMap.put("revKey", value);


        final HashMap<String,Object> orderUserMap= new HashMap<>();
        orderUserMap.put("userName",Prevalent.currentOnlineUser.getName());
        orderUserMap.put("userPhone",Prevalent.currentOnlineUser.getPhone());
        if(Prevalent.currentOnlineUser.getAddress() == null){
            orderUserMap.put("address",deliveryAddressEditText.getText().toString());
        }
        else if(Prevalent.currentOnlineUser.getAddress() != null){
            orderUserMap.put("address",Prevalent.currentOnlineUser.getAddress());
        }
        orderUserMap.put("deliveryName",deliveryNameEditText.getText().toString());
        orderUserMap.put("deliveryPhone",deliveryPhoneEditText.getText().toString());
        orderUserMap.put("deliveryAddress",deliveryAddressEditText.getText().toString());
        orderUserMap.put("date",saveCurrentDate);
        orderUserMap.put("time",saveCurrentTime);
        orderUserMap.put("discount","");
        orderUserMap.put("status",DBNodes.dbOrderPlacedKey);
        orderUserMap.put("totalAmount",totalAmount);
        orderUserMap.put("revKey", value);


        orderRef.child(DBNodes.dbOrderPlacedKey)
                .child(orderID)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {

                        if(task.isSuccessful()){

                            FirebaseDatabase.getInstance().getReference()
                                    .child(DBNodes.dbCart)
                                    .child(Prevalent.currentOnlineUser.getPhone())
                                    .child(DBNodes.dbProductsKey)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(DBNodes.dbOrderProducts)
                                                    .child(orderID)
                                                    .child(DBNodes.dbProductsKey)
                                                    .setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                                        }
                                                    });

                                            HashMap<String, Object> quantityMap = new HashMap<>();

                                            for(DataSnapshot snapshot1 : snapshot.getChildren()){

                                                int quantCart = Integer.parseInt(snapshot1.child("quantity").getValue().toString());

                                                DatabaseReference prodQuantRef = FirebaseDatabase.getInstance().getReference().child(DBNodes.dbProducts)
                                                        .child(Objects.requireNonNull(snapshot1.child("productID").getValue()).toString());

                                                        prodQuantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                int quantProd = Integer.parseInt(snapshot2.child("quantity").getValue().toString());
                                                                int leftQuant = quantProd-quantCart;
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

                        }
                    }
                });

        orderUserRef.child(Prevalent.currentOnlineUser.getPhone())
                .child(orderID)
                .updateChildren(orderUserMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        HashMap<String, Object> statusMap = new HashMap<>();
                        statusMap.put(DBNodes.dbUsers_status,DBNodes.dbOrderPlacedKey);
                        if(Prevalent.currentOnlineUser.getAddress() == null){
                            statusMap.put("Address",deliveryAddressEditText.getText().toString());
                        }
                        FirebaseDatabase.getInstance().getReference()
                                .child(DBNodes.dbUsers)
                                .child(Prevalent.currentOnlineUser.getPhone())
                                .updateChildren(statusMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        FirebaseDatabase.getInstance().getReference()
                                                .child(DBNodes.dbCart)
                                                .child(Prevalent.currentOnlineUser.getPhone())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            loadingBar.dismiss();
                                                            //Prevalent.currentOnlineUser.setStatus(DBNodes.dbOrderPlacedKey);
                                                            Toast.makeText(ConfirmCart.this, "Order Confirmed Successfully", Toast.LENGTH_SHORT).show();
                                                            Intent i = new Intent(ConfirmCart.this,HomeActivity.class);
                                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    }
                                                });

                                    }
                                });



                    }
                });



    }
}