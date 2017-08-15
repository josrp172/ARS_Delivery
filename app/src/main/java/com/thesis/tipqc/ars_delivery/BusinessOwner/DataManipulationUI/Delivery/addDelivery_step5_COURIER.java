package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._DeliveryPersonnel;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter.recyclerViewCourier;
import com.thesis.tipqc.ars_delivery.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class addDelivery_step5_COURIER extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    Button btnNext;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;

    public static ArrayList<_DeliveryPersonnel> arrCourier;
    public static Map<String, _DeliveryPersonnel> dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_step5__courier);

        btnNext = (Button)findViewById(R.id.btnNext);
        recyclerView =(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        arrCourier = new ArrayList<>();
        dp = new HashMap<>();

        retrieveCourierFromDatabase();

        adapter  = new recyclerViewCourier();
        recyclerView.setAdapter(adapter);
    }



    public void onClick(View v){
        if(v.equals(btnNext)){
            saveInformation();
        }
    }


    private void retrieveCourierFromDatabase() {
        if(arrCourier.size()!=0)
            arrCourier.clear();
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
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "You have no couriers yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //save information without the need of authentication
    private void saveInformation(){
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Saving data")
                .content("Uploading the data....")
                .progress(true, 0)
                .cancelable(false)
                .show();

        String businessKey = _OBJECTS.session.getBusinessKey();
        ref = database.getReference("Business/"+businessKey);
        addDelivery_step4_MAPS.delivery.setDeliveryPersonnel(dp);

        ref.child("Delivery").push().setValue(addDelivery_step4_MAPS.delivery).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();

                    //go to main UI
                    Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    _OBJECTS.session.setCurrentActivity("Delivery");
                    startActivity(i);
                    //end
                }
            }
        });
    }
}
