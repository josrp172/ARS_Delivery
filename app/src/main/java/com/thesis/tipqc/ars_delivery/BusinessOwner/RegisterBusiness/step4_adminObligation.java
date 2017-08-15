package com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.thesis.tipqc.ars_delivery.R;

public class step4_adminObligation extends AppCompatActivity {
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step4_admin_obligation);

        btnNext = (Button)findViewById(R.id.btnNext);

    }

    public void click(View v){
        if(v.equals(btnNext)){
            Intent i = new Intent(getApplicationContext(), step5_ownerRegistration.class);
            startActivity(i);
        }
    }
}
