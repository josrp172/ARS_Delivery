package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.R;

public class customerInformation extends AppCompatActivity{
    private TextView lblName, lblEmail, lblPhone, lblAddress;
    private MapView customerMap;
    private GoogleMap mMap;
    private int position;
    String customerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.thesis.tipqc.ars_delivery.R.layout.activity_customer_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        lblName = (TextView)findViewById(R.id.lblName2);
        lblEmail = (TextView)findViewById(R.id.lblEmail1);
        lblPhone =(TextView)findViewById(R.id.lblPhone1);
        lblAddress = (TextView)findViewById(R.id.lblAddress1);
        customerMap = (MapView) findViewById(R.id.customerMapView);
        customerMap.onCreate(savedInstanceState);

        getInformation();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
                                String businessKey = ownerLogin.business.getBusinessKey();
                                //remove courier's information from the database
                                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().getRoot().child("Business/"+businessKey+"/Customer").child(customerID);
                                db_node.removeValue();

                                customer_main.arrCustomer.clear();

                                //go back from the main UI
                                Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                                _OBJECTS.session.setCurrentActivity("Customer");
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
            }
        });
    }



    private void getInformation(){
         position = getIntent().getIntExtra("position", -1);
        if(position!=-1){
            String name = customer_main.arrCustomer.get(position).getFirstName()+" "+customer_main.arrCustomer.get(position).getLastName(),
                    email = customer_main.arrCustomer.get(position).getEmail(),
                    phone = customer_main.arrCustomer.get(position).getMobilePhone(),
                    address = customer_main.arrCustomer.get(position).getAddress();

            customerID = customer_main.arrCustomer.get(position).getCustomerID();

            //imgCourier.setImageBitmap(courierBitmap);
            lblName.setText(name);
            lblEmail.setText(email);
            lblPhone.setText(phone);
            lblAddress.setText(address);

            // Gets to GoogleMap from the MapView and does initialization stuff
            customerMap.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    setUpMap(googleMap);
                }
            });

        }else{
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customerMap.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        customerMap.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        customerMap.onPause();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        customerMap.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        customerMap.onSaveInstanceState(outState);
    }

    private void setUpMap(GoogleMap map) {
        double latitude = Double.parseDouble(customer_main.arrCustomer.get(position).getLatitude()),
                longitude = Double.parseDouble(customer_main.arrCustomer.get(position).getLongitude());
        mMap = map;
        LatLng customerPlace = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(customerPlace).title("Customer's place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerPlace,15.0f));

    }

}
