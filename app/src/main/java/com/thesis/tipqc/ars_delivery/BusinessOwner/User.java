//select the user of the system (business owner, delivery personnel)
//use alt+enter
package com.thesis.tipqc.ars_delivery.BusinessOwner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.ownerLogin;
import com.thesis.tipqc.ars_delivery.R;

public class User extends AppCompatActivity {
    private Button btnOwner, btnDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btnOwner = (Button)findViewById(R.id.btnOwner);
        btnDelivery = (Button)findViewById(R.id.btnDelivery);

    }

    public void btnClick(View v){
        if(v.equals(btnDelivery)){
            //go to delivery personnel login/register page


        }else if(v.equals(btnOwner)){
            //go to owner login/register page
            Intent i = new Intent(getApplicationContext(), ownerLogin.class);
            startActivity(i);

        }
    }




}
