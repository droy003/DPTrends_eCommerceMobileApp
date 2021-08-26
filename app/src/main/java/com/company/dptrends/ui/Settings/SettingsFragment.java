package com.company.dptrends.ui.Settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.dptrends.Prevalent.DBNodes;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private TextView close,update,changeProfileImage, phoneNumberTXT;
    private EditText FullNameEditSettings,AddressEditSettings;
    private CircleImageView profileImageView;
    private Uri imageUri;
    private String imageURL;
    private StorageReference storageProfilePictureReference;
    private String checker = "";
    private StorageTask uploadTask;
    private String currentOnlineUserPhone;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    private FloatingActionButton floatingActionButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);

        close = view.findViewById(R.id.close_settings_btn);
        update = view.findViewById(R.id.update_acc_settings);
        changeProfileImage = view.findViewById(R.id.profile_image_change_btn);
        phoneNumberTXT = view.findViewById(R.id.settings_phoneNumber);
        FullNameEditSettings = view.findViewById(R.id.settings_Name);
        AddressEditSettings  = view.findViewById(R.id.settings_Address);
        profileImageView = view.findViewById(R.id.settings_profile_image);

        storageProfilePictureReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures");
        currentOnlineUserPhone = Prevalent.currentOnlineUser.getPhone();

        userInfoDisplay();

        changeProfileImage.setOnClickListener(v->{
            checker = "clicked";

            CropImage.activity()
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getContext(), this);
        });

        update.setOnClickListener(v->{


            if(checker.equals("clicked")){
                userInfoSaved();

            }
            else{
                updateOnlyUserInfo();
            }

        });

        close.setOnClickListener(v->{
            Navigation.findNavController(getView()).navigate(R.id.nav_home);

        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                profileImageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getView().getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void updateOnlyUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child(DBNodes.dbUsers).child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Name",FullNameEditSettings.getText().toString());
        hashMap.put("Address",AddressEditSettings.getText().toString());

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getView().getContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getView().getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        getParentFragmentManager().beginTransaction()
                .detach(SettingsFragment.newInstance())
                .attach(SettingsFragment.newInstance())
                .commit();
    }

    private void userInfoSaved() {

        if(TextUtils.isEmpty(AddressEditSettings.getText().toString())){
            Toast.makeText(getActivity(), "Enter Address Please", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(FullNameEditSettings.getText().toString())){
            Toast.makeText(getActivity(), "Enter Full Name Please", Toast.LENGTH_SHORT).show();
        }
        else{
            if(checker.equals("clicked")){
                uploadImage();
            }
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getView().getContext());
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait while Processing");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            StorageReference fileRef = storageProfilePictureReference
                    .child(currentOnlineUserPhone+".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull @NotNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadURL = task.getResult();
                        imageURL = downloadURL.toString();
                        DatabaseReference reference =FirebaseDatabase.getInstance()
                                .getReference().child("Users");
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("Name",FullNameEditSettings.getText().toString());
                        hashMap.put("Address",AddressEditSettings.getText().toString());
                        hashMap.put("Image",imageURL);
                        Prevalent.currentOnlineUser.setImage(imageURL);
                        Prevalent.currentOnlineUser.setName(FullNameEditSettings.getText().toString());
                        Prevalent.currentOnlineUser.setAddress(AddressEditSettings.getText().toString());
                        reference.child(currentOnlineUserPhone)
                                .updateChildren(hashMap);

                        progressDialog.dismiss();
                        getParentFragmentManager().beginTransaction()
                                .detach(SettingsFragment.newInstance())
                                .attach(SettingsFragment.newInstance())
                                .commit();
                        Toast.makeText(getView().getContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(getView().getContext(), "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(getView().getContext(), "Image Not Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay() {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child(DBNodes.dbUsers)
                .child(Prevalent.currentOnlineUser.getPhone());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("Image").exists()) {
                        String image = snapshot.child("Image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                    }
                    if(snapshot.child("Address").exists()) {
                        String address = snapshot.child("Address").getValue().toString();
                        AddressEditSettings.setText(address);
                    }
                    if(snapshot.child("Name").exists()) {
                        String name = snapshot.child("Name").getValue().toString();
                        FullNameEditSettings.setText(name);
                    }
                    phoneNumberTXT.setText(Prevalent.currentOnlineUser.getPhone());

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}