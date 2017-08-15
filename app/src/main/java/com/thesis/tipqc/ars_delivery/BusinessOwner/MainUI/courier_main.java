package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Customer;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerViewCourier;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._DeliveryPersonnel;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;

import java.util.ArrayList;
import java.util.Map;

public class courier_main extends Fragment {
    RecyclerView recyclerView;
    private RecyclerViewHeader recyclerHeader;
    RecyclerView.LayoutManager layoutManager;
    public static RecyclerView.Adapter adapter;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;

    public static ArrayList<_DeliveryPersonnel> arrCourier;
    public static ArrayList<_DeliveryPersonnel> origContainer = new ArrayList<>();

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_courier_main,container,false);

        recyclerView = myView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        //layoutManager = new LinearLayoutManager(myView.getContext());
        layoutManager = new GridLayoutManager(myView.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerHeader = (RecyclerViewHeader) myView.findViewById(R.id.header);
        recyclerHeader.attachTo(recyclerView);
        recyclerView.setHasFixedSize(true);

        arrCourier = new ArrayList<>();
        retrieveCourierFromDatabase();

        adapter  = new recyclerViewCourier();
        recyclerView.setAdapter(adapter);

        return myView;
    }

    private void retrieveCourierFromDatabase() {
        String businessKey = ownerLogin.business.getBusinessKey();
        ref = database.getReference("Delivery Personnel");

        Query query = ref.orderByChild("businessKey").equalTo(businessKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0) {
                    for(DataSnapshot courierDataSnapshot: dataSnapshot.getChildren()){
                        _DeliveryPersonnel courier = courierDataSnapshot.getValue(_DeliveryPersonnel.class);
                        arrCourier.add(courier);
                    }
                    origContainer =  new ArrayList<>(arrCourier);
                    adapter.notifyDataSetChanged();
                }else{
                    //update the adapter
                    Toast.makeText(myView.getContext(), "You have no couriers yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
