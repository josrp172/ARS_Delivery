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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_orders;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Orders;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Products;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class orders_main extends Fragment {
    RecyclerView recyclerView;
    private RecyclerViewHeader recyclerHeader;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    public static ArrayList<_Orders> arrOrders = new ArrayList<>();

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_orders_main,container,false);

        recyclerView = myView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(myView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerHeader = (RecyclerViewHeader) myView.findViewById(R.id.header);
        recyclerHeader.attachTo(recyclerView);
        retrieveOrders();
        adapter  = new recyclerAdapter_orders();
        recyclerView.setAdapter(adapter);



        return myView;
    }

    private void retrieveOrders() {
        if(arrOrders.size()!=0)
            arrOrders.clear();

        String businessKey = ownerLogin.business.getBusinessKey();
        String transactionID = getArguments().getString("transactionID");
        ref = database.getReference("Business/"+businessKey+"/Transaction/"+transactionID+"/orders");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0) {
                    for(DataSnapshot orderDataSnapShot: dataSnapshot.getChildren()){
                        _Orders dOrder = orderDataSnapShot.getValue(_Orders.class);

                        arrOrders.add(dOrder);
                    }

                    adapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(myView.getContext(), "You have no orders yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




}
