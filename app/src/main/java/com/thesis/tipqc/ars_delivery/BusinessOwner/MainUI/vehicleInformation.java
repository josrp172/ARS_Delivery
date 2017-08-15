package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_vehicles;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;


public class vehicleInformation extends AppCompatActivity {
    private TextView lblVehicleName, lblLicencePlate, lblType, lblMPG, lblVolume;
    private ImageView imgVehicle;
    private String vehicleID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        lblVehicleName = (TextView)findViewById(R.id.lblName2);
        lblLicencePlate = (TextView)findViewById(R.id.lblLicencePlate1);
        lblType = (TextView)findViewById(R.id.lblType1);
        lblMPG = (TextView)findViewById(R.id.lblMPG1);
        lblVolume = (TextView)findViewById(R.id.lblVolumeCapacity1);
        imgVehicle = (ImageView)findViewById(R.id.imgVehicle);

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
                                //remove courier's information from the database
                                String businessKey = ownerLogin.business.getBusinessKey();
                                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().getRoot().child("Business/"+businessKey+"/Vehicles").child(vehicleID);
                                db_node.removeValue();

                                //go back from the main UI
                                Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                                _OBJECTS.session.setCurrentActivity("Vehicles");
                                vehicle_main.arrVehicles.clear();
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
        int position = getIntent().getIntExtra("position", -1);
        if(position!=-1){
            String vehicleName = vehicle_main.arrVehicles.get(position).getVehicleName(),
                    licencePlate = vehicle_main.arrVehicles.get(position).getLicencePlate(),
                    type = vehicle_main.arrVehicles.get(position).getType(),
                    volume = vehicle_main.arrVehicles.get(position).getVolumeCapacity(),
                    mpg = vehicle_main.arrVehicles.get(position).getMpg();

            vehicleID = vehicle_main.arrVehicles.get(position).getVehicleID();

            if(type.equals("Car")) imgVehicle.setImageResource(recyclerAdapter_vehicles.images[2]);
            else if(type.equals("Scooter")) imgVehicle.setImageResource(recyclerAdapter_vehicles.images[4]);
            else if(type.equals("Truck")) imgVehicle.setImageResource(recyclerAdapter_vehicles.images[0]);
            else if(type.equals("Van")) imgVehicle.setImageResource(recyclerAdapter_vehicles.images[3]);
            else if(type.equals("Motorcycle")) imgVehicle.setImageResource(recyclerAdapter_vehicles.images[1]);

            lblVehicleName.setText(vehicleName);
            lblLicencePlate.setText(licencePlate);
            lblType.setText(type);
            lblVolume.setText(volume);
            lblMPG.setText(mpg);
        }else{
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }
}
