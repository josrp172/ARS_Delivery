package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Transaction;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Customer;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Transaction;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_customer;

import java.util.ArrayList;
import java.util.Map;

public class addTransaction_step1 extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    private Button btnNext;
    public static ArrayList<_Customer> arrCustomer;
    public static _Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _OBJECTS.session.setCurrentActivity("Transaction");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction_step1);

        btnNext = (Button)findViewById(R.id.btnNext);

        recyclerView =(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        arrCustomer = new ArrayList<>();
        transaction = new _Transaction();

        retrieveCustomerDatabase();
        adapter  = new recyclerAdapter_customer();
        recyclerView.setAdapter(adapter);
    }

    public void click(View v){
        if(v.equals(btnNext)){
            //go to addTransaction step2 (Adding of orders)
            Intent iOrders = new Intent(getApplicationContext(), addTransaction_step2.class);
            startActivity(iOrders);
        }
    }



    private void retrieveCustomerDatabase() {
        if(arrCustomer.size()!=0)
            arrCustomer.clear();

        String businessKey = _OBJECTS.session.getBusinessKey();
        ref = database.getReference("Business/"+businessKey+"/Customer");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0) {
                    //Get map of users in
                    for(DataSnapshot custData: dataSnapshot.getChildren()){
                        _Customer c = custData.getValue(_Customer.class);
                        arrCustomer.add(c);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "You have no customers yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
