package com.company.dptrends.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.dptrends.Model.Products;
import com.company.dptrends.Prevalent.Prevalent;
import com.company.dptrends.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, ProductName,saveCurrentDate,saveCurrentTime,saveCurrentDateKey,saveCurrentTimeKey,act,pid,Quantity;
    private Button AddNewProductButton;
    private EditText InputProductName,InputProductDesc,InputProductPrice,InputProductQuantity;
    private TextView categoriesTxt;
    private ImageView InputProductImage;
    private Uri ImageUri;
    private static final int GalleryPick = 1;
    private String productKey,downloadImageUrl="";
    private StorageReference ProductImageRef;
    private DatabaseReference ProductRef;
    private StorageTask uploadTask;
    private ProgressDialog loadingBar;
    private String ImageURLString="",ImageStringURL="";
    private String checker = "empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        CategoryName = getIntent().getExtras().get("Categories").toString();
        act = getIntent().getExtras().get("act").toString();
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("ProductImages");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        AddNewProductButton = findViewById(R.id.add_new_product);
        InputProductName = findViewById(R.id.product_name);
        InputProductDesc = findViewById(R.id.product_description);
        InputProductPrice = findViewById(R.id.product_price);
        InputProductImage = findViewById(R.id.select_product_image);
        InputProductQuantity = findViewById(R.id.product_quantity);
        categoriesTxt = findViewById(R.id.adminaddnewproducts_Categories_txt2);
        loadingBar = new ProgressDialog(this);

        categoriesTxt.setText(CategoryName);

        if(act.equals("add")){
            //Toast.makeText(AdminAddNewProductActivity.this, "ADD", Toast.LENGTH_SHORT).show();
            AddNewProductButton.setText("ADD PRODUCT");
            AddNewProductButton.setOnClickListener(v->{
                ValidateProductData();
            });
        }
        else if(act.equals("edit")){
            AddNewProductButton.setText("SAVE PRODUCT");
            pid = getIntent().getExtras().get("productID").toString();
            fetchProductData(pid);
            AddNewProductButton.setOnClickListener(v->{
                saveEditedProduct(pid);
            });
        }

        InputProductImage.setOnClickListener(v->{
            checker = "clicked";
            OpenGallery();
        });



    }

    //edit data
    private void fetchProductData(String pid) {

        ProductRef.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Products products = snapshot.getValue(Products.class);
                InputProductName.setText(products.getProductName());
                InputProductDesc.setText(products.getDescription());
                InputProductPrice.setText(products.getPrice());
                if(products.getQuantity() != null){
                    InputProductQuantity.setText(products.getQuantity());
                }
                ImageStringURL=products.getImage();
                Picasso.get().load(ImageStringURL).into(InputProductImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminAddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveEditedProduct(String pid) {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        Description = InputProductDesc.getText().toString();
        Price = InputProductPrice.getText().toString();
        ProductName = InputProductName.getText().toString();
        Quantity = InputProductQuantity.getText().toString();
        if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please write Product Description", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Please write Product Price", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(ProductName)){
            Toast.makeText(this, "Please write Product name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Quantity)){
            Toast.makeText(this, "Please write Quantity", Toast.LENGTH_SHORT).show();
        }
        else {


            loadingBar.setTitle("Saving Data");
            loadingBar.setMessage("Wait while processing...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("productName", ProductName);
            productMap.put("description", Description);
            productMap.put("price", Price);
            productMap.put("quantity", Quantity);
            productMap.put("itemEditedBy",Prevalent.currentOnlineUser.getPhone());
            productMap.put("itemEditedDate",saveCurrentDate);
            if (checker.equals("empty")) {
                productMap.put("image", ImageStringURL);
            } else if (checker.equals("clicked")) {

                if(ImageUri != null) {
                    ProductImageRef.child(pid + ".jpg").delete();
                    StorageReference fileRef = ProductImageRef.child(pid + ".jpg");
                    uploadTask = fileRef.putFile(ImageUri);

                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull @NotNull Task task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadURL = task.getResult();
                                ImageStringURL = downloadURL.toString();
                                //Toast.makeText(AdminAddNewProductActivity.this,"ImageUrl: "+ImageStringURL , Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Toast.makeText(AdminAddNewProductActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    Toast.makeText(AdminAddNewProductActivity.this,"ImageUrl: "+ImageStringURL , Toast.LENGTH_SHORT).show();
                    productMap.put("image",ImageStringURL);

                }
                //productMap.put("image", imaURL[0]);
            }
            //productMap.put("image",ImageStringURL);
            ProductRef.child(pid).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        loadingBar.dismiss();
                        Toast.makeText(AdminAddNewProductActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        loadingBar.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(AdminAddNewProductActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    loadingBar.dismiss();
                    Intent i = new Intent(AdminAddNewProductActivity.this, AdminCategoriesProductsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("categories",CategoryName);
                    startActivity(i);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingBar.dismiss();
                    String message = e.toString();
                    Toast.makeText(AdminAddNewProductActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    //add new data to database
    private void ValidateProductData() {
        Description = InputProductDesc.getText().toString();
        Price = InputProductPrice.getText().toString();
        ProductName = InputProductName.getText().toString();
        Quantity=InputProductQuantity.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this, "Product Image is necessary", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please write Product Description", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Please write Product Price", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(ProductName)){
            Toast.makeText(this, "Please write Product name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Quantity)){
            Toast.makeText(this, "Please write Quantity", Toast.LENGTH_SHORT).show();
        }
        else{

            loadingBar.setTitle("Adding New Product");
            loadingBar.setMessage("Wait while Processing...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            StoreProductInformation();
        }

    }

    private void StoreProductInformation() {

        /*
        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Please wait while processing");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

         */

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentDateKey = new SimpleDateFormat("yyyyMMdd");
        saveCurrentDateKey = currentDateKey.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendar.getTime());
        SimpleDateFormat currentTimeKey = new SimpleDateFormat("HHmmss");
        saveCurrentTimeKey = currentTimeKey.format(calendar.getTime());

        productKey = saveCurrentDateKey + saveCurrentTimeKey;


        SaveProductInfoToDatabase();


    }

    private void SaveProductInfoToDatabase() {

        if(ImageUri != null){
            StorageReference fileRef = ProductImageRef
                    .child(productKey+".jpg");
            uploadTask = fileRef.putFile(ImageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull @NotNull Task task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadURL = task.getResult();
                        ImageURLString = downloadURL.toString();
                        DatabaseReference reference =FirebaseDatabase.getInstance()
                                .getReference().child("Products");


                        HashMap<String,Object> productMap = new HashMap<>();
                        productMap.put("productID",productKey);
                        productMap.put("itemAddedDate",saveCurrentDate);
                        productMap.put("itemAddedTime",saveCurrentTime);
                        productMap.put("description",Description);
                        productMap.put("image",ImageURLString);
                        productMap.put("category",CategoryName);
                        productMap.put("productName",ProductName);
                        productMap.put("price", Price);
                        productMap.put("quantity", Quantity);
                        productMap.put("itemAddedBy", Prevalent.currentOnlineUser.getPhone());

                        reference.child(productKey)
                                .updateChildren(productMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            loadingBar.dismiss();
                                            Toast.makeText(AdminAddNewProductActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(AdminAddNewProductActivity.this, AdminCategoriesProductsActivity.class);
                                            i.putExtra("categories",CategoryName);
                                            startActivity(i);
                                        }
                                        else
                                        {
                                            loadingBar.dismiss();
                                            String message = task.getException().toString();
                                            Toast.makeText(AdminAddNewProductActivity.this, message, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                    }
                    else
                    {
                        loadingBar.dismiss();
                        Toast.makeText(AdminAddNewProductActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            loadingBar.dismiss();
            Toast.makeText(AdminAddNewProductActivity.this, "Image Not Selected", Toast.LENGTH_SHORT).show();
        }


    }

    /*
    public void getImaURL(String pid){
        final StorageReference ref = ProductImageRef.child(pid+".jpg");
        final UploadTask uploadTask = ref.putFile(ImageUri);
        Task<Uri> task  = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    ImageString = task.getResult().toString();
                }
                else
                {
                    Toast.makeText(AdminAddNewProductActivity.this, "Error 1", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

 */

    public String getImageUrl(String productKey) {

        StorageReference filePath = ProductImageRef.child(productKey+".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this,"Error",Toast.LENGTH_LONG).show();
                //loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Image Added successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        //int d=5;
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            //ImageString = downloadImageUrl;
                            //downloadImageUrl = task.getResult().toString();
                            //String h="hello";
                            //Toast.makeText(AdminAddNewProductActivity.this, "Getting product Image URL successfully", Toast.LENGTH_SHORT).show();

                        }
                    }

                });

            }
        });
        return downloadImageUrl;
    }

    //Select Image from Phone
    private void OpenGallery() {
        CropImage.activity()
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ImageUri = result.getUri();
                InputProductImage.setImageURI(ImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(AdminAddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}