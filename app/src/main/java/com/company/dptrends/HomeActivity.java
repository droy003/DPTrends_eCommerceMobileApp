package com.company.dptrends;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.company.dptrends.Model.Products;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.ViewHolder.ProductViewHolder;
import com.company.dptrends.ui.Cart.CartFragment;
import com.company.dptrends.ui.Home.HomeFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.dptrends.databinding.ActivityHomeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private Toolbar toolbar;
    private boolean backPressedOnce = false;
    private boolean onHome = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarHome.toolbar);
        backPressedOnce = false;
        onHome = true;

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cart, R.id.nav_categories,
                R.id.nav_logout,R.id.nav_settings,R.id.nav_orders,
                R.id.nav_product_details,R.id.nav_search,R.id.nav_categories_products,
                R.id.nav_order_products,R.id.nav_contactUs)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.nav_cart){
                    binding.appBarHome.fab.hide();
                    onHome = false;
                }
                else if(destination.getId() == R.id.nav_orders){
                    binding.appBarHome.fab.hide();
                    onHome = false;
                }
                else if(destination.getId() == R.id.nav_order_products){
                    binding.appBarHome.fab.hide();
                    onHome = false;
                }
                else if(destination.getId() == R.id.nav_categories){
                    binding.appBarHome.fab.show();
                    onHome = false;
                }
                else if(destination.getId() == R.id.nav_search){
                    binding.appBarHome.fab.show();
                    onHome = false;
                }
                else if(destination.getId() == R.id.nav_settings){
                    binding.appBarHome.fab.hide();
                    onHome = false;
                }
                else if(destination.getId() == R.id.nav_logout){
                    binding.appBarHome.fab.hide();
                    onHome = false;
                }
                else if(destination.getId() == R.id.nav_home){
                    binding.appBarHome.fab.show();
                    onHome = true;
                }
                else if(destination.getId() == R.id.nav_product_details){
                    binding.appBarHome.fab.show();
                    onHome = false;
                }
                else if(destination.getId() == R.id.nav_categories_products){
                    binding.appBarHome.fab.show();
                    onHome = false;
                }
                else if(destination.getId() == R.id.nav_contactUs){
                    binding.appBarHome.fab.hide();
                    onHome = false;
                }

            }
        });


        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageUser = headerView.findViewById(R.id.user_profile_image);
        //String dsf=Prevalent.currentOnlineUser.getImage();
        //String dsfd=Prevalent.currentOnlineUser.getName();
        if(Prevalent.currentOnlineUser.getName() != null){
            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        }
        if(Prevalent.currentOnlineUser.getImage() != null){
            if(!Prevalent.currentOnlineUser.getImage().equals(""))
            {
                Picasso.get().load(Prevalent.currentOnlineUser.getImage()).into(profileImageUser);
            }
        }


        binding.appBarHome.fab.setOnClickListener(v->{

                navController.navigate(R.id.nav_cart);

        });

        /*
        //needs work joining previous
        userNameTextView.setText(Prevalent.currentOnlineUsers.getName());
        //vid 19 30mi
        Picasso.get().load(Prevalent.currentOnlineUsers.getImage()).placeholder(R.drawable.profile).into(profileImageUser);

        */

    }
    /*

    */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {


        if (onHome){

            if (!backPressedOnce) {
                this.backPressedOnce=true;
                Toast.makeText(HomeActivity.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backPressedOnce=false;
                    }
                },2000);

            }
            else{
                super.onBackPressed();
                return;
            }
        }
        else{
            super.onBackPressed();
        }
    }
}