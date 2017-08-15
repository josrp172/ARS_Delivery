package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Customer;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Delivery;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_delivery;
import com.thesis.tipqc.ars_delivery.R;

import java.util.ArrayList;

public class delivery_main extends Fragment {

    RecyclerView recyclerView;
    private RecyclerViewHeader recyclerHeader;
    RecyclerView.LayoutManager layoutManager;
    public static RecyclerView.Adapter adapter;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    public static ArrayList<_Delivery> arrDelivery;
    public static ArrayList<_Delivery> origContainer = new ArrayList<>();
    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_delivery_main,container,false);

        recyclerView = myView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(myView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerHeader = (RecyclerViewHeader) myView.findViewById(R.id.header);
        recyclerHeader.attachTo(recyclerView);
        arrDelivery = new ArrayList<>();

        retrieveDeliveryFromDatabase();
        adapter  = new recyclerAdapter_delivery();
        recyclerView.setAdapter(adapter);

        return myView;
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
                    origContainer =  new ArrayList<>(arrDelivery);
                    adapter.notifyDataSetChanged();
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
