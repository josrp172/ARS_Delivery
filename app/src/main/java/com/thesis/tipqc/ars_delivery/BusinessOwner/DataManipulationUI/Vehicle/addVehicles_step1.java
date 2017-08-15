package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Vehicle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Vehicles;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;

import java.util.HashMap;
import java.util.Map;

public class addVehicles_step1 extends AppCompatActivity {
    private EditText txtVehicleName, txtLicencePlate, txtMpg, txtVolume;
    private Spinner spnType;
    private Button btnAdd, btnCancel;
    private ImageSwitcher imgType;
    private ArrayAdapter<String> arrType;
    private final String[] typeName = { "Car", "Scooter", "Truck", "Van", "Motorcycle"};

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    String vehicleName,licencePlate,mpg,type,volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicles_step1);

        txtVehicleName = (EditText)findViewById(R.id.txtVehicleName);
        txtLicencePlate = (EditText)findViewById(R.id.txtLicencePlate);
        txtMpg = (EditText)findViewById(R.id.txtMpg);
        txtVolume = (EditText)findViewById(R.id.txtVolume);
        spnType = (Spinner)findViewById(R.id.spnType);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        imgType = (ImageSwitcher)findViewById(R.id.imgType);

        arrType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typeName);
        spnType.setAdapter(arrType);

        //imageSwitcher
        imgType.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                return imageView;
            }
        });

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                if(selectedItem.equals("Car")){
                        imgType.setImageResource(R.drawable.suv);
                }else if(selectedItem.equals("Scooter")){
                        imgType.setImageResource(R.drawable.vespa);
                }else if(selectedItem.equals("Truck")){
                        imgType.setImageResource(R.drawable.delivery_truck);
                }else if(selectedItem.equals("Van")){
                        imgType.setImageResource(R.drawable.trucking);
                }else if(selectedItem.equals("Motorcycle")){
                        imgType.setImageResource(R.drawable.motor_sports);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //do nothing
            }
        });
        String businessKey = ownerLogin.business.getBusinessKey();
        ref = database.getReference("Business/"+businessKey);
    }

    public void click(View v){
        if(validate())
        {
            if(v.equals(btnAdd)){
                //save vehicle information to the database
                saveInformation();

                //go to main UI
                Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                _OBJECTS.session.setCurrentActivity("Vehicles");
                startActivity(i);
            }else if(v.equals(btnCancel)){

            }
        }
        else
        {
            Toast.makeText(this,"Add vehicle failed!",Toast.LENGTH_LONG).show();
        }
    }

    private void saveInformation() {
        _Vehicles v = new _Vehicles();

        String vehicleName = txtVehicleName.getText().toString(),
                licencePlate = txtLicencePlate.getText().toString(),
                mpg = txtMpg.getText().toString(),
                volume = txtVolume.getText().toString(),
                type = spnType.getSelectedItem().toString();

        v.setVehicleName(vehicleName);
        v.setLicencePlate(licencePlate);
        v.setMpg(mpg);
        v.setVolumeCapacity(volume);
        v.setType(type);
        //v.put("Business ID", businessKey);

        DatabaseReference key = ref.push();
        String vehicleKey = key.getKey();
        v.setVehicleID(vehicleKey);

        ref.child("Vehicles").child(vehicleKey).setValue(v);
    }

    public void initialize()
    {
        vehicleName = txtVehicleName.getText().toString().trim();
        licencePlate = txtLicencePlate.getText().toString().trim();
        mpg = txtMpg.getText().toString().trim();
        volume = txtVolume.getText().toString().trim();
        type = spnType.getSelectedItem().toString().trim();
    }

    public boolean validate()
    {
        initialize();
        boolean valid = true;
        if(vehicleName.isEmpty() | vehicleName.length() > 15)
        {
            txtVehicleName.setError("Please enter a valid vehicle name");
            valid = false;
        }
        if(licencePlate.isEmpty() | licencePlate.length() > 7)
        {
            txtLicencePlate.setError("Please enter a valid license plate");
            valid = false;
        }
        if(mpg.isEmpty() | mpg.length() > 20)
        {
            txtMpg.setError("Please enter or compute a valid value for mpg");
            valid = false;
        }
        if(volume.isEmpty() | volume.length() > 20)
        {
            txtVolume.setError("Please computer or enter a valid value for volume");
            valid = false;
        }
        if(type.isEmpty())
        {
            valid = false;
        }
        return valid;
    }

}
