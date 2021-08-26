package com.company.dptrends.ui.Categories;

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

import com.company.dptrends.Admin.AdminCategoriesProductsActivity;
import com.company.dptrends.Admin.AdminCategoryActivity;
import com.company.dptrends.Model.Categories;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.CategoriesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CategoriesFragment extends Fragment {



    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }


    private DatabaseReference catRef;
    private RecyclerView categoriesList;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_categories, container, false);

        categoriesList = view.findViewById(R.id.CategoriesFragment_List);
        layoutManager = new LinearLayoutManager(getActivity());
        categoriesList.setLayoutManager(layoutManager);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        catRef = FirebaseDatabase.getInstance().getReference().child("Categories");

        FirebaseRecyclerOptions<Categories> options = new FirebaseRecyclerOptions.Builder<Categories>()
                .setQuery(catRef,Categories.class)
                .build();

        FirebaseRecyclerAdapter<Categories, CategoriesViewHolder> adapter = new FirebaseRecyclerAdapter<Categories, CategoriesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull Categories model) {
                holder.categoryName.setText(model.getCategories());
                holder.itemView.setOnClickListener(v->{

                    Bundle args = new Bundle();
                    args.putString("categories",model.getCategories());
                    Navigation.findNavController(getView()).navigate(R.id.nav_categories_products,args);

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