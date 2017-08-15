package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Customer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addCustomer_step2 extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap  mMap;
    private Button  btnNext;
    private LatLng latLng;
    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static Address address;
    private String getPlace, getAddressName;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private int markerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.thesis.tipqc.ars_delivery.R.layout.activity_add_customer_step2);
        //(1)declaration
        btnNext = findViewById(R.id.btnNext);


        //(2)this is needed to create google maps
        SupportMapFragment mapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFrag.getMapAsync(this);
        //end(2)

        //(3)this is for autocompelete suggestion in google maps
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fragment);
        //end(3)

        //(4)use to filter out autosuggestion (only for PH)
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("PH")
                .build();
        autocompleteFragment.setFilter(typeFilter);
        //end(4)

        //(5)listen to the suggested places and get its content
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("Look", "Place"+place.getName());
                getPlace = place.getName().toString();
                setMap(getPlace);
                getAddressName = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {
                Log.i("Look", "error occured"+status);
            }
        });
        //end(5)

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
            if(resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this,data);
                Log.i("Place", "Place: "+place.getName());

            }else if(resultCode == PlaceAutocomplete.RESULT_ERROR){
                Status status = PlaceAutocomplete.getStatus(this,data);
                Log.i("Look", status.getStatusMessage());
            }else if(resultCode == RESULT_CANCELED){
                //The user cancelled the operation
            }
        }
    }

    public void onclick(View v){
        if(v.equals(btnNext)){
            if(markerCount!=0) {
                //get the latitude, longitude and addressName from the object class(customer)
                addCustomer_step1.customer.setLatitude(Double.toString(address.getLatitude()));
                addCustomer_step1.customer.setLongitude(Double.toString(address.getLongitude()));
                addCustomer_step1.customer.setAddress(getAddressName);

                //save information from the database
                saveInformation();

                //go to main UI
                Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                _OBJECTS.session.setCurrentActivity("Customer");
                startActivity(i);
                Toast.makeText(this,"Add customer Success!",Toast.LENGTH_SHORT).show();
                //end
            }else{
                Toast.makeText(getApplicationContext(), "You have no markers yet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveInformation(){
        String businessKey = ownerLogin.business.getBusinessKey();
        ref = database.getReference("Business/"+businessKey);

        DatabaseReference key = ref.push();
        String customerKey = key.getKey();
        addCustomer_step1.customer.setCustomerID(customerKey);

        ref.child("Customer").child(customerKey).setValue(addCustomer_step1.customer);
    }

    public void setMap(String place){
        String location = place;
        //if location has not been set
        if(location!=null && !(location.equals(""))){
            //if there is a marker in the map
            mMap.clear();

            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = null;

            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //need to import address
            address = addressList.get(0);
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));

            //best way to animateCamera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.5f),4000, null);

            ++markerCount;
        }
    }

    /*
    private void setUpMapIfNeeded(){
        //do a null check to confirm that we have not already instantiated the map
        if(mMap == null){
            //Try to obtain the map from the SupportFragment.
            SupportMapFragment mapFrag = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            mapFrag.getMapAsync(this);
            if(mapFrag!=null){
                setUpMap();
            }
        }
    }
    */

    public void setUpMap(){
        mMap.addMarker(new MarkerOptions().position(new LatLng(12.8797,121.7740)).title("Marker"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //this is for initialization
        mMap = googleMap;
        LatLng philippines = new LatLng(12.8797,121.7740);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(philippines,5.5f));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //mMap.setMyLocationEnabled(true);
        } else {
            //Show rationale and request permission.
        }
    }

}
