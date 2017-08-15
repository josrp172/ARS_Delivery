package com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.__Loader;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects.__base64EncodeDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class step6_ownerProfilePic extends AppCompatActivity {
    private ImageView imgProfilePic;
    private Button btnCamera, btnUpload, btnNext;
    private Bitmap bitmap1;
    private FirebaseAuth firebaseAuth;
    private final int PICK_PHOTO_FOR_AVATAR = 1;
    private Uri filePath;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step6_owner_profile_pic);

        btnCamera = (Button)findViewById(R.id.btnCamera1);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        btnNext = (Button)findViewById(R.id.btnNext);
        imgProfilePic = (ImageView)findViewById(R.id.imgProductPic);

        firebaseAuth = FirebaseAuth.getInstance();
        ref = database.getReference("User");
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

                //Joseph New run background ------------------------------------
                //converting image run in background
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        //do stuff
                        //store image to business object in base64 string
                        //_OBJECTS.owner.setPhoto(__base64EncodeDecoder.encodeTobase64(bitmap1));
                        return "Executed";
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        //do stuff
                    }
                }.execute();


                //save to database/authentication
                registerUser(); //for authentication
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
                //inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                //bitmap1 = BitmapFactory.decodeStream(inputStream);
                imgProfilePic.setImageBitmap(bitmap1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerUser(){
        firebaseAuth.createUserWithEmailAndPassword(step5_ownerRegistration.owner.getEmailAddress(), step5_ownerRegistration.owner.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = task.getResult().getUser();
                            saveInformation(user.getUid());
                        }else{
                            Toast.makeText(getApplicationContext(), "Failed to Register", Toast.LENGTH_SHORT).show();

                            //go back to owner login
                            //Intent i = new Intent(getApplicationContext(), ownerLogin.class);
                           // startActivity(i);
                        }
                    }
                });
        //end
    }

    private void saveInformation(String userID){

        step5_ownerRegistration.owner.setUserID(userID);
        step5_ownerRegistration.owner.setBusinessKey(_OBJECTS.session.getBusinessKey());
        ref.child(userID).setValue(step5_ownerRegistration.owner);


        //_OBJECTS.storageDatabase.uploadFileToDatabase("User", userID, filePath);

        //Joseph New (Upload owner profile pic image while loading)
        Intent i = new Intent(getApplicationContext(), __Loader.class);
        i.putExtra("storageFolder", "User");
        i.putExtra("keyName", userID);
        i.putExtra("filePath", filePath.toString());
        i.putExtra("currentLoadActivity", "step6_ownerProfilePic");
        startActivity(i);
    }
}
