package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_customer;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Customer;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class customer_main extends Fragment{

    public static RecyclerView recyclerView;
    private RecyclerViewHeader recyclerHeader;
    RecyclerView.LayoutManager layoutManager;
    public static RecyclerView.Adapter adapter;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    public static ArrayList<_Customer> arrCustomer = new ArrayList<>();
    public static ArrayList<_Customer> origContainer = new ArrayList<>();
    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_customer_main,container,false);
        recyclerView = myView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(myView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerHeader = (RecyclerViewHeader) myView.findViewById(R.id.header);
        recyclerHeader.attachTo(recyclerView);
        retrieveCustomerDatabase();
        adapter  = new recyclerAdapter_customer();
        recyclerView.setAdapter(adapter);
        return myView;

    }

    private void retrieveCustomerDatabase() {
        if(arrCustomer.size()!=0)
            arrCustomer.clear();

        String businessKey = ownerLogin.business.getBusinessKey();
        ref = database.getReference("Business/"+businessKey+"/Customer");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0) {
                    for(DataSnapshot custData: dataSnapshot.getChildren()){
                        _Customer c = custData.getValue(_Customer.class);

                        arrCustomer.add(c);
                    }
                    origContainer =  new ArrayList<>(arrCustomer);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(myView.getContext(), "You have no customers yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
