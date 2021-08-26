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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.company.dptrends.Model.Categories;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.CategoriesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminCategoryActivity extends AppCompatActivity {

    private RecyclerView categoriesList;
    private RecyclerView.LayoutManager layoutManager;
    private Button addNewCategoriesBtn,maintainOrdersButton;
    private EditText categoriesDialogBoxEditText;
    private DatabaseReference catRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        initialize();

        maintainOrdersButton.setOnClickListener(v->{
            Intent i = new Intent(AdminCategoryActivity.this, AdminNewOrderActivity.class);
            startActivity(i);
        });

        addNewCategoriesBtn.setOnClickListener(v->{
            View viewDialog = getLayoutInflater().inflate(R.layout.admin_add_category_dialog_layout,null);
            AlertDialog.Builder alertBox = new AlertDialog.Builder(AdminCategoryActivity.this);
            alertBox.setTitle("Enter Category Name");
            //alertBox.setMessage("Hello");
            alertBox.setView(viewDialog);
            alertBox.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            categoriesDialogBoxEditText = viewDialog.findViewById(R.id.admin_Categories_dialog_edittext);
                            if(TextUtils.isEmpty(categoriesDialogBoxEditText.getText().toString())){
                                Toast.makeText(AdminCategoryActivity.this, "Enter A Category", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                categoriesAdd(categoriesDialogBoxEditText.getText().toString());
                            }
                        }
                    });
            alertBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });


            alertBox.show();
        });

    }

    private void categoriesAdd(String categoryName) {
        HashMap<String,Object> hashMap = new HashMap<>();

        catRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                hashMap.put("categories", categoryName);
                hashMap.put("personAdded",Prevalent.currentOnlineUser.getPhone());
                catRef.child(String.valueOf(snapshot.getChildrenCount() + 1))
                        .updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AdminCategoryActivity.this, "Category Added", Toast.LENGTH_SHORT).show();
                                onStart();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminCategoryActivity.this, "Error 0", Toast.LENGTH_SHORT).show();
                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



    private void initialize() {
        categoriesList = findViewById(R.id.admin_Categories_list);
        layoutManager = new LinearLayoutManager(this);
        categoriesList.setLayoutManager(layoutManager);
        addNewCategoriesBtn = findViewById(R.id.admin_Categories_addButton);
        catRef = FirebaseDatabase.getInstance().getReference().child("Categories");
        maintainOrdersButton= findViewById(R.id.admin_Categories_OrderButton);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Categories> options = new FirebaseRecyclerOptions.Builder<Categories>()
                .setQuery(catRef,Categories.class)
                .build();

        FirebaseRecyclerAdapter<Categories, CategoriesViewHolder> adapter = new FirebaseRecyclerAdapter<Categories, CategoriesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull Categories model) {
                holder.categoryName.setText(model.getCategories());
                holder.itemView.setOnClickListener(v->{

                        Intent i = new Intent(AdminCategoryActivity.this, AdminCategoriesProductsActivity.class);
                        i.putExtra("categories",model.getCategories());
                        startActivity(i);
                });
            }

            @NonNull
            @Override
            public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_name_layout,parent,false);
                CategoriesViewHolder categoriesViewHolder = new CategoriesViewHolder(view);
                return categoriesViewHolder;
            }
        };
        categoriesList.setAdapter(adapter);
        adapter.startListening();


    }
}