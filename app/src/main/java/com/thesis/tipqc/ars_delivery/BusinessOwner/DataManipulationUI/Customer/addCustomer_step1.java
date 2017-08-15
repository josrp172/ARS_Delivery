package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Customer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Customer;

public class addCustomer_step1 extends AppCompatActivity {
    private Button btnNext, btnCancel;
    private EditText txtFirstName, txtLastName, txtEmail, txtPhone;

    static _Customer customer;
    String firstN,lastN,emailAdd,phoneN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_step1);

        txtFirstName = (EditText)findViewById(R.id.txtFirstName);
        txtLastName = (EditText)findViewById(R.id.txtLastName);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPhone = (EditText)findViewById(R.id.txtPhone);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnCancel = (Button)findViewById(R.id.btnCancel);

        customer = new _Customer();
    }

    public void click(View v){
        if(validate())
        {
            if(v.equals(btnNext)){

                saveInformation();
                Intent i = new Intent(getApplicationContext(), addCustomer_step2.class);
                startActivity(i);

            }else if(v.equals(btnCancel)){

            }
        }
        else
        {
            Toast.makeText(this,"Add customer failed!",Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInformation(){
        String firstName = txtFirstName.getText().toString(),
                lastName = txtLastName.getText().toString(),
                email = txtEmail.getText().toString(),
                phone = txtPhone.getText().toString();

        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setMobilePhone(phone);
    }

    public void initialize()
    {
        firstN = txtFirstName.getText().toString().trim();
        lastN = txtLastName.getText().toString().trim();
        emailAdd = txtEmail.getText().toString().trim();
        phoneN = txtPhone.getText().toString().trim();
    }
    public boolean validate()
    {
        initialize();
        boolean valid = true;
        if(firstN.isEmpty() | firstN.length()> 14)
        {
            txtFirstName.setError("Please enter a valid first name");
            valid = false;
        }
        if(lastN.isEmpty() | lastN.length() > 14)
        {
            txtLastName.setError("Please enter a valid last name");
            valid = false;
        }
        if(emailAdd.isEmpty() | Patterns.EMAIL_ADDRESS.matcher(emailAdd).matches())
        {
            txtEmail.setError("Please enter a valid email address");
            valid = false;
        }
        if(phoneN.isEmpty() | phoneN.length() > 14)
        {
            txtPhone.setError("Please enter a valid phone number");
            valid = false;
        }
        return valid;
    }




}
