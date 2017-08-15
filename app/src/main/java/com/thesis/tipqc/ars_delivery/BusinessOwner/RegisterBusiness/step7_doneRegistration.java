package com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;

public class step7_doneRegistration extends AppCompatActivity {
    private Button btnDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step7_done_registration);

        btnDone = (Button)findViewById(R.id.btnDone);
    }

    public void click(View v){
        if(v.equals(btnDone)){
            Intent i = new Intent(getApplicationContext(), ownerLogin.class);
            //clear all the previous activity and start a new one
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            Toast.makeText(this,"Signup Success!",Toast.LENGTH_SHORT).show();
        }
    }
}
