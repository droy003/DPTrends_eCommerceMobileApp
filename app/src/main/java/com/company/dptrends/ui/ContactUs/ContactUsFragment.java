package com.company.dptrends.ui.ContactUs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ContactUsFragment extends Fragment {



    public static ContactUsFragment newInstance() {
        return new ContactUsFragment();
    }

    private RelativeLayout relativeLayout2,relativeLayout3;
    private Button submitButton;
    private EditText queryText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        relativeLayout2 = view.findViewById(R.id.contactUs_relativeLayout2);
        relativeLayout3 = view.findViewById(R.id.contactUs_relativeLayout3);
        submitButton=view.findViewById(R.id.contactUs_submitButton);
        queryText = view.findViewById(R.id.contactUs_editText);
        submitButton.setOnClickListener(v->{
            if(TextUtils.isEmpty(queryText.getText().toString())){
                Toast.makeText(getActivity(), "Enter your query", Toast.LENGTH_SHORT).show();
            }
            else{


                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                SimpleDateFormat dateFormat1= new SimpleDateFormat("yyyyMMdd");
                String saveCurrentDate = dateFormat.format(calendar.getTime());
                String saveCurrentDate1 = dateFormat1.format(calendar.getTime());

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat timeFormat1 = new SimpleDateFormat("HHmmss");
                String saveCurrentTime = timeFormat.format(calendar.getTime());
                String saveCurrentTime1 = timeFormat1.format(calendar.getTime());
                HashMap<String, Object>hashMap = new HashMap<>();
                hashMap.put("phone", Prevalent.currentOnlineUser.getPhone());
                hashMap.put("query",queryText.getText().toString());
                hashMap.put("dateTime",saveCurrentDate1+saveCurrentTime1);
                FirebaseDatabase.getInstance().getReference()
                        .child(DBNodes.dbContactUS).child("Q"+saveCurrentDate1+saveCurrentTime1)
                        .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Query Submitted Successfully", Toast.LENGTH_SHORT).show();
                            relativeLayout2.setVisibility(View.GONE);
                            relativeLayout3.setVisibility(View.VISIBLE);
                        }
                        else{
                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}