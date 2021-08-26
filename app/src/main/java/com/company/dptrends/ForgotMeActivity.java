package com.company.dptrends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.HashMap;

public class ForgotMeActivity extends AppCompatActivity {

    private EditText InputNumber, InputPassword1, InputPassword2;
    private Button verifyButton, changePasswordButton;
    private RelativeLayout relativeLayout1, relativeLayout2;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_me);
        InputNumber = findViewById(R.id.forgotMe_phoneNumber_input);
        InputPassword1 = findViewById(R.id.forgotMe_password_input);
        InputPassword2 = findViewById(R.id.forgotMe_password_input2);
        verifyButton = findViewById(R.id.forgotMe_verify_btn);
        changePasswordButton = findViewById(R.id.forgotMe_changePassword_btn);
        relativeLayout1 = findViewById(R.id.forgotMe_relativeLayout1);
        relativeLayout2 = findViewById(R.id.forgotMe_relativeLayout2);
        loadingBar = new ProgressDialog(ForgotMeActivity.this);


        verifyButton.setOnClickListener(v->{

            String phone = InputNumber.getText().toString();
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(ForgotMeActivity.this, "Enter a phone Number", Toast.LENGTH_SHORT).show();
            }
            else if (phone.length() != 10){
                Toast.makeText(ForgotMeActivity.this, "Enter a Valid 10 digit Pone Number", Toast.LENGTH_SHORT).show();
            }
            else{
                loadingBar.setTitle("Verifying Number");
                loadingBar.setMessage("Please wait while processing");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                FirebaseDatabase.getInstance().getReference().child(DBNodes.dbUsers).child(phone)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    loadingBar.dismiss();
                                    relativeLayout1.setVisibility(View.GONE);
                                    relativeLayout2.setVisibility(View.VISIBLE);
                                }
                                else{
                                    loadingBar.dismiss();
                                    Toast.makeText(ForgotMeActivity.this, "Phone Number Does Not Exists. Try Registering the Number", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                loadingBar.dismiss();
                                Toast.makeText(ForgotMeActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });

            }



        });

        changePasswordButton.setOnClickListener(v->{
            String password1 = InputPassword1.getText().toString();
            String password2 = InputPassword2.getText().toString();
            if(password1.length() < 4 || password1.length() > 10){
                Toast.makeText(ForgotMeActivity.this, "Enter a password of length 4-10", Toast.LENGTH_SHORT).show();
            }
            else if(!password1.equals(password2)){
                Toast.makeText(ForgotMeActivity.this, "Confirm Password Doesnot Match. Enter Password Again.", Toast.LENGTH_SHORT).show();
            }
            else{
                loadingBar.setTitle("Verifying Number");
                loadingBar.setMessage("Please wait while processing");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                String encryptPassword = passwordEncrypt(password1);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(DBNodes.dbUsers_Password,encryptPassword);
                FirebaseDatabase.getInstance().getReference().child(DBNodes.dbUsers).child(InputNumber.getText().toString())
                        .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotMeActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ForgotMeActivity.this,LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(ForgotMeActivity.this, "Password Could not be change. Try again later.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }
    public String passwordEncrypt(String password) {
        String encryptPassword = "";
        try {
            encryptPassword = AESCrypt.encrypt(Prevalent.EncryptPassword,password);
        }
        catch (GeneralSecurityException e){
            Toast.makeText(ForgotMeActivity.this, "encrypt failure", Toast.LENGTH_SHORT).show();
        }
        return encryptPassword;
    }
}