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
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Products;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerAdapter_products;

import java.util.ArrayList;
import java.util.Map;

public class products_main extends Fragment {
    RecyclerView recyclerView;
    private RecyclerViewHeader recyclerHeader;
    RecyclerView.LayoutManager layoutManager;
    public static RecyclerView.Adapter adapter;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    public static ArrayList<_Products> arrProducts;
    public static ArrayList<_Products> origContainer;

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_products_main,container,false);

        recyclerView = myView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(myView.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerHeader = (RecyclerViewHeader) myView.findViewById(R.id.header);
        recyclerHeader.attachTo(recyclerView);
        String businessKey = ownerLogin.business.getBusinessKey();
        ref = database.getReference("Business/"+businessKey+"/Products");

        retriveProductsDatabase();
        adapter  = new recyclerAdapter_products();
        recyclerView.setAdapter(adapter);

        origContainer=arrProducts;
        arrProducts = new ArrayList<>();

        return myView;
    }

    private void retriveProductsDatabase() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0) {
                    for(DataSnapshot productsSnapshot: dataSnapshot.getChildren()) {
                        _Products p = productsSnapshot.getValue(_Products.class);
                        arrProducts.add(p);
                    }

                }else{
                    Toast.makeText(myView.getContext(), "You have no products yet", Toast.LENGTH_LONG).show();
                }
                origContainer =  new ArrayList<>(arrProducts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
