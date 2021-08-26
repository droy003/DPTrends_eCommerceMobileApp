package com.company.dptrends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.company.dptrends.Model.Users;
import com.company.dptrends.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, registerButton;
    private final String parentDBName = "Users";
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.main_login_btn);
        registerButton = findViewById(R.id.main_join_now_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        if(Paper.book().contains(Prevalent.UserPhoneKey) && Paper.book().contains(Prevalent.UserPasswordKey)){

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
        }

    }

    private void AllowAccess(final String userPhoneKey, final String userPasswordKey) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(parentDBName).child(userPhoneKey).exists()){

                    Users usersData = snapshot.child(parentDBName).child(userPhoneKey).getValue(Users.class);

                    if(usersData.getPhone().equals(userPhoneKey)){
                        if(usersData.getPassword().equals(userPasswordKey)){
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "You are already logged in", Toast.LENGTH_SHORT).show();
                            //Intent i = new Intent(MainActivity.this, HomeActivity.class);
                            //startActivity(i);
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "This Phone Number does not exist.Please Create New Account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}