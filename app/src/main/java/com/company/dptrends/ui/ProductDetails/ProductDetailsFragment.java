package com.company.dptrends.ui.ProductDetails;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.company.dptrends.HomeActivity;
import com.company.dptrends.Model.Products;
import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
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

public class ProductDetailsFragment extends Fragment {



    public static ProductDetailsFragment newInstance() {
        return new ProductDetailsFragment();
    }

    private ImageView productImage;
    private Button numberButton;
    private TextView productName, productDescription,productPrice;
    private Button addToCart;
    private String productID="",productNumber;
    private Spinner dropdown;
    private String priceProduct="",productImageString="";
    private RelativeLayout relativeLayout1,relativeLayout2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_product_details, container, false);
        productName = view.findViewById(R.id.product_name_details);
        productDescription = view.findViewById(R.id.product_description_details);
        productPrice = view.findViewById(R.id.product_price_details);
        //numberButton = view.findViewById(R.id.product_increment_btn);
        productImage = view.findViewById(R.id.product_image_details);
        addToCart = view.findViewById(R.id.product_addtoCart_Button);
        dropdown = view.findViewById(R.id.product_increment_spinner);
        relativeLayout1 = view.findViewById(R.id.product_details_relativeLayout);
        relativeLayout2 = view.findViewById(R.id.product_details_relativeLayout2);

        productID = getArguments().getString(Prevalent.ProductIDKey);

        getProductDetails(productID);
        //getActivity().setTitle("Product Details");


        addToCart.setOnClickListener(v->{
            addingToCartList();
        });
        return view;

    }

    private void addingToCartList() {

        String saveCurrentDate,saveCurrentTime;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        saveCurrentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = timeFormat.format(calendar.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference()
                .child(DBNodes.dbCart);
        final HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("productID",productID);
        hashMap.put("productName",productName.getText().toString());
        hashMap.put("price",priceProduct);
        hashMap.put("date",saveCurrentDate);
        hashMap.put("image",productImageString);
        hashMap.put("time",saveCurrentTime);
        hashMap.put("quantity",productNumber);
        hashMap.put("discount","");

        cartListRef.child(Prevalent.currentOnlineUser.getPhone()).child(DBNodes.dbProducts)
                .child(productID).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(getActivity(), "Product Added/Modified To Cart", Toast.LENGTH_SHORT).show();

                            /*
                            cartListRef.child("AdminView").child(Prevalent.Phone).child("Products")
                                    .child(productID).updateChildren(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(getActivity(),HomeActivity.class);
                                                startActivity(i);
                                            }
                                        }
                                    });

                             */
                        }
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        //checkOderStateProductDetails();
    }

    private void getProductDetails(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference()
                .child(DBNodes.dbProducts);

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products products = snapshot.getValue(Products.class);
                    productName.setText(products.getProductName());
                    productDescription.setText(products.getDescription());
                    priceProduct = products.getPrice();
                    productPrice.setText(new StringBuilder().append(getString(R.string.price_rs)).append(priceProduct).append(getString(R.string.price_end)).toString());
                    productImageString = products.getImage();
                    Picasso.get().load(products.getImage()).into(productImage);
                    if (products.getQuantity() == null) {
                        createQuantityDropdown(0);
                    }
                    else{
                        createQuantityDropdown(Integer.parseInt(products.getQuantity()));
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }

        });
    }

    private void createQuantityDropdown(int numberItems) {
        String[] items = Prevalent.getStringArray(numberItems);
        if(items.length == 0){
            addToCart.setVisibility(View.GONE);
            relativeLayout1.setVisibility(View.GONE);
            relativeLayout2.setVisibility(View.VISIBLE);
        }
        else {
            addToCart.setVisibility(View.VISIBLE);
            relativeLayout1.setVisibility(View.VISIBLE);
            relativeLayout2.setVisibility(View.GONE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item_2, items);
            dropdown.setAdapter(adapter);
            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    productNumber = adapterView.getItemAtPosition(i).toString();
                    //Toast.makeText(getActivity(),adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    /*
    private void checkOderStateProductDetails(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child(DBNodes.dbOrders)
                .child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String state = snapshot.child("status").getValue().toString();

                    if(state.equals("shipped")){
                        status = "Order Shipped";

                    }
                    else if(state.equals("not shipped")){
                        status = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

     */


}