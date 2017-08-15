package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;


public class deliveryInformation extends AppCompatActivity {
    private TextView lblName1, lblEmail1, lblPhone1, lblAddress1;
    private ImageView imgCourier;
    private String courierKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        lblName1 = (TextView)findViewById(R.id.lblName2);
        lblEmail1 = (TextView)findViewById(R.id.lblEmail1);
        lblPhone1 = (TextView)findViewById(R.id.lblPhone1);
        lblAddress1 = (TextView)findViewById(R.id.lblAddress1);
        imgCourier = (ImageView)findViewById(R.id.imgCourier);
        getInformation();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                builder1.setMessage("Are you sure you want to delete this");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //remove courier's information from the database
                                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().getRoot().child("Delivery Personnel").child(courierKey);
                                db_node.removeValue();

                                //remove products pic from the storage
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference productsReference = storage.getReferenceFromUrl("gs://arsdelivery2017.appspot.com/Delivery Personnel/"+courierKey);
                                productsReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //successfully deleted the picture.
                                    }
                                });


                                //go back from the main UI
                                Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                                _OBJECTS.session.setCurrentActivity("Delivery Personnel");
                                courier_main.arrCourier.clear();
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                //delete

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
            }
        });
    }

    private void getInformation(){
        int position = getIntent().getIntExtra("position", -1);
        if(position!=-1){
            String name = courier_main.arrCourier.get(position).getFirstName()+" "+courier_main.arrCourier.get(position).getLastName(),
                    email = courier_main.arrCourier.get(position).getEmail(),
                    phone = courier_main.arrCourier.get(position).getMobilePhone(),
                    address = courier_main.arrCourier.get(position).getAddress();
            courierKey = courier_main.arrCourier.get(position).getCourierKey();


            //Bitmap courierBitmap = courier_main.arrCourier.get(position).getPhoto();
            imgCourier.setImageBitmap(_OBJECTS.session.getPhoto());

            lblName1.setText(name);
            lblEmail1.setText(email);
            lblPhone1.setText(phone);
            lblAddress1.setText(address);
        }else{
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }


}
