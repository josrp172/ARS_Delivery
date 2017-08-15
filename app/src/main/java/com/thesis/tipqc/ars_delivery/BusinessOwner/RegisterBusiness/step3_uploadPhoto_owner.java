package com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.__Loader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class step3_uploadPhoto_owner extends AppCompatActivity {
    private Button btnUpload, btnCamera, btnNext;
    private ImageView imgBusiness;
    private Bitmap bitmap1;
    private Uri filePath;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    private final int PICK_PHOTO_FOR_AVATAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3_upload_photo_owner);

        btnUpload = (Button)findViewById(R.id.btnUpload);
        btnCamera = (Button)findViewById(R.id.btnCamera1);
        btnNext = (Button)findViewById(R.id.btnNext);
        imgBusiness = (ImageView)findViewById(R.id.imgProductPic);

        ref = database.getReference("Business");
    }

    public void clickStep3(View v){
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
                //store image to business object in base64 string
                //_OBJECTS.business.setPhoto(__base64EncodeDecoder.encodeTobase64(bitmap1));
                //business registration is already done

                //(2)Save business information to the database
                saveInformation();
                //end(2)


                //step 4 (admin registration/obligation)
               // Intent i = new Intent(getApplicationContext(), step4_adminObligation.class);
              //  startActivity(i);
            }else{
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
            filePath = data.getData();
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgBusiness.setImageBitmap(bitmap1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //save information without the need of authentication
    private void saveInformation(){

        DatabaseReference key = ref.push();
        String businessKey = key.getKey();
        step1_register_owner.business.setBusinessKey(businessKey);

        ref.child(businessKey).setValue(step1_register_owner.business);

        //save businessKey from the session from later use(Admin)
        _OBJECTS.session.setBusinessKey(businessKey);

        //Joseph New (Upload business image while loading)
        Intent i = new Intent(getApplicationContext(), __Loader.class);
        i.putExtra("storageFolder", "Business");
        i.putExtra("keyName", businessKey);
        i.putExtra("filePath", filePath.toString());
        i.putExtra("currentLoadActivity", "step3_uploadPhoto_owner");
        startActivity(i);
    }
}
