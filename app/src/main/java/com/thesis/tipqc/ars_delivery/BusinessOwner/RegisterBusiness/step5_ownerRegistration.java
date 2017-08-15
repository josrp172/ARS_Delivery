package com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Owner;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;

public class step5_ownerRegistration extends AppCompatActivity {
    private EditText txtUsername, txtPassword, txtLastName, txtFirstName, txtMobileNum, txtEmailAddress, txtAddress;
    private Button btnNext;

    public static _Owner owner = new _Owner();
    String userN,pass,lastN,firstN,mobileN,emailA,Add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step5_owner_registration);

        txtUsername = (EditText)findViewById(R.id.txtUsername);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtLastName = (EditText)findViewById(R.id.txtLastName);
        txtFirstName = (EditText)findViewById(R.id.txtFirstName);
        txtMobileNum = (EditText)findViewById(R.id.txtMobileNum);
        txtEmailAddress = (EditText)findViewById(R.id.txtEmailAddress);
        txtAddress = (EditText)findViewById(R.id.txtAddress);
        btnNext = (Button)findViewById(R.id.btnNext);
    }

    public void click(View v){
        if(validate())
        {
            if(v.equals(btnNext)){
                String username = txtUsername.getText().toString(),
                        password = txtPassword.getText().toString(),
                        lastName = txtLastName.getText().toString(),
                        firstName = txtFirstName.getText().toString(),
                        mobileNum = txtMobileNum.getText().toString(),
                        emailAddress = txtEmailAddress.getText().toString(),
                        address = txtAddress.getText().toString();

                owner.setUsername(username);
                owner.setPassword(password);
                owner.setLastName(lastName);
                owner.setFirstName(firstName);
                owner.setMobilePhone(mobileNum);
                owner.setEmailAddress(emailAddress);
                owner.setAddress(address);

                //go to step 5
                Intent i = new Intent(getApplicationContext(), step6_ownerProfilePic.class);
                startActivity(i);
            }
        }
        else
        {
            Toast.makeText(this,"Signup Failed",Toast.LENGTH_SHORT).show();
        }
    }
    public void initialize()
    {
        userN = txtUsername.getText().toString().trim();
        pass = txtPassword.getText().toString().trim();
        lastN = txtLastName.getText().toString().trim();
        firstN = txtFirstName.getText().toString().trim();
        mobileN = txtMobileNum.getText().toString().trim();
        emailA = txtEmailAddress.getText().toString().trim();
        Add = txtAddress.getText().toString().trim();
    }
    public boolean validate()
    {
        initialize();
        boolean valid = true;
        if(userN.isEmpty() | userN.length()>15)
        {
            txtUsername.setError("Please enter a valid username");
            valid=false;
        }
        if(pass.isEmpty() | pass.length()>15)
        {
            txtPassword.setError("Please enter a valid password");
            valid=false;
        }
        if(lastN.isEmpty() | lastN.length()>15)
        {
            txtLastName.setError("Please enter a valid last name");
            valid=false;
        }
        if(firstN.isEmpty() | firstN.length()>15)
        {
            txtFirstName.setError("Please enter a valid first name");
            valid=false;
        }
        if(mobileN.isEmpty() | mobileN.length()>14)
        {
            txtMobileNum.setError("Please enter a valid mobile number");
            valid=false;
        }
        if(emailA.isEmpty() | !Patterns.EMAIL_ADDRESS.matcher(emailA).matches())
        {
            txtEmailAddress.setError("Please enter a valid email address");
            valid=false;
        }
        if(Add.isEmpty())
        {
            txtAddress.setError("Please enter a valid address");
            valid=false;
        }
        return valid;
    }

}
