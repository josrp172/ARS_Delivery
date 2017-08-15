package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Product;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.__Loader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class addProducts_step2 extends AppCompatActivity {
    private Button btnCamera, btnUpload, btnNext;
    private ImageView imgProductPic;
    private Bitmap bitmap1;
    private final int PICK_PHOTO_FOR_AVATAR = 1;
    private Uri filePath;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products_step2);

        btnCamera = (Button)findViewById(R.id.btnCamera);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        btnNext = (Button)findViewById(R.id.btnNext);
        imgProductPic = (ImageView)findViewById(R.id.imgProductPic);

    }

    public void click(View v){
        if(v.equals(btnUpload)){
            //get the image from the chosen gallery
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getIntent().setType("image/*");
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_PHOTO_FOR_AVATAR);
        }else if(v.equals(btnCamera)){
            //get the image from taking picture/camera
            Intent i_Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(Intent.createChooser(i_Camera, "Select Picture"), PICK_PHOTO_FOR_AVATAR);
        }else if(v.equals(btnNext)){
            if(bitmap1!=null){

                //(2)Save products information to the database
                saveInformation();
                Toast.makeText(this,"Add product success!",Toast.LENGTH_SHORT).show();
                //end(2)

            }else{
                Toast.makeText(getApplicationContext(), "You have no picture yet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK){
            if(data == null){
                //Display an error
                return;
            }
            filePath = data.getData();
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgProductPic.setImageBitmap(bitmap1);

                //addProducts_step1.products.setPhoto(bitmap1);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    MaterialDialog md;
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference, imageFolder;
    public void saveInformation(){
        String businessKey = ownerLogin.business.getBusinessKey(); //_OBJECTS.session.getBusinessKey(); //must not be null
        ref = database.getReference("Business/"+businessKey);

        DatabaseReference key = ref.push();
        String productKey = key.getKey();

        addProducts_step1.products.setProductKey(productKey);
        addProducts_step1.products.setBusinessKey(businessKey);
        addProducts_step1.products.setVolume("10");

        ref.child("Products").child(productKey).setValue(addProducts_step1.products);

        //store productKey to session for later use
        _OBJECTS.session.setProductKey(productKey);


        md = new MaterialDialog.Builder(this)
                .title("Saving Product")
                .content("Uploading Product Information to the database")
                .progress(false, 100, true)
                .cancelable(false)
                .show();

        storageReference = storage.getReference();
        imageFolder = storageReference.child("Products");
        StorageReference images = imageFolder.child(productKey);
        images.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        md.dismiss();

                        _OBJECTS.session.setCurrentActivity("Delivery Personnel");
                        Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                        md.setProgress((int)progress);
                    }
                });

    }
}
