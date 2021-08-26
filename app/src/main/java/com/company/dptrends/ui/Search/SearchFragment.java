package com.company.dptrends.ui.Search;

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
import android.widget.Button;
import android.widget.EditText;

import com.company.dptrends.Model.Products;
import com.company.dptrends.R;
import com.company.dptrends.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class SearchFragment extends Fragment {


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    private EditText searchText;
    private Button searchButton;
    DatabaseReference ref;
    String searchTextString="";
    RecyclerView searchList;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        searchButton=view.findViewById(R.id.searchFragment_searchButton);
        searchText=view.findViewById(R.id.searchFragment_searchText);
        searchList = view.findViewById(R.id.searchFragment_list);
        searchList.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchButton.setOnClickListener(v->{
            searchTextString=searchText.getText().toString();
            onStart();
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        ref = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ref.orderByChild("productName").startAt(searchTextString).endAt(searchTextString+"\uf8ff"),Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int position, @NonNull @NotNull Products model) {
                holder.textProductName.setText(model.getProductName());
                holder.textProductPrice.setText(new StringBuilder().append(getString(R.string.price_rs)).append(model.getPrice()).append(getString(R.string.price_end)).toString());
                holder.textProductDescription.setText(model.getDescription());
                Picasso.get().load(model.getImage()).into(holder.imageView);
                holder.imageView.setOnClickListener(v->{

                    Bundle args = new Bundle();
                    args.putString("pid",model.getProductID());
                    Navigation.findNavController(getView()).navigate(R.id.nav_product_details,args);

                });
            }

            @NonNull
            @NotNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder productViewHolder = new ProductViewHolder(view);
                return productViewHolder;
            }
        };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}