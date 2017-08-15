package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Business;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._DeliveryPersonnel;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Owner;
import com.thesis.tipqc.ars_delivery.Personnel.Main_UI_DP;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness.step1_register_owner;

public class ownerLogin extends AppCompatActivity {
    private Button btnSignIn, btnRegister;
    private EditText txtUsername, txtPassword;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static _Business business = new _Business();
    public static _Owner owner = new _Owner();
    public static _DeliveryPersonnel courier = new _DeliveryPersonnel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);

        txtUsername = (EditText)findViewById(R.id.txtUsername);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnRegister = (Button)findViewById(R.id.btnRegister);

    }

    public void click(View v){
        if(v.equals(btnRegister)){
            //go to owner register page

            Intent i = new Intent(getApplicationContext(), step1_register_owner.class);
            startActivity(i);

        }else if(v.equals(btnSignIn)){
            logIn();
        }
    }

    String email, password;
    MaterialDialog md;
    private DatabaseReference ref, ref1;
    private void logIn(){
        email = txtUsername.getText().toString();
        password = txtPassword.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Email or password is empty", Toast.LENGTH_SHORT).show();
        }else{
            authentication();
            md = new MaterialDialog.Builder(this)
                    .title("Authentication")
                    .content("Validating your account...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
        }
    }

    FirebaseUser user;
    private void authentication(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Joseph New run background ------------------------------------
                            //converting image run in background
                            //do stuff
                           user = task.getResult().getUser();
                            ref = database.getReference("User/"+user.getUid());

                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild("firstName")){
                                        adminLogin(dataSnapshot);
                                    }else {
                                        courierLogin(user);
                                    }
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
                            md.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to LogIn", Toast.LENGTH_SHORT).show();
                            //go back to owner login
                            Intent i = new Intent(getApplicationContext(), ownerLogin.class);
                            startActivity(i);
                        }
                    }
                });
    }


    public void adminLogin(DataSnapshot dataSnapshot){
        owner = dataSnapshot.getValue(_Owner.class);
        String businessKey = owner != null ? owner.getBusinessKey() : null;
        _OBJECTS.session.setBusinessKey(businessKey);

        ref1 = database.getReference("Business/" + businessKey);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                business = dataSnapshot.getValue(_Business.class);
                _OBJECTS.session.setCurrentActivity("Main");
                Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                startActivity(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void courierLogin(FirebaseUser user){
        ref = database.getReference("Delivery Personnel/"+user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courier = dataSnapshot.getValue(_DeliveryPersonnel.class);
                String businessKey = courier != null ? courier.getBusinessKey() : null;
                _OBJECTS.session.setBusinessKey(businessKey);

                ref1 = database.getReference("Business/" + businessKey);
                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        business = dataSnapshot.getValue(_Business.class);
                        _OBJECTS.session.setCurrentActivity("Main");
                        Intent i = new Intent(getApplicationContext(), Main_UI_DP.class);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
