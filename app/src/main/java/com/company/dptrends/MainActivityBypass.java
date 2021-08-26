package com.company.dptrends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import com.company.dptrends.Admin.AdminCategoryActivity;

public class MainActivityBypass extends AppCompatActivity {

    private Button HomeButton,AdminButton;
    private boolean backPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bypass);
        HomeButton = findViewById(R.id.main_button);
        AdminButton = findViewById(R.id.admin_button);
        backPressedOnce = false;

        HomeButton.setOnClickListener(v->{

            Intent i = new Intent(MainActivityBypass.this,HomeActivity.class);
            startActivity(i);

        });

        AdminButton.setOnClickListener(v->{
            Intent i = new Intent(MainActivityBypass.this,
                    AdminCategoryActivity.class);
            startActivity(i);
        });

    }
    public void onBackPressed() {

        if(backPressedOnce){
            super.onBackPressed();
            return;
        }

        this.backPressedOnce=true;
        Toast.makeText(MainActivityBypass.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedOnce=false;
            }
        },2000);
    }
}