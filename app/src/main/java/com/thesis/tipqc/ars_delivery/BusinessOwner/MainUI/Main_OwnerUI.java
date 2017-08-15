package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Courier.addDeliveryPersonnel_step1;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Customer.addCustomer_step1;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery.addDelivery_step1;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Product.addProducts_step1;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Transaction.addTransaction_step1;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Vehicle.addVehicles_step1;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_customer;
import com.thesis.tipqc.ars_delivery.R;

import java.util.ArrayList;


public class Main_OwnerUI extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView lblEmail, lblName;
    private ImageView imgProfilePic;
    public FloatingActionButton fab;
    private String currentActivity = "";
    private static String filter="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__owner_ui);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //added
        View header = navigationView.getHeaderView(0);

        //end
        lblEmail = header.findViewById(R.id.lblEmailMain);
        lblName = header.findViewById(R.id.lblNameMain);
        imgProfilePic = header.findViewById(R.id.imgProfilePicMain);

        lblName.setText(ownerLogin.owner.getFirstName()+" "+ownerLogin.owner.getLastName());
        lblEmail.setText(ownerLogin.owner.getEmailAddress());
        _OBJECTS.storageDatabase.getFileFromDatabase(getApplicationContext(),"User",ownerLogin.owner.getUserID(),imgProfilePic, null);

        if(_OBJECTS.session.getCurrentActivity().equals("")) {
            //do nothing
        }else{
            currentActivity = _OBJECTS.session.getCurrentActivity();
            Fragment fragment = null;
            if (currentActivity.equals("Delivery Personnel")) {
                fragment = new courier_main();
            } else if (currentActivity.equals("Products")) {
                fragment = new products_main();
            } else if (currentActivity.equals("Vehicles")) {
                fragment = new vehicle_main();
            }else if (currentActivity.equals("Transaction")){
                fragment = new transaction_main();
            }else if (currentActivity.equals("Delivery")){
                fragment = new delivery_main();
            }else if (currentActivity.equals("Customer")){
                fragment = new customer_main();
            }else if(currentActivity.equals("Main")){
                fragment = new Maps_main();
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.cdLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = null;
                if(currentActivity.equals("Products")){
                    i = new Intent(getApplicationContext(), addProducts_step1.class);
                }else if(currentActivity.equals("Vehicles")){
                    i = new Intent(getApplicationContext(), addVehicles_step1.class);
                }else if(currentActivity.equals("Delivery Personnel")){
                    i = new Intent(getApplicationContext(), addDeliveryPersonnel_step1.class);
                }else if(currentActivity.equals("Customer")){
                    i = new Intent(getApplicationContext(), addCustomer_step1.class);
                }else if(currentActivity.equals("Transaction")){
                    i = new Intent(getApplicationContext(), addTransaction_step1.class);
                }else if(currentActivity.equals("Delivery")){
                    i = new Intent(getApplicationContext(), addDelivery_step1.class);
                }
                startActivity(i);
            }
        });

        if(currentActivity.equals("Main"))  fab.hide();
        else fab.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main__owner_ui, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                currentActivity = _OBJECTS.session.getCurrentActivity();
                filter=s;

                if(currentActivity.equals("Customer"))
                {
                    customer_main.arrCustomer.clear();
                    if(filter.isEmpty())
                    {
                        customer_main.arrCustomer=  new ArrayList<>(customer_main.origContainer);
                    }
                    else
                    {
                        for(int i = 0;i < customer_main.origContainer.size();i++)
                        {
                            if((customer_main.origContainer.get(i).getFirstName().toLowerCase()).contains(filter.toLowerCase()))
                            {
                                customer_main.arrCustomer.add(customer_main.origContainer.get(i));
                            }
                        }
                    }

                    customer_main.recyclerView.removeAllViews();
                    customer_main.adapter.notifyDataSetChanged();
//                    customer_main.recyclerView.setAdapter(customer_main.adapter);
//                    Toast.makeText(Main_OwnerUI.this,"sample"+s,Toast.LENGTH_SHORT).show();
                }
                else if(currentActivity.equals("Products"))
                {
                    products_main.arrProducts.clear();
                    if(filter.isEmpty())
                    {
                        products_main.arrProducts = new ArrayList<>(products_main.origContainer);
                    }
                    else
                    {

                        for(int i = 0;i < products_main.origContainer.size();i++)
                        {
                            if((products_main.origContainer.get(i).getProductName().toLowerCase()).contains(filter.toLowerCase()))
                            {
                                products_main.arrProducts.add(products_main.origContainer.get(i));
                            }
                        }
                    }
                    products_main.adapter.notifyDataSetChanged();
                }
                else if(currentActivity.equals("Vehicles"))
                {
                    vehicle_main.arrVehicles.clear();
                    if(filter.isEmpty())
                    {
                        vehicle_main.arrVehicles = new ArrayList<>(vehicle_main.origContainer);
                    }
                    else
                    {
                        for(int i = 0;i < vehicle_main.origContainer.size();i++)
                        {
                            if((vehicle_main.origContainer.get(i).getVehicleName().toLowerCase()).contains(filter.toLowerCase()))
                            {
                                vehicle_main.arrVehicles.add(vehicle_main.origContainer.get(i));
                            }
                        }
                    }
                    vehicle_main.adapter.notifyDataSetChanged();
                }
                else if(currentActivity.equals("Delivery"))
                {
                    delivery_main.arrDelivery.clear();
                    if(filter.isEmpty())
                    {
                        delivery_main.arrDelivery = new ArrayList<>(delivery_main.origContainer);
                    }
                    else
                    {
                        for(int i = 0;i < delivery_main.origContainer.size();i++)
                        {
                            if((delivery_main.origContainer.get(i).toString().toLowerCase()).contains(filter.toLowerCase()))
                            {
                                delivery_main.arrDelivery.add(delivery_main.origContainer.get(i));
                            }
                        }
                    }

                    delivery_main.adapter.notifyDataSetChanged();
                }
                else if(currentActivity.equals("Delivery Personnel"))
                {
                    courier_main.arrCourier.clear();
                    if(filter.isEmpty())
                    {
                        courier_main.arrCourier = new ArrayList<>(courier_main.origContainer);
                    }
                    else
                    {
                        for(int i = 0;i < courier_main.origContainer.size();i++)
                        {
                            if((courier_main.origContainer.get(i).getFirstName().toLowerCase()).contains(filter.toLowerCase()))
                            {
                                courier_main.arrCourier.add(courier_main.origContainer.get(i));
                            }
                        }
                    }

                    courier_main.adapter.notifyDataSetChanged();
                }
                else if(currentActivity.equals("Transaction"))
                {
                    transaction_main.arrTransaction.clear();
                    if(filter.isEmpty())
                    {
                        transaction_main.arrTransaction = new ArrayList<>(transaction_main.origContainer);
                    }
                    else
                    {
                        for(int i = 0;i < transaction_main.origContainer.size();i++)
                        {
                            if((transaction_main.origContainer.get(i).toString().toLowerCase()).contains(filter.toLowerCase()))
                            {
                                transaction_main.arrTransaction.add(transaction_main.origContainer.get(i));
                            }
                        }
                    }

                    transaction_main.adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if(id == R.id.nav_main) {
            fragment = new Maps_main();
            currentActivity = "Main";
        }if (id == R.id.nav_delivery) {
            fragment = new delivery_main();
            currentActivity = "Delivery";
        } else if (id == R.id.nav_transaction) {
            fragment = new transaction_main();
            currentActivity = "Transaction";
        } else if (id == R.id.nav_customer) {
            fragment = new customer_main();
            currentActivity = "Customer";
        } else if (id == R.id.nav_products) {
            fragment = new products_main();
            currentActivity = "Products";
        } else if (id == R.id.nav_vehicles) {
            fragment = new vehicle_main();
            currentActivity = "Vehicles";
        }else if (id == R.id.nav_deliveryPersonnel) {
            fragment = new courier_main();
            currentActivity = "Delivery Personnel";
        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if(id == R.id.nav_logOut) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getApplicationContext(), ownerLogin.class);
           i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(i);
        }
        _OBJECTS.session.setCurrentActivity(currentActivity);

        if(currentActivity.equals("Main"))  fab.hide();
        else fab.show();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.cdLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    public static String getFilter()
    {
        if(filter==null)
        {
            filter="";
        }
        return filter;
    }
}
