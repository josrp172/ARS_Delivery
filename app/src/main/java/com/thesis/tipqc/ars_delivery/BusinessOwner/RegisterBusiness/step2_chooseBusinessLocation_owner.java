package com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Customer;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


//https://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/

public class step2_chooseBusinessLocation_owner extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener{

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    //Joseph New
    private GoogleMap  mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker, mFindMarker;
    private MarkerOptions findMarker, currentPlaceMarker;
    private Geocoder geocoder;

    private double selectedLatitude, selectedLongitude;

    private Button  btnNext;
    private LatLng latLng;
    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final static int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String getPlace, getAddressName;
    private int markerCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.thesis.tipqc.ars_delivery.R.layout.activity_step2_choose_business_location_owner);
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

                //add the marker where the user inputted
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onMarkerDragEnd(Marker marker) {
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        if (marker.equals(mFindMarker)) {
            LatLng currentAddressPosition = mFindMarker.getPosition();

            try {
                List<Address> addressName =  geo.getFromLocation(currentAddressPosition.latitude, currentAddressPosition.longitude,1);
                mFindMarker.setTitle(addressName.get(0).getFeatureName()+""+addressName.get(0).getAdminArea()+", "+addressName.get(0).getLocality()+", "+addressName.get(0).getCountryName());
                mFindMarker.showInfoWindow();

                selectedLatitude = addressName.get(0).getLatitude();
                selectedLongitude = addressName.get(0).getLongitude();
                getAddressName = mFindMarker.getTitle();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(marker.equals(mCurrLocationMarker)){
            LatLng currentAddressPosition = mCurrLocationMarker.getPosition();

            try {
                List<Address> addressName =  geo.getFromLocation(currentAddressPosition.latitude, currentAddressPosition.longitude,1);
                mCurrLocationMarker.setTitle(addressName.get(0).getAddressLine(0)+" St."+", "+addressName.get(0).getLocality());
                mCurrLocationMarker.showInfoWindow();

                selectedLatitude = addressName.get(0).getLatitude();
                selectedLongitude = addressName.get(0).getLongitude();
                getAddressName = mCurrLocationMarker.getTitle();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    //later
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    public void onclick(View v){
        if(v.equals(btnNext)){
            if(markerCount!=0) {
                if(mFindMarker!=null) mFindMarker.hideInfoWindow();
                if(mCurrLocationMarker!=null) mCurrLocationMarker.hideInfoWindow();


                //save latitude, longitude and addressName from the BUSINESS object class
                step1_register_owner.business.setLatitude(selectedLatitude);
                step1_register_owner.business.setLongitude(selectedLongitude);
                step1_register_owner.business.setAddress(getAddressName);
                //end

                //go to step 3 - upload picture
                Intent i = new Intent(getApplicationContext(), step3_uploadPhoto_owner.class);
                startActivity(i);
                //end
            }else{
                Toast.makeText(getApplicationContext(), "You have no markers yet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //this is used to get the result of the autocomplete address
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


    public void setMap(String place){
        String location = place;
        //if location has not been set
        if(location!=null && !(location.equals(""))){
            //if there is a marker in the map
            mMap.clear();

            geocoder = new Geocoder(this);
            List<Address> addressList = null;

            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //need to import address where it composed of methods needed
            Address address = addressList.get(0);
            selectedLatitude = address.getLatitude();
            selectedLongitude = address.getLongitude();
            latLng = new LatLng(selectedLatitude, selectedLongitude);
            //end

            //this is your selected place based on the autocomplete TextView
            findMarker = new MarkerOptions().position(latLng);
            mFindMarker = mMap.addMarker(findMarker);
            mFindMarker.setDraggable(true);
            //end


            //best way to animateCamera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.5f),4000, null);
            ++markerCount;
            //end
        }
    }



    /*
     *This function is called when map is ready to be used. Here we can add all markers,
     *listeners and other functional attributes. Add following code inside
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //this is for initialization
        mMap = googleMap;
        LatLng philippines = new LatLng(12.8797,121.7740);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(philippines,5.5f));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            //Initialize Google Play Services
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
            }
            else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            //Show rationale and request permission.
        }

        ///(6) when location button is clicked
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                if(mFindMarker != null){
                    mFindMarker.remove();
                }

                mCurrLocationMarker = mMap.addMarker(currentPlaceMarker);
                mCurrLocationMarker.setDraggable(true);


                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                return true;
            }
        });

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);




    }
      //end of method

    // NOT YET NEEDED (IF YOU HAVE ANDROID 6.0, then turn it on
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //9
        ref = database.getReference("Location");


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        //end(9)


        //(8)this lets the system asked the user to turn on their GPS to see their current location (REQUIRED)
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); // this is the key ingredient

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(step2_chooseBusinessLocation_owner.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
        //end(8)

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    Map<String, String> hm = new HashMap<>();

    @Override
    public void onLocationChanged(Location location) {
        hm.put("Latitude", Double.toString(location.getLatitude()));
        hm.put("Longitude", Double.toString(location.getLongitude()));
        hm.put("Time", Long.toString(location.getTime()));
        hm.put("Nano", Long.toString(location.getElapsedRealtimeNanos()));
        hm.put("Bearing", Float.toString(location.getAccuracy()));

        ref.setValue(hm);

        //Place current location marker
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //get the current LatLng
        selectedLatitude = location.getLatitude();
        selectedLongitude = location.getLongitude();


        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.setPosition(latLng);

        }else{
            currentPlaceMarker = new MarkerOptions();
            currentPlaceMarker.position(latLng);
            currentPlaceMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(currentPlaceMarker);
            mCurrLocationMarker.setDraggable(true);
        }

        ++markerCount;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0) {
                    HashMap<String, Object> value = (HashMap<String, Object>)dataSnapshot.getValue();
                    Toast.makeText(getApplicationContext(), "Lat: "+dataSnapshot.child("Latitude").getValue()+"\n Lot: "+dataSnapshot.child("Longitude").getValue()+
                            "\nBearing: "+dataSnapshot.child("Bearing").getValue(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
