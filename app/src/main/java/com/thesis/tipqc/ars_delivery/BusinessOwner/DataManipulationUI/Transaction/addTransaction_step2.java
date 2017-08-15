package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Transaction;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Orders;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Products;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_products;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class addTransaction_step2 extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private String businessKey;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    private Button btnNext;

    public static ArrayList<_Products> arrProducts = new ArrayList<>();
    public static ArrayList<_Orders> arrOrders = new ArrayList<>();

    public ArrayList<String> productsID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction_step2);

        btnNext = (Button)findViewById(R.id.btnNext);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        businessKey = _OBJECTS.session.getBusinessKey();
        retrieveProductDatabase();

        adapter  = new recyclerAdapter_products();
        recyclerView.setAdapter(adapter);

    }

    public void click(View v){
        if(v.equals(btnNext)){
            saveInformation();

            //go the main UI
            _OBJECTS.session.setCurrentActivity("Transaction");
            Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            //end
        }
    }

    //save information without the need of authentication
    private void saveInformation(){
        String businessKey = _OBJECTS.session.getBusinessKey();
        ref = database.getReference("Business/"+businessKey);
        Map<String, _Orders> mOrder = new HashMap<>();

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String transactionID = ref.push().getKey();

        addTransaction_step1.transaction.setDateBought(currentDateTimeString);
        addTransaction_step1.transaction.setTransactionID(transactionID);

        //Orders save within the transaction child
        int size = arrOrders.size();
        String[] key1 = new String[size];
        for(int i = 0; i < size; i++){
            key1[i] = ref.push().getKey();
            arrOrders.get(i).setOrderID(key1[i]);

            mOrder.put(arrOrders.get(i).getOrderID(), arrOrders.get(i));

        }
        addTransaction_step1.transaction.setOrders(mOrder);

        ref.child("Transaction").child(transactionID).setValue(addTransaction_step1.transaction);
    }

    private void retrieveProductDatabase() {
        if(arrProducts.size()!=0)
            arrProducts.clear();

        String businessKey = _OBJECTS.session.getBusinessKey();
        ref = database.getReference("Business/"+businessKey+"/Products");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0) {
                    for(DataSnapshot productDataSnapShot: dataSnapshot.getChildren()){
                        _Products products = productDataSnapShot.getValue(_Products.class);

                        arrProducts.add(products);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "You have no products yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
