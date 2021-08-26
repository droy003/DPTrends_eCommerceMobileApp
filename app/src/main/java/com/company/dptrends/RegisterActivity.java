package com.company.dptrends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton, sendOTPButton,verifyOTPButton;
    private EditText InputName, InputPhoneNumber, InputPassword,InputOTP;
    private ProgressDialog loadingBar;

    private FirebaseAuth mobileAuth;
    private String verificationID;
    private RelativeLayout relativeLayoutOTP,relativeLayoutPassword,relativeLayoutVerify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        CreateAccountButton = findViewById(R.id.register_btn);
        InputName = findViewById(R.id.register_username);
        InputPassword = findViewById(R.id.register_password_input);
        InputPhoneNumber = findViewById(R.id.register_phone_number_input);
        relativeLayoutOTP = findViewById(R.id.register_relativeLayout_otp);
        relativeLayoutPassword = findViewById(R.id.register_relativeLayout_password);
        relativeLayoutVerify = findViewById(R.id.register_relativeLayout_otp_enter);
        InputOTP = findViewById(R.id.register_otp_input);
        sendOTPButton = findViewById(R.id.register_sendOtp_btn);
        verifyOTPButton =findViewById(R.id.register_VerifyOtp_btn);
        mobileAuth = FirebaseAuth.getInstance();

        sendOTPButton.setOnClickListener(v->{
            if(TextUtils.isEmpty(InputPhoneNumber.getText().toString())){
                Toast.makeText(RegisterActivity.this, "Enter a Phone Number", Toast.LENGTH_SHORT).show();
            }
            else if(InputPhoneNumber.getText().toString().length() != 10){
                Toast.makeText(RegisterActivity.this, "Enter a valid 10 digit phone number", Toast.LENGTH_SHORT).show();
            }
            else{
                FirebaseDatabase.getInstance().getReference().child(DBNodes.dbUsers).child(InputPhoneNumber.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Toast.makeText(RegisterActivity.this, "Phone Number already exists", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String phone = "+91"+InputPhoneNumber.getText().toString();
                            relativeLayoutVerify.setVisibility(View.VISIBLE);
                            sendVerificationCode(phone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        verifyOTPButton.setOnClickListener(v->{
            if(TextUtils.isEmpty(InputOTP.getText().toString())){
                Toast.makeText(RegisterActivity.this, "Please enter an OTP" , Toast.LENGTH_SHORT).show();
            }
            else{
                verifyCode(InputOTP.getText().toString());
            }
        });


        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(v -> {
            createAccount();
        });

    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mobileAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(RegisterActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                            relativeLayoutOTP.setVisibility(View.GONE);
                            relativeLayoutPassword.setVisibility(View.VISIBLE);
                            InputPhoneNumber.setText(InputPhoneNumber.getText().toString());
                            InputPhoneNumber.setTextColor(ContextCompat.getColor(RegisterActivity.this,R.color.dark_grey));
                            InputPhoneNumber.setEnabled(false);

                        }
                        else{
                            Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String phone) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mobileAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationID = s;

        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            final String code = phoneAuthCredential.getSmsCode();


            if (code != null) {

                InputOTP.setText(code);
                verifyCode(code);

            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };



    private void createAccount() {

        String name = InputName.getText().toString();
        String password = InputPassword.getText().toString();
        String phoneNumber = InputPhoneNumber.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this, "Please enter your name", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
        }
        else if(password.length() < 4 || password.length() > 10){
            Toast.makeText(RegisterActivity.this, "Please enter a password of length 4-10", Toast.LENGTH_LONG).show();
        }
        else{

            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait while processing");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            String encryptPassword = passwordEncrypt(password);
            validateCredentials(name, phoneNumber, encryptPassword);

        }
    }

    private void validateCredentials(String name, String phoneNumber, String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference().child(DBNodes.dbUsers);

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.hasChild(phoneNumber))){

                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put(DBNodes.dbUsers_Phone, phoneNumber);
                    userDataMap.put(DBNodes.dbUsers_Password, password);
                    userDataMap.put(DBNodes.dbUsers_Name, name);
                    userDataMap.put(DBNodes.dbUsers_status,DBNodes.dbNewOrderKey);


                    RootRef.child(phoneNumber).updateChildren(userDataMap).addOnCompleteListener(task -> {

                        if(task.isSuccessful()){
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this,"Account Created Successfully",Toast.LENGTH_LONG).show();
                            Intent intent =new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this,"Network Error. Please Try Again",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Phone Number already exists",Toast.LENGTH_LONG).show();

                    Toast.makeText(RegisterActivity.this,"Please try with another Number or try Forgot Password",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public String passwordEncrypt(String password) {
        String encryptPassword = "";
        try {
            encryptPassword = AESCrypt.encrypt(Prevalent.EncryptPassword,password);
        }
        catch (GeneralSecurityException e){
            Toast.makeText(RegisterActivity.this, "encrypt failure", Toast.LENGTH_SHORT).show();
        }
        return encryptPassword;
    }
}
