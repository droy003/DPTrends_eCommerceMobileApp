package com.company.dptrends.ui.Cart;

import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentStateManagerControl;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.company.dptrends.ConfirmCart;
import com.company.dptrends.Model.Cart;
import com.company.dptrends.Model.Users;
import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.CartViewHolder;
import com.company.dptrends.ui.ProductDetails.ProductDetailsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class CartFragment extends Fragment {



    public static CartFragment newInstance() {
        return new CartFragment();
    }

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextButton;
    private TextView cartTotalPrice,textmsg1;
    private int cartAllProductsPrice = 0;
    private int oneProductTotalPrice = 0;
    FloatingActionButton floatingActionButton;
    private String  orderStatus;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        nextButton = view.findViewById(R.id.cart_next_btn);
        cartTotalPrice = view.findViewById(R.id.cart_price_txt);
        recyclerView = view.findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        textmsg1 = view.findViewById(R.id.msg1);
        //floatingActionButton=view.findViewById(R.id.fab);

        orderStatus();

        return view;
    }

    private void orderStatus() {
        FirebaseDatabase.getInstance().getReference().child(DBNodes.dbUsers).child(Prevalent.currentOnlineUser.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderStatus = snapshot.child("status").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        //checkOderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference()
                .child(DBNodes.dbCart).child(Prevalent.currentOnlineUser.getPhone())
                .child(DBNodes.dbProductsKey);
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef,Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull CartViewHolder holder, int position, @NonNull @NotNull Cart model) {
                holder.productName.setText(model.getProductName());
                holder.productQuantity.setText(new StringBuilder().append("Quantity: ").append(model.getQuantity()).toString());
                if(model.getImage() != null || !TextUtils.isEmpty(model.getImage())){
                    Picasso.get().load(model.getImage()).into(holder.productImage);
                }
                oneProductTotalPrice = ((Integer.parseInt(model.getPrice()))*(Integer.parseInt(model.getQuantity())));
                holder.productPrice.setText(new StringBuilder().append(getString(R.string.price_rs)).append(oneProductTotalPrice).append(getString(R.string.price_end)).toString());
                cartAllProductsPrice = cartAllProductsPrice + oneProductTotalPrice;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]
                        {
                            "Edit", "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Cart Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){


                                    Bundle args = new Bundle();
                                    args.putString(Prevalent.ProductIDKey,model.getProductID());
                                    Navigation.findNavController(getView()).navigate(R.id.nav_product_details,args);

                                }
                                else if (i==1){
                                    cartListRef
                                            .child(model.getProductID())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    Toast.makeText(getActivity(), "Item Removed", Toast.LENGTH_SHORT).show();
                                                    Navigation.findNavController(getView()).navigate(R.id.nav_cart);

                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
                cartTotalPrice.setText(new StringBuilder().append("Amount: ")
                                                        .append(getString(R.string.rs))
                                                        .append(" ")
                                                        .append(Integer.toString(cartAllProductsPrice)));
            }

            @NonNull
            @NotNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder cartViewHolder = new CartViewHolder(v);
                return cartViewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


        nextButton.setOnClickListener(v->{

            if(orderStatus.equals(DBNodes.dbOrderPlacedKey) || orderStatus.equals(DBNodes.dbOrderShippedKey))
            {
                Toast.makeText(getActivity(), "You can make only one order at a time. Try Again after Order delivery.", Toast.LENGTH_LONG).show();
            }
            else if(cartAllProductsPrice == 0){
                Toast.makeText(getActivity(), "Add items to cart", Toast.LENGTH_LONG).show();
            }
            else if(orderStatus.equals(DBNodes.dbOrderDeliveredKey) || orderStatus.equals(DBNodes.dbNewOrderKey)){
                Intent i = new Intent(getActivity(), ConfirmCart.class);
                i.putExtra("totalPrice",Integer.toString(cartAllProductsPrice));

                startActivity(i);
            }

        });
    }

    /*
    private void checkOderState(){

        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.Phone);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String status = snapshot.child("status").getValue().toString();
                    String user = snapshot.child("productName").getValue().toString();

                    if(status.equals("shipped")){
                        cartTotalPrice.setText("Dear "+user+" order is shipped successfully.");
                        recyclerView.setVisibility(View.GONE);
                        textmsg1.setVisibility(View.VISIBLE);
                        textmsg1.setText("Congratulations your order has been placed. Soon you will you will receive the order in your doorstep.");
                        nextButton.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();
                    }
                    else if(status.equals("not shipped")){
                        cartTotalPrice.setText("Status: Not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        textmsg1.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Try Later", Toast.LENGTH_SHORT).show();
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