package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Courier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;


public class addDeliveryPersonnel_DONE extends AppCompatActivity {
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_personnel__done);

        btnDone = (Button)findViewById(R.id.btnDone);
    }


    public void click(View v){
        if(v.equals(btnDone)){
            //go the main UI
            _OBJECTS.session.setCurrentActivity("Delivery Personnel");
            Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            Toast.makeText(this,"Add delivery personnel success!",Toast.LENGTH_SHORT).show();
            //end
        }
    }
}
