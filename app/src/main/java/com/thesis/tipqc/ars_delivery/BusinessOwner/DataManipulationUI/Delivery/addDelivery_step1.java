package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Orders;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Transaction;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_transaction;

import java.util.ArrayList;
import java.util.Map;


public class addDelivery_step1 extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    private Button btnNext;

    public static ArrayList<_Transaction> arrTransaction;
    public static ArrayList<_Transaction> addedTransaction;
    public static ArrayList<LatLng> location;
    public static SparseBooleanArray isSelected;
    public static double totalDemand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_step1);

        btnNext = (Button)findViewById(R.id.btnNext);
        recyclerView =(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);


        addedTransaction = new ArrayList<>();
        location = new ArrayList<>();
        isSelected = new SparseBooleanArray();
        arrTransaction = new ArrayList<>();
        totalDemand = 0;

        retrieveTransactionFromDatabase();
        adapter  = new recyclerAdapter_transaction();
        recyclerView.setAdapter(adapter);


        location.add(new LatLng(ownerLogin.business.getLatitude(), ownerLogin.business.getLongitude()));
    }



    public void click(View v){
        if(v.equals(btnNext)){
            for(int i = 0; i < arrTransaction.size(); i++){
                if(isSelected.get(i)){
                        //add transaction to the arrayList coming from Add Delivery Step1 class
                        addedTransaction.add(arrTransaction.get(i));

                        String key = arrTransaction.get(i).getCustomer().keySet().toArray()[0].toString();

                        location.add(new LatLng(Double.parseDouble(arrTransaction.get(i).getCustomer().get(key).getLatitude()),
                                Double.parseDouble(arrTransaction.get(i).getCustomer().get(key).getLongitude())));

                        for (Map.Entry<String, _Orders> orders : arrTransaction.get(i).getOrders().entrySet()) {
                            totalDemand += Double.parseDouble(orders.getValue().getQuantity());
                        }
                    }

            }

            //go to addDelivery step2 (Adding of vehicles)
            Intent iDelivery = new Intent(getApplicationContext(), addDelivery_step4_MAPS.class);
            startActivity(iDelivery);
        }
    }


    private void retrieveTransactionFromDatabase() {
        if (arrTransaction.size() != 0)
            arrTransaction.clear();
        String businessKey = ownerLogin.business.getBusinessKey();
        ref = database.getReference("Business/"+businessKey+"/Transaction");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0) {

                    //Get map of transaction in
                    for(DataSnapshot transactData: dataSnapshot.getChildren()){
                        _Transaction t = transactData.getValue(_Transaction.class);
                        arrTransaction.add(t);
                    }

                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "You have no transactions yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}
