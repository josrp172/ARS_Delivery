package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Courier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._DeliveryPersonnel;

public class addDeliveryPersonnel_step1 extends AppCompatActivity {
    private EditText txtEmail, txtPassword, txtFirstName, txtLastName, txtMobileNum, txtAddress;
    private Button btnNext;
    String email,password,firstName,lastName,mobileNum,address;
    public static _DeliveryPersonnel courier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_personnel_step1);

        //initialization
        txtEmail = (EditText)findViewById(R.id.lblEmailMain);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtFirstName = (EditText)findViewById(R.id.txtFirstName);
        txtLastName = (EditText)findViewById(R.id.txtLastName);
        txtMobileNum = (EditText)findViewById(R.id.txtMobileNum);
        txtAddress = (EditText)findViewById(R.id.txtAddress);
        btnNext = (Button)findViewById(R.id.btnNext);

        courier = new _DeliveryPersonnel();
    }

    public void click(View v){
        if(validate())
        {
            if(v.equals(btnNext)){
                String email = txtEmail.getText().toString(),
                        password = txtPassword.getText().toString(),
                        firstName = txtFirstName.getText().toString(),
                        lastName = txtLastName.getText().toString(),
                        mobileNum = txtMobileNum.getText().toString(),
                        address = txtAddress.getText().toString();

                courier.setEmail(email);
                courier.setPassword(password);
                courier.setFirstName(firstName);
                courier.setLastName(lastName);
                courier.setMobilePhone(mobileNum);
                courier.setAddress(address);

                //go to step 2 (set picture of courier)
                Intent i = new Intent(getApplicationContext(), addDeliveryPersonnel_step2.class);
                startActivity(i);
                //end
            }
        }
        else
        {
            Toast.makeText(this,"Add delivery personnel failed!",Toast.LENGTH_SHORT).show();
        }
    }

    public void initialize()
    {
        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
        firstName = txtFirstName.getText().toString().trim();
        lastName = txtLastName.getText().toString().trim();
        mobileNum = txtMobileNum.getText().toString().trim();
        address = txtAddress.getText().toString().trim();
    }
    public boolean validate()
    {   initialize();
        boolean valid = true;
        if(email.isEmpty() | !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            txtEmail.setError("Please enter a valid email address");
            valid = false;
        }
        if(password.isEmpty() | password.length() > 15)
        {
            txtPassword.setError("Please enter a valid password");
            valid = false;
        }
        if(firstName.isEmpty() | firstName.length() > 15)
        {
            txtFirstName.setError("Please enter a valid first name");
            valid = false;
        }
        if(lastName.isEmpty() | lastName.length() > 15)
        {
            txtLastName.setError("Please enter a valid last name");
            valid = false;
        }
        if(mobileNum.isEmpty() | mobileNum.length() > 14)
        {
            txtMobileNum.setError("Please enter a valid mobile number");
            valid = false;
        }
        if(address.isEmpty())
        {
            txtAddress.setError("Please enter a valid address");
            valid = false;
        }
        return valid;
    }

}
