package com.company.dptrends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.dptrends.Admin.AdminCategoryActivity;
import com.company.dptrends.Model.Users;
import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import org.jetbrains.annotations.NotNull;

import java.security.GeneralSecurityException;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private TextView forgetMeText;
    private CheckBox checkBoxRememberMe;

    private EditText LoginPhoneText, LoginPasswordText;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private boolean paperChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialising
        LoginPasswordText = findViewById(R.id.login_password_input);
        LoginPhoneText = findViewById(R.id.login_phone_number_input);
        LoginButton = findViewById(R.id.loginpage_login_btn);
        loadingBar = new ProgressDialog(this);
        checkBoxRememberMe = findViewById(R.id.remember_me_checkbox);
        forgetMeText = findViewById(R.id.forgot_password_text);
        //initialising Paper library
        Paper.init(this);

        LoginButton.setOnClickListener(v->{
            LoginUser();
        });

        if(Paper.book().contains(Prevalent.UserPhoneKey) && Paper.book().contains(Prevalent.UserPasswordKey)){
            paperChecker = true;
            AllowAccessToAccount(
                    Paper.book().read(Prevalent.UserPhoneKey),
                    Paper.book().read(Prevalent.UserPasswordKey)
            );

        }
        forgetMeText.setOnClickListener(v->{
            Intent i = new Intent(LoginActivity.this,ForgotMeActivity.class);
            startActivity(i);
        });



    }

    private void LoginUser() {
        String password = LoginPasswordText.getText().toString();
        String phoneNumber = LoginPhoneText.getText().toString();
        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(LoginActivity.this, "Please enter your phone Number", Toast.LENGTH_LONG).show();
        }
        else if (phoneNumber.length() < 10){
            Toast.makeText(LoginActivity.this, "Please enter your valid 10 digit phone Number", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
        }
        else{

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while processing");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            

            paperChecker=false;
            String encryptPassword = passwordEncrypt(password);
            AllowAccessToAccount(phoneNumber, encryptPassword);
        }

    }

    public String passwordEncrypt(String password) {
        String encryptPassword = "";
        try {
            encryptPassword = AESCrypt.encrypt(Prevalent.EncryptPassword,password);
        }
        catch (GeneralSecurityException e){
            Toast.makeText(LoginActivity.this, "encrypt failure", Toast.LENGTH_SHORT).show();
        }
        return encryptPassword;
    }

    private void AllowAccessToAccount(String phoneNumber, String password) {


        final DatabaseReference AdminRef;
        final DatabaseReference userRef;
        userRef = FirebaseDatabase.getInstance().getReference().child(DBNodes.dbUsers);
        AdminRef = FirebaseDatabase.getInstance().getReference().child(DBNodes.dbAdmins);

        try{

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.hasChild(phoneNumber)){

                        if(checkBoxRememberMe.isChecked()) {
                            Paper.book().write(Prevalent.UserPhoneKey, phoneNumber);
                            Paper.book().write(Prevalent.UserPasswordKey,password);
                        }

                        Users usersData = snapshot.child(phoneNumber).getValue(Users.class);
                        Prevalent.currentOnlineUser = usersData;

                        if(usersData.getPhone().equals(phoneNumber)){
                            if(usersData.getPassword().equals(password)){

                                AdminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(phoneNumber)){
                                            if(!paperChecker){
                                                loadingBar.dismiss();
                                            }
                                            Toast.makeText(LoginActivity.this, "Welcome Admin you are logged in successfully...", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(LoginActivity.this, MainActivityBypass.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);

                                        }

                                        else{

                                            if(!paperChecker){
                                                loadingBar.dismiss();
                                            }
                                            //Toast.makeText(LoginActivity.this, "logged in successfully...", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    else{
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "This Phone Number does not exist.Please Create New Account.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            loadingBar.dismiss();
            Toast.makeText(LoginActivity.this, "Error. Problem in Internet Connection", Toast.LENGTH_SHORT).show();
            throw e;
        }



    }


}