package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Delivery;
import com.thesis.tipqc.ars_delivery.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Maps_main extends Fragment implements OnMapReadyCallback {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    private GoogleMap mMap;
    private LatLng camera = new LatLng(14.5377516, 121.0013794);

    AHBottomNavigationItem item1, item2, item3;
    AHBottomNavigation bottomNavigation;

    public static ArrayList<_Delivery> arrDelivery;
    ArrayList<PolylineOptions> routes;
    ArrayList<Polyline> savePath;
    ArrayList<Marker> location;

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.activity_maps_main,container,false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        bottomNavigation = myView.findViewById(R.id.bottom_navigation);
        bottomNavigation.setBackgroundColor(Color.BLUE);

        // Create items
        item1 = new AHBottomNavigationItem("Delivery", R.drawable.delivery_truck);
        item2 = new AHBottomNavigationItem("Summary", R.drawable.people);
        item3 = new AHBottomNavigationItem("Chat", R.drawable.people);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position == 0) {
                    showDelivery();
                } else if (position == 1) {

                } else if (position == 2) {

                }
                return true;
            }
        });

        arrDelivery = new ArrayList<>();
        location = new ArrayList<>();
        savePath = new ArrayList<>();
        routes = new ArrayList<>();

        retrieveDeliveryFromDatabase();

        return myView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(camera).title("Welcome"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(camera));
    }



    private void parseRoute(String route){
            String[] vehicle = route.split("<div>");

            for(String mVehicle: vehicle){

                String[] vehiclePath = mVehicle.split("<endPath>");
                double lat = 0, lot = 0;
                for (String mVehiclePath : vehiclePath) {
                    if (!mVehiclePath.equals("")) {
                        String[] category = mVehiclePath.split("::");

                        String vehicleID = category[0];
                        int colorPath = Integer.parseInt(category[1]);
                        String position = category[2];
                        String[] path = category[3].split("&");
                        routes.add(new PolylineOptions());
                        for (String mPath : path) {
                            String[] div = mPath.split("#");
                            lat = Double.parseDouble(div[0]);
                            lot = Double.parseDouble(div[1]);
                            routes.get(routes.size() - 1).add(new LatLng(lat, lot));
                        }
                        savePath.add(mMap.addPolyline(routes.get(routes.size() - 1).color(colorPath)));
                    }
                }
          }

    }

    private void showDelivery(){
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                //clear past routes
                for(Polyline path: savePath){
                    path.remove();
                }
                for(Marker marker: location){
                    marker.remove();
                }

                location.clear();
                savePath.clear();
                routes.clear();


                for(Map.Entry<String, String> realRoute: arrDelivery.get(index).getRealRoutes().entrySet()){
                    parseRoute(realRoute.getValue());
                }
                for(String transactKey: arrDelivery.get(index).getTransaction().keySet()){
                    String customerKey = arrDelivery.get(index).getTransaction().get(transactKey).getCustomer().keySet().toArray()[0].toString();
                    double latitude = Double.parseDouble(arrDelivery.get(index).getTransaction().get(transactKey).getCustomer().get(customerKey).getLatitude());
                    double longitude = Double.parseDouble(arrDelivery.get(index).getTransaction().get(transactKey).getCustomer().get(customerKey).getLongitude());

                    Marker marker = mMap.addMarker(new MarkerOptions().title("Customer").position(new LatLng(latitude, longitude)));
                    location.add(marker);
                }

                dialog.dismiss();

            }
        });
        if(adapter.getItemCount()!=0) adapter.clear();

        for(_Delivery delivery: arrDelivery){
            String deliveryID, name;

            deliveryID = delivery.getDeliveryPersonnel().keySet().toArray()[0].toString();
            name = delivery.getDeliveryPersonnel().get(deliveryID).getFirstName();
            adapter.add(new MaterialSimpleListItem.Builder(myView.getContext())
                    .content(name)
                    .icon(R.drawable.order)
                    .backgroundColor(Color.WHITE)
                    .build());
        }

        new MaterialDialog.Builder(myView.getContext())
                .title("Delivery Selection")
                .adapter(adapter, null)
                .show();
    }


    private void retrieveDeliveryFromDatabase() {
        String businessKey = ownerLogin.business.getBusinessKey();
        ref = database.getReference("Business/" + businessKey + "/Delivery");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot deliveryDataSnapshot : dataSnapshot.getChildren()) {
                        _Delivery delivery = deliveryDataSnapshot.getValue(_Delivery.class);
                        arrDelivery.add(delivery);
                    }

                } else {
                    //update the adapter
                    Toast.makeText(myView.getContext(), "You have no deliveries yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
