package com.thesis.tipqc.ars_delivery.BusinessOwner;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Courier.addDeliveryPersonnel_DONE;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Product.addProducts_DONE;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Business;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Owner;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness.step4_adminObligation;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness.step7_doneRegistration;

public class __Loader extends AppCompatActivity {
    private TextView lblProgress, lblTitle;
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference, imageFolder;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref, ref1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity____loader);
        lblProgress = (TextView)findViewById(R.id.lblProgress);
        lblTitle = (TextView)findViewById(R.id.lblTitle);
        final String currentLoadActivity = getIntent().getStringExtra("currentLoadActivity");

        if(currentLoadActivity.equals("Login")){
            lblTitle.setText("Authenticating...");
            lblProgress.setVisibility(View.GONE);

            String email = getIntent().getStringExtra("email"),
                    password = getIntent().getStringExtra("password");

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //Joseph New run background ------------------------------------
                                //converting image run in background
                                //do stuff
                                FirebaseUser user = task.getResult().getUser();

                                ref = database.getReference("User/"+user.getUid());

                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ownerLogin.owner = dataSnapshot.getValue(_Owner.class);

                                        String businessKey = ownerLogin.owner.getBusinessKey();
                                        _OBJECTS.session.setBusinessKey(businessKey);
                                        ref1 = database.getReference("Business/"+businessKey);
                                        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                ownerLogin.business = dataSnapshot.getValue(_Business.class);

                                                Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                                                startActivity(i);


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(), "Error in retrieving from database", Toast.LENGTH_LONG).show();
                                        //go back to owner login
                                        Intent i = new Intent(getApplicationContext(), ownerLogin.class);
                                        startActivity(i);
                                    }
                                });
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed to LogIn", Toast.LENGTH_SHORT).show();
                                //go back to owner login
                                Intent i = new Intent(getApplicationContext(), ownerLogin.class);
                                startActivity(i);
                            }
                        }
                    });

        }else{
            String storageFolder = getIntent().getStringExtra("storageFolder"),
                    keyName = getIntent().getStringExtra("keyName");
            Uri filePath = Uri.parse(getIntent().getStringExtra("filePath"));


            storageReference = storage.getReference();
            imageFolder = storageReference.child(storageFolder);
            StorageReference images = imageFolder.child(keyName);
            images.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //successful
                            Intent i = null;
                            if(currentLoadActivity.equals("step3_uploadPhoto_owner")){
                                i = new Intent(getApplicationContext(), step4_adminObligation.class);
                            }else if(currentLoadActivity.equals("step6_ownerProfilePic")){
                                i = new Intent(getApplicationContext(), step7_doneRegistration.class);
                            }
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
                            //can be useful in displaying the progress of the upploading of file
                            double progress = (100.0*taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                            lblProgress.setText(Integer.toString((int)progress)+"%");

                        }
                    });
        }


    }
}
