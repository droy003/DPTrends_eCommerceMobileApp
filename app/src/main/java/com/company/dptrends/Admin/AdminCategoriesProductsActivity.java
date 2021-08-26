package com.company.dptrends.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.company.dptrends.Model.Products;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminCategoriesProductsActivity extends AppCompatActivity {

    private String categories;
    private RecyclerView productList;
    private RecyclerView.LayoutManager layoutManager;
    private Button addNewProduct;
    private DatabaseReference productRef;
    private TextView headingSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_categories_products);
        final SwipeRefreshLayout pullToRefresh =findViewById(R.id.pullToRefresh);
        categories = getIntent().getExtras().get("categories").toString();
        headingSlogan=findViewById(R.id.slogan_category_adminProducts);
        headingSlogan.setText(categories);
        productList = findViewById(R.id.admin_CategoriesProducts_list);
        layoutManager = new LinearLayoutManager(this);
        productList.setHasFixedSize(true);
        productList.setLayoutManager(layoutManager);
        addNewProduct = findViewById(R.id.admin_Categories_products_addButton);
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                new Intent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                onStart();
            }
        });
        addNewProduct.setOnClickListener(v->{
            Intent i = new Intent(AdminCategoriesProductsActivity.this, AdminAddNewProductActivity.class);
            i.putExtra("Categories",categories);
            i.putExtra("act","add");
            startActivity(i);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productRef.orderByChild("category").equalTo(categories),Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                holder.textProductName.setText(model.getProductName());
                holder.textProductPrice.setText("Price: ₹ "+model.getPrice());
                holder.textProductDescription.setText(model.getDescription());
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(v->{
                    String pid= getRef(position).getKey();
                    CharSequence[] dialogOptions = new CharSequence[]{
                            "Edit",
                            "Delete"
                    };
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AdminCategoriesProductsActivity.this);
                    dialog.setTitle("Options");
                    dialog.setItems(dialogOptions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                //Edit Product
                                Intent intent = new Intent(AdminCategoriesProductsActivity.this,AdminAddNewProductActivity.class);
                                intent.putExtra("Categories",categories);
                                intent.putExtra("act","edit");
                                intent.putExtra("productID",pid);
                                startActivity(intent);
                            }
                            else if (i==1){
                                //Delete Product
                                productRef.child(pid).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(AdminCategoriesProductsActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    dialog.show();
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder productViewHolder = new ProductViewHolder(view);
                return productViewHolder;
            }
        };
        productList.setAdapter(adapter);
        adapter.startListening();


    }
}