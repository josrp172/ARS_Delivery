package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Courier;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.__Loader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class addDeliveryPersonnel_step2 extends AppCompatActivity {
    private Button btnCamera, btnUpload, btnNext;
    private ImageView imgProfilePic;
    private ProgressBar progress1;

    private Bitmap bitmap1;
    private Uri filePath;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;

    String businessKey;

    private final int PICK_PHOTO_FOR_AVATAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_personnel_step2);

        //initialization
        btnCamera = (Button)findViewById(R.id.btnCamera1);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        btnNext = (Button)findViewById(R.id.btnNext);
        imgProfilePic = (ImageView)findViewById(R.id.imgProductPic);
        progress1 = (ProgressBar)findViewById(R.id.progressUpload);
        progress1.setMax(100);

        //get firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();
        //go to database child (delivery personnel)
        ref = database.getReference("Delivery Personnel");
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
                //store image to deliveryPersonnel object in base64 string
                //_OBJECTS.business.setPhoto(__base64EncodeDecoder.encodeTobase64(bitmap1));
                //business registration is already done

                //save to database/authentication
                registerUser(); //for authentication

            }else{
                //if no picture has beem added
                Toast.makeText(getApplicationContext(), "You have no picture yet!", Toast.LENGTH_SHORT);
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
            //get the picture and save it from URI
            filePath = data.getData();
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //set imageview image
                imgProfilePic.setImageBitmap(bitmap1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerUser(){
        //register delivery personnel from the database(authentication)
        firebaseAuth.createUserWithEmailAndPassword(addDeliveryPersonnel_step1.courier.getEmail(), addDeliveryPersonnel_step1.courier.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //get deliver personnel userID
                            FirebaseUser user = task.getResult().getUser();

                            //save information from the database
                            saveInformation(user.getUid());

                            //go to another activity if done
                            //Intent i = new Intent(getApplicationContext(), addDeliveryPersonnel_DONE.class);
                            //startActivity(i);
                        }else{
                            //if there is already a user existing or incorrect email/password
                            Toast.makeText(getApplicationContext(), "Failed to Register", Toast.LENGTH_SHORT).show();

                            //go back to owner login
                            Intent i = new Intent(getApplicationContext(), addDeliveryPersonnel_step1.class); //CHANGE THIS ------------------
                            startActivity(i);
                        }
                    }
                });
        //end
    }

    //save information without the need of authentication
    private void saveInformation(String userID){
        addDeliveryPersonnel_step1.courier.setCourierKey(userID);
        businessKey = ownerLogin.business.getBusinessKey(); //must not be null
        addDeliveryPersonnel_step1.courier.setBusinessKey(businessKey);

        ref.child(userID).setValue(addDeliveryPersonnel_step1.courier);


        //Joseph New (Upload Delivery personnel profile pic image while loading)
        Intent i = new Intent(getApplicationContext(), __Loader.class);
        i.putExtra("storageFolder", "Delivery Personnel");
        i.putExtra("keyName", userID);
        i.putExtra("filePath", filePath.toString());
        i.putExtra("currentLoadActivity", "addDeliveryPersonnel_DONE");
        startActivity(i);
    }


}
