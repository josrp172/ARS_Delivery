package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Delivery;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Orders;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Products;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Transaction;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Vehicles;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_orders;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.tasks.ProgressBarTask;
import com.thesis.tipqc.ars_delivery.tasks.VrpSolverTask;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingImporter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class addDelivery_step4_MAPS extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    private GoogleMap  mMap;
    public static ProgressBar progress;
    private LatLng camera = new LatLng(14.5377516, 121.0013794);

    public static _Delivery delivery;
    data d;

    //FILE STRINGS
    final String NAME = "NAME",
            COMMENT = "COMMENT",
            TYPE = "TYPE",
            DIMENSION = "DIMENSION",
            EDGE_WEIGHT_TYPE = "EDGE_WEIGHT_TYPE",
            EDGE_WEIGHT_FORMAT = "EDGE_WEIGHT_FORMAT",
            EDGE_WEIGHT_UNIT_OF_MEASUREMENT = "EDGE_WEIGHT_UNIT_OF_MEASUREMENT",
            CAPACITY = "CAPACITY",
            NODE_COORD_SECTION = "NODE_COORD_SECTION",
            EDGE_WEIGHT_SECTION = "EDGE_WEIGHT_SECTION",
            DEMAND_SECTION = "DEMAND_SECTION",
            DEPOT_SECTION = "DEPOT_SECTION",
            VEHICLES = "VEHICLES",
            EOF = "EOF";

    final int[] COLOR = {Color.BLUE,
            Color.YELLOW,
            Color.WHITE,
            Color.CYAN,
            Color.DKGRAY,
            Color.GREEN,
            Color.BLACK
    };

    //MATERIAL DESIGN
    MaterialDialog dialog;
    AHBottomNavigationItem item1, item2, item3, item4;
    AHBottomNavigation bottomNavigation;
    MaterialSimpleListAdapter adapter;


    //DECLARATION OF NEEDED OVERALL VARIABLES
    public ArrayList<LatLng> location;
    ArrayList<Polyline> tempRoutes;
    ArrayList<Polyline> realRoutes;
    Map<String, Marker> arrMarker;
    public static ArrayList<_Orders> arrOrders;
    public static ArrayList<_Vehicles> arrVehicles;
    public static Map<String, _Transaction> savedInfo;
    public static Map<String, String> vehicleTransaction;
    public static SparseBooleanArray selectedVehicle;
    SparseArray<ArrayList<Integer>> realRoutesPos;
    SparseArray<ArrayList<Integer>> realRouteCustomerPos;
    SparseBooleanArray selectedRoutesVehicle;

    double totalLoad, currentLoad;
    int selectedVehicleSize;
    private int timeLimitInSeconds;

    private VehicleRoutingSolution vrs;
    private VrpSolverTask vrpSolverTask;
    private ProgressBarTask progressBarTask;
    public boolean vrpSolverDone = false;
    private String algorithm;


    //INITIALIZATION
    public addDelivery_step4_MAPS(){
        super();
        this.vrs = null;
        this.vrpSolverTask = null;
        this.timeLimitInSeconds = 0;
        this.progressBarTask = null;
        this.algorithm = null;
        tempRoutes = new ArrayList<>();
        realRoutes = new ArrayList<>();
        arrMarker = new HashMap<>();
        arrOrders = new ArrayList<>();
        arrVehicles = new ArrayList<>();
        savedInfo = new HashMap<>();
        vehicleTransaction = new HashMap<>();
        selectedVehicle = new SparseBooleanArray();
        realRoutesPos = new SparseArray<>();
        realRouteCustomerPos = new SparseArray<>();
        selectedRoutesVehicle = new SparseBooleanArray();

        delivery  = new _Delivery();
        d = new data();
    }


    public void setVrs(VehicleRoutingSolution vrs) {
        this.vrs = vrs;
        drawTemporaryRoutes();
        if(!vrpSolverTask.isRunning()){
            if(vrpSolverDone){
                progress.setProgress(timeLimitInSeconds/1000);
                Toast.makeText(getApplicationContext(), "Drawing Real Routes", Toast.LENGTH_LONG).show();
                for(int i = 0; i < tempRoutes.size(); i++){
                    tempRoutes.get(i).remove();
                }
                drawRealRoutes();
                vrpSolverDone = false;
            }
        }
    }

    public VehicleRoutingSolution getVrs() {
        return vrs;
    }

    public VrpSolverTask getVrpSolverTask() {
        return vrpSolverTask;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.thesis.tipqc.ars_delivery.R.layout.activity_add_delivery_step4_maps);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Create items
        item1 = new AHBottomNavigationItem("Routes", R.drawable.delivery_truck);
        item2 = new AHBottomNavigationItem("Summary", R.drawable.order);
        item3 = new AHBottomNavigationItem("Play", R.drawable.delivery_truck);
        item4 = new AHBottomNavigationItem("Next", R.drawable.delivery_truck);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if(position == 0) {
                    showSelectedRoutes();
                }else if(position == 2){
                    //run the solution
                    new MaterialDialog.Builder(addDelivery_step4_MAPS.this)
                            .title("Time Required to finish")
                            .content("Input time")
                            .inputType(InputType.TYPE_CLASS_NUMBER )
                            .input("Seconds", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {

                                    //clear everything
                                    if(tempRoutes.size()!=0){
                                        //remove temporary route path
                                        for(Polyline route: tempRoutes){
                                            route.remove();
                                        }
                                        tempRoutes.clear();
                                    }
                                    if(realRoutes.size()!=0){
                                        //remove real route path
                                        for(Polyline route: realRoutes){
                                            route.remove();
                                        }
                                        realRoutes.clear();
                                    }
                                    if(realRoutesPos.size()!=0) realRoutesPos.clear();
                                    if(selectedRoutesVehicle.size()!=0) selectedRoutesVehicle.clear();
                                    //end of clearing


                                    //running the solution
                                    timeLimitInSeconds = Integer.parseInt(input.toString())*1000;
                                    progress.setMax(timeLimitInSeconds/1000);
                                    timeLimitInSeconds = Integer.parseInt(input.toString());
                                    if(vrpSolverTask != null) {
                                        if (!vrpSolverTask.isRunning()) {
                                            optimizeRoutes();
                                        }
                                    }else{
                                        optimizeRoutes();
                                    }
                                }
                            }).show();
                }else if(position == 3){

                    Map<String, String> destination = new HashMap<>(); //destination of every vehicle
                    int key, inc = 0;
                    long inc1 = 0, size = 0;
                    StringBuilder sb = new StringBuilder();

                    //realRoutesPos   key -> vehicle   value -> route id
                    for(int i = 0; i < realRoutesPos.size(); i++){
                        key = realRoutesPos.keyAt(i); //vehicle

                        for(int pos: realRoutesPos.get(key)){ //arraylist of position
                            sb.append(key); //vehicle
                            sb.append("::");
                            sb.append(realRoutes.get(pos).getColor()); //color
                            sb.append("::");
                            sb.append(realRouteCustomerPos.get(key).get(inc)); //customer Position
                            sb.append("::");

                            size = realRoutes.get(pos).getPoints().size();

                            for(LatLng posLatLng: realRoutes.get(pos).getPoints()){

                                sb.append(posLatLng.latitude); //latitude
                                sb.append("#");
                                sb.append(posLatLng.longitude); //longitude

                                if (size-1!=inc1) sb.append("&");
                                ++inc1;
                            }
                            sb.append("<endPath>");
                            ++inc;
                            inc1 = 0;
                        }
                        inc = 0;
                        if(i!=realRoutesPos.size()-1) sb.append("<div>");

                        destination.put("ID "+Integer.toString(key), sb.toString());
                        sb.setLength(0); //clear
                    }

                    //save data from delivery object
                    Map<String, _Transaction> mTransaction = new HashMap<>();
                    for(_Transaction transaction : addDelivery_step1.addedTransaction){
                        mTransaction.put(transaction.getTransactionID(), transaction);
                    }

                    Map<String, _Vehicles> vehicle = new HashMap<>();
                    for(int i = 0; i < selectedVehicle.size(); i++){
                        if(selectedVehicle.get(i))
                            vehicle.put("ID "+Integer.toString(i), arrVehicles.get(i));
                    }

                    delivery.setTransaction(mTransaction); //key -> (latitude, longitude)  value -> transaction
                    delivery.setRealRoutes(destination);
                    delivery.setVehicle(vehicle);
                    delivery.setVehicleTransaction(vehicleTransaction);

                    Intent i = new Intent(getApplicationContext(), addDelivery_step5_COURIER.class);
                    startActivity(i);
                }
                return true;
            }
        });

        //(2)this is needed to create google maps
        SupportMapFragment mapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFrag.getMapAsync(this);
        //end(2)

        //(1)declaration
        progress = findViewById(com.thesis.tipqc.ars_delivery.R.id.progress);

        location = addDelivery_step1.location;

        //downloading Google Maps Data(traffic, distance, etc)
        downloadingGoogleData(0,0);

        //show dialog while downloading data
        boolean showMinMax = true;
         dialog = new MaterialDialog.Builder(this)
                .title("Google Maps Data")
                .content("Downloading data")
                .progress(false, d.matrixSize, showMinMax)
                 .cancelable(false)
                .show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //this is for initialization
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 10.5f));
        mMap.setOnMarkerClickListener(this);

        //get the customer address via latitude and longitude
        final int size = addDelivery_step1.location.size();
        for(int i = 0; i < size; i++){
            String keyLocation = location.get(i).latitude+"_"+location.get(i).longitude;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location.get(i));
            if(i!=0) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                savedInfo.put(keyLocation, addDelivery_step1.addedTransaction.get(i - 1));
            }else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
            Marker marker = mMap.addMarker(markerOptions);
            arrMarker.put(keyLocation, marker);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
       String key = marker.getPosition().latitude+"_"+marker.getPosition().longitude;
        if(savedInfo.containsKey(key)) {
            if (marker.equals(arrMarker.get(key))) {
                if(arrOrders.size()!=0) arrOrders.clear();

                String customerKey = savedInfo.get(key).getCustomer().keySet().toArray()[0].toString();
                String customerName = savedInfo.get(key).getCustomer().get(customerKey).getFirstName()+" "
                        +savedInfo.get(key).getCustomer().get(customerKey).getLastName();

                for(Map.Entry<String, _Orders> entry: savedInfo.get(key).getOrders().entrySet()){
                    arrOrders.add(entry.getValue());
                }

                Set<String> orderKey = savedInfo.get(key).getOrders().keySet();

                int inc = 1;
                if(orderKey.size() != 0) {
                    for (String orders : orderKey) {
                        String quantity = savedInfo.get(key).getOrders().get(orders).getQuantity();
                        String orderID = savedInfo.get(key).getOrders().get(orders).getOrderID();

                        String productKey = savedInfo.get(key).getOrders().get(orders).getProducts().keySet().toArray()[0].toString();
                        _Products product = savedInfo.get(key).getOrders().get(orders).getProducts().get(productKey);
                        ++inc;
                    }
                }

                RecyclerView.Adapter adapter  = new recyclerAdapter_orders();
                new MaterialDialog.Builder(this)
                        .title("Customer")
                        .content("Name: "+customerName)
                        .positiveText("BACK")
                        // second parameter is an optional layout manager. Must be a LinearLayoutManager or GridLayoutManager.
                        .adapter(adapter, null)
                        .show();
            }
        }

        return false;
    }


    public void optimizeRoutes(){
        try {
            File folder = getExternalFilesDir("myFolder");
            File fileName = new File(folder, "A-n32-k2.vrp");

            /*GET THE FILE*/
            FileInputStream cvrpFile = new FileInputStream(fileName);
            /* PARSE THE FILE*/
            vrs = (VehicleRoutingSolution)VehicleRoutingImporter.readSolution(
                    "A-n32-k2.vrp", cvrpFile);
            vrpSolverTask = new VrpSolverTask(this, timeLimitInSeconds, "vrpBranchAndBoundSolverConfig.xml");
            progressBarTask = new ProgressBarTask(this);

            //solve the problem
            progressBarTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, timeLimitInSeconds);
            vrpSolverTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, vrs);

            cvrpFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class data{
        final String key = "AIzaSyA_Vj68cTwVJVDQiht3Aw4-sBh8QRvMvTw";
        int size = addDelivery_step1.location.size();

        ArrayList<Double> durationTraffic = new ArrayList<>();
        ArrayList<Double> distance = new ArrayList<>();
        Map<String, ArrayList<LatLng>> arrBestWay = new HashMap<>();
        ArrayList<String> keySet = new ArrayList<>();
        ArrayList<LatLng> bestWay = new ArrayList<>();
        ArrayList<LatLng> directionPositionList;
        List<LatLng> location = addDelivery_step1.location;

        private LatLng origins, destinations;
        private int x = 0, y = 0, total = 0, matrixSize = size*size;
        int bestTimePosition;

        public data(){}
    }


    public void downloadingGoogleData(int x, int y){
        d.origins = d.location.get(x);
        d.destinations = d.location.get(y);

        //get more information (traffic duration etc)
        String a = "" + System.currentTimeMillis() / 1000;
        String key = d.origins.latitude + "_" + d.origins.longitude + "-" + d.destinations.latitude
                + "_" + d.destinations.longitude;
        d.keySet.add(key);

        GoogleDirection.withServerKey(d.key)
                .from(d.origins)
                .to(d.destinations)
                .transportMode(TransportMode.DRIVING) //driving only
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.TOLLS) //avoid tolls for improving the cheapest path
                .alternativeRoute(true) //this gets the fastest route that we can get
                .departureTime(a)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            int best = 1000000000;
                            for (int i = 0; i < direction.getRouteList().size(); i++) {
                                Route route = direction.getRouteList().get(i);

                                d.directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                Info traffic = route.getLegList().get(0).getDurationInTraffic();

                                //find the best route depending on the travel duration
                                if (Integer.parseInt(traffic.getValue()) < best) {
                                    best = Integer.parseInt(traffic.getValue());
                                    d.bestTimePosition = i; //get the best alternative path
                                    d.bestWay = d.directionPositionList;
                                }
                            }

                            d.durationTraffic.add(Double.parseDouble(direction.getRouteList().get(d.bestTimePosition).getLegList().get(0).getDurationInTraffic().getValue()));
                            d.distance.add(Double.parseDouble(direction.getRouteList().get(d.bestTimePosition).getLegList().get(0).getDistance().getValue()));
                            String key = d.origins.latitude+"_"+d.origins.longitude+"-"+d.destinations.latitude+"_"+d.destinations.longitude;
                            d.arrBestWay.put(key, d.bestWay);

                            if(d.y!=d.size-1) {
                                d.total++;
                                dialog.incrementProgress(1);
                                d.y++;
                                downloadingGoogleData(d.x,d.y);
                            }else{
                                if(d.x!=d.size-1){
                                    d.total++;
                                    dialog.incrementProgress(1);
                                    d.y = 0;
                                    d.x++;
                                    downloadingGoogleData(d.x,d.y);
                                }else{
                                    dialog.incrementProgress(1);
                                    dialog.dismiss();

                                    //vehicle selection
                                    retrieveVehicleKeyFromDatabase();
                                }
                            }
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });
        }


        public void drawTemporaryRoutes(){
            int vehicleNo = 0;
            for(Vehicle vehicle: vrs.getVehicleList()){
                int position = 1;
                Customer vehicleCustomer = null;
                int longestDistanceDepot = -1;
                realRouteCustomerPos.put(vehicle.getId().intValue(), new ArrayList<Integer>());

                //draw routes
                for(Customer customer: vrs.getCustomerList()){
                    if(customer.getPreviousStandstill() != null && customer.getVehicle() == vehicle) {
                        Location previousLocation = customer.getPreviousStandstill().getLocation();
                        Location location = customer.getLocation();

                        IconGenerator tc = new IconGenerator(this);
                        tc.setColor(COLOR[vehicleNo%7]);
                        Bitmap bmp = tc.makeIcon(""+position); // pass the text you want.
                        arrMarker.get(location.getLatitude()+"_"+location.getLongitude()).setIcon(BitmapDescriptorFactory.fromBitmap(bmp));

                        PolylineOptions line=
                                new PolylineOptions().add(new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude()),
                                        new LatLng(location.getLatitude(), location.getLongitude())).width(5).color(Color.RED);
                        tempRoutes.add(mMap.addPolyline(line));

                        realRouteCustomerPos.get(vehicle.getId().intValue()).add(position);

                        int distance = customer.getDistanceFromPreviousStandstill();

                        if(customer.getPreviousStandstill() instanceof Customer) {
                            if(longestDistanceDepot < distance) {
                                longestDistanceDepot = distance;
                                vehicleCustomer = customer;
                            }
                        } else if (vehicleCustomer == customer) {
                            vehicleCustomer = customer;
                        }

                        //draw route back from the depot
                        if(customer.getNextCustomer() == null) {
                            Location vehicleLocation = vehicle.getLocation();

                            PolylineOptions line1=
                                    new PolylineOptions().add(new LatLng(location.getLatitude(), location.getLongitude()),
                                            new LatLng(vehicleLocation.getLatitude(), vehicleLocation.getLongitude())).width(5).color(Color.BLUE);
                            tempRoutes.add(mMap.addPolyline(line1));
                            realRouteCustomerPos.get(vehicle.getId().intValue()).add(position);
                        }
                        ++position;
                    }
                }
                ++vehicleNo;
            }
        }


    public void drawRealRoutes(){
        for(Vehicle vehicle: vrs.getVehicleList()){
            Customer vehicleCustomer = null;
            int longestDistanceDepot = -1;
            int load = 0;

            realRoutesPos.put(vehicle.getId().intValue(), new ArrayList<Integer>());
            //draw routes
            for(Customer customer: vrs.getCustomerList()){
                Toast.makeText(getApplicationContext(), ""+vrs.getCustomerList().size(), Toast.LENGTH_LONG).show();
                if(customer.getPreviousStandstill() != null && customer.getVehicle() == vehicle) {
                    load += customer.getDemand();
                    Location previousLocation = customer.getPreviousStandstill().getLocation();
                    Location location = customer.getLocation();

                    String keyLocation = location.getLatitude()+"_"+location.getLongitude();

                    if(savedInfo.containsKey(keyLocation))
                        vehicleTransaction.put(savedInfo.get(keyLocation).getTransactionID(), Long.toString(vehicle.getId()));

                    String prevLat = Double.toString(previousLocation.getLatitude()),
                            prevLot = Double.toString(previousLocation.getLongitude()),
                            nextLat = Double.toString(location.getLatitude()),
                            nextLot = Double.toString(location.getLongitude());

                    String key = prevLat+"_"+prevLot+"-"+nextLat+"_"+nextLot;


                    if(d.arrBestWay.containsKey(key)){
                        realRoutes.add( mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), d.arrBestWay.get(key), 5, Color.RED)));
                        realRoutesPos.get(vehicle.getId().intValue()).add(realRoutes.size()-1);
                    }

                    int distance = customer.getDistanceFromPreviousStandstill();

                    if(customer.getPreviousStandstill() instanceof Customer) {
                        if(longestDistanceDepot < distance) {
                            longestDistanceDepot = distance;
                            vehicleCustomer = customer;
                        }
                    } else if (vehicleCustomer == customer) {
                        vehicleCustomer = customer;
                    }

                    //draw route back from the depot
                    if(customer.getNextCustomer() == null) {
                        Location vehicleLocation = vehicle.getLocation();
                        String key2 = nextLat+"_"+nextLot+"-"+vehicleLocation.getLatitude()+"_"+vehicleLocation.getLongitude();

                        if(d.arrBestWay.containsKey(key2)){
                            realRoutes.add(mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), d.arrBestWay.get(key2), 5, Color.BLUE)));
                            realRoutesPos.get(vehicle.getId().intValue()).add(realRoutes.size()-1);
                        }
                    }

                }
            }

        }
    }



    private void adapterUpdate(int usage){
        if(adapter.getItemCount()!=0) adapter.clear();
        selectedVehicleSize = 0;

        int colorBackground;
        int selectedImage = -1;
        String contents;
        String vehicleName, volume;
        boolean isSelected;

        for(int i = 0; i < arrVehicles.size(); i++){
            if(usage==0) isSelected = selectedVehicle.get(i);
            else isSelected = !(selectedRoutesVehicle.get(i));

            if (isSelected) {
                colorBackground = Color.RED;
                ++selectedVehicleSize;
            } else {
                colorBackground = Color.WHITE;
            }

            if(arrVehicles.get(i).getType().equals("Car"))  selectedImage = R.drawable.suv;
            else if(arrVehicles.get(i).getType().equals("Scooter")) selectedImage = R.drawable.vespa;
            else if(arrVehicles.get(i).getType().equals("Truck")) selectedImage = R.drawable.delivery_truck;
            else if(arrVehicles.get(i).getType().equals("Van"))  selectedImage = R.drawable.trucking;
            else if(arrVehicles.get(i).getType().equals("Motorcycle")) selectedImage = R.drawable.motor_sports;

            vehicleName = arrVehicles.get(i).getVehicleName();
            volume = arrVehicles.get(i).getVolumeCapacity();

            contents = vehicleName+"\n"+volume;
            MaterialSimpleListItem ms = new MaterialSimpleListItem.Builder(this)
                    .content(contents)
                    .backgroundColor(colorBackground)
                    .icon(selectedImage)
                    .build();

            if(usage==1) {
                if (selectedVehicle.valueAt(i)) {
                    adapter.add(ms);
                }
            }else if(usage == 0) adapter.add(ms);
        }

    }

    private void vehicleSelection(){
        totalLoad = 0;
         adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                View positive = dialog.getActionButton(DialogAction.POSITIVE);
                if(selectedVehicle.indexOfKey(index)!=-1){
                    if(selectedVehicle.valueAt(index)) selectedVehicle.put(index, false);
                    else selectedVehicle.put(index, true);
                }else
                    selectedVehicle.put(index, true);

                //get the total demand(refreshes after click)
                for(int i = 0; i < arrVehicles.size(); i++){
                    if(selectedVehicle.get(i)){
                        totalLoad +=Double.parseDouble(arrVehicles.get(i).getVolumeCapacity());
                    }
                }
                currentLoad = addDelivery_step1.totalDemand - totalLoad;
                totalLoad = 0;

                if(currentLoad<0) {
                    currentLoad = 0;
                    positive.setEnabled(true);
                }else positive.setEnabled(false);


                adapterUpdate(0);
                adapter.notifyDataSetChanged();

                dialog.setContent("Needed Load: "+currentLoad);
            }
        });


       adapterUpdate(0);
       MaterialDialog m = new MaterialDialog.Builder(this)
                .title("Vehicle Selection")
                .content("Selected")
                .dividerColor(Color.BLACK)
               .adapter(adapter, null)
                .positiveText("CONTINUE")
                .negativeText("CANCEL")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //save data from the text file for later use
                        saveCVRPFile("yes", "maybe");
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
        m.getActionButton(DialogAction.POSITIVE).setEnabled(false);

    }


    private void showSelectedRoutes(){
        adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                if(selectedRoutesVehicle.indexOfKey(index)!=-1){
                    if(selectedRoutesVehicle.valueAt(index)){
                        selectedRoutesVehicle.put(index,false);
                    }
                    else{
                        selectedRoutesVehicle.put(index,true);
                    }
                }else{
                    selectedRoutesVehicle.put(index,true);
                }

                adapterUpdate(1);
                adapter.notifyDataSetChanged();
            }
        });


        adapterUpdate(1);
        new MaterialDialog.Builder(this)
                .title("Vehicle Routes Selection")
                .adapter(adapter, null)
                .positiveText("CONTINUE")
                .negativeText("CANCEL")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        for(int i = 0; i < selectedRoutesVehicle.size(); i++){
                            if(selectedRoutesVehicle.valueAt(i)){
                                int key = selectedRoutesVehicle.keyAt(i);
                                for(int j = 0; j < realRoutesPos.get(key).size(); j++){
                                    realRoutes.get(realRoutesPos.get(key).get(j)).setVisible(false);
                                }
                            }else{
                                int key = selectedRoutesVehicle.keyAt(i);
                                for(int j = 0; j < realRoutesPos.get(key).size(); j++){
                                    realRoutes.get(realRoutesPos.get(key).get(j)).setVisible(true);
                                }
                            }
                        }

                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void retrieveVehicleKeyFromDatabase() {
        if(arrVehicles.size()!=0)
            arrVehicles.clear();

        String businessKey = ownerLogin.business.getBusinessKey();
        ref = database.getReference("Business/"+businessKey);
        ref.child("Vehicles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0) {
                    for(DataSnapshot vehicleDataSnapshot: dataSnapshot.getChildren()){
                        _Vehicles vehicle = vehicleDataSnapshot.getValue(_Vehicles.class);
                        arrVehicles.add(vehicle);
                    }

                    //show dialog for vehicle Selection
                    vehicleSelection();
                }else{
                    Toast.makeText(getApplicationContext(), "You have no vehicles yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*Checks if external storage is available for read and write*/
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public void saveCVRPFile(String name, String comment){
        if(isExternalStorageWritable()) {
            try {
                File folder = getExternalFilesDir("myFolder");
                if (!folder.exists()) folder.mkdir();
                File fileName = new File(folder, "A-n32-k2.vrp");
                if (!fileName.exists()) fileName.createNewFile();

                FileOutputStream cvrpFile = new FileOutputStream(fileName);

                    /*NAME*/
                cvrpFile.write((NAME + " : A-n32-k2").getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                    /*COMMENT*/
                cvrpFile.write((COMMENT + " : (" + comment + ")").getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                    /*TYPE*/
                cvrpFile.write((TYPE + " : CVRP").getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                //one transaction = one customer
                int transactionSize = addDelivery_step1.addedTransaction.size();
                double[] arrDemand = new double[transactionSize + 1];
                double demand = 0;

                    /*DIMENSION*/
                cvrpFile.write((DIMENSION + " : " + (transactionSize + 1)).getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                    /*EDGE WEIGHT TYPE*/
                cvrpFile.write((EDGE_WEIGHT_TYPE + " : EXPLICIT").getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                    /*EDGE WEIGHT FORMAT*/
                cvrpFile.write((EDGE_WEIGHT_FORMAT + ": FULL_MATRIX").getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                    /*EDGE WEIGHT UNIT OF MEASUREMENT*/
                cvrpFile.write((EDGE_WEIGHT_UNIT_OF_MEASUREMENT + " : sec").getBytes()); //CHANGE THIS
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                    /*CAPACITY*/
                cvrpFile.write((CAPACITY + " : 124").getBytes()); //CHANGE THIS
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                    /*NODE COORDINATE SECTION*/
                cvrpFile.write((NODE_COORD_SECTION).getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                double businessLat = ownerLogin.business.getLatitude();
                double businessLot = ownerLogin.business.getLongitude();
                cvrpFile.write((" 1" + " " + businessLat + " " + businessLot).getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());
                for (int i = 0; i < transactionSize; i++) {
                    int key = i + 2;
                    String customerKey = addDelivery_step1.addedTransaction.get(i).getCustomer().keySet().toArray()[0].toString();

                    double latitude = Double.parseDouble(addDelivery_step1.addedTransaction.get(i).getCustomer().get(customerKey).getLatitude());
                    double longitude = Double.parseDouble(addDelivery_step1.addedTransaction.get(i).getCustomer().get(customerKey).getLongitude());

                    Object[] orderKey = addDelivery_step1.addedTransaction.get(i).getOrders().keySet().toArray();
                    for(Object j: orderKey)
                        demand += Integer.parseInt(addDelivery_step1.addedTransaction.get(i).getOrders().get(j.toString()).getQuantity());


                    arrDemand[i] = demand;
                    demand = 0;

                    //coordinates of customers and their key
                    cvrpFile.write((" " + key + " " + latitude + " " + longitude + " " + customerKey).getBytes()); //OPTIONAL = ADD DESTINATION ADDRESS
                    cvrpFile.write(System.getProperty("line.separator").getBytes());
                }
                //start
                int inc = 0;
                cvrpFile.write((EDGE_WEIGHT_SECTION).getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());
                for (int i = 0; i < d.size; i++) {
                    for (int j = 0; j < d.size; j++) {
                        cvrpFile.write((d.distance.get(inc) + " ").getBytes());
                        inc++;
                    }
                    cvrpFile.write(System.getProperty("line.separator").getBytes());
                }


                    /*DEMAND SECTION*/
                cvrpFile.write((DEMAND_SECTION).getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());
                cvrpFile.write(("1 0").getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                for (int i = 0; i < transactionSize; i++) {
                    int key = i + 2;
                    cvrpFile.write((key + " " + (int) (arrDemand[i] + 30)).getBytes());
                    cvrpFile.write(System.getProperty("line.separator").getBytes());
                }

                    /*DEPOT SECTION*/
                cvrpFile.write((DEPOT_SECTION).getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());
                cvrpFile.write((" 1").getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());
                cvrpFile.write((" -1").getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());

                    /*VEHICLE SECTION */
                cvrpFile.write((VEHICLES + ": " + selectedVehicleSize).getBytes());
                cvrpFile.write(System.getProperty("line.separator").getBytes());
                for (int i = 0; i < arrVehicles.size(); i++) {
                    if (selectedVehicle.valueAt(i)) {
                        cvrpFile.write((i + " " + arrVehicles.get(i).getVolumeCapacity()).getBytes());
                        cvrpFile.write(System.getProperty("line.separator").getBytes());
                    }
                }
                cvrpFile.write((EOF).getBytes()); //end the file

                cvrpFile.close();


                    /*REFRESH STORAGE*/
                MediaScannerConnection.scanFile(this,
                        new String[]{folder.toString()},
                        null,
                        null);

                MediaScannerConnection.scanFile(this,
                        new String[]{fileName.toString()},
                        null,
                        null);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{

            new MaterialDialog.Builder(this)
                    .title("Warning")
                    .content("Cannot Proceed. There is a problem with your external storage")
                    .cancelable(false)
                    .positiveText("BACK TO MAIN MENU")
                    .show();
        }
    }

}
