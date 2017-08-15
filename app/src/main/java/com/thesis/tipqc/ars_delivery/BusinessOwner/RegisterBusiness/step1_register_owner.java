package com.thesis.tipqc.ars_delivery.BusinessOwner.RegisterBusiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Business;
import com.thesis.tipqc.ars_delivery.R;

public class step1_register_owner extends AppCompatActivity {
    private EditText txtName, txtPhone, txtDescription;
    private Button btnCancel, btnNext;
    private Spinner spnCategory;
    ArrayAdapter<String> arrCategory;
    private String[] category = {"Technology"}; //add more
    String name,phone,description,category2;
    public static _Business business = new _Business();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1_register_owner);

        txtName = (EditText)findViewById(R.id.lblNameMain);
        txtPhone = (EditText)findViewById(R.id.txtPhone);
        txtDescription = (EditText)findViewById(R.id.txtDescription);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnNext = (Button)findViewById(R.id.btnNext);

        spnCategory = (Spinner)findViewById(R.id.spnCategory);
        arrCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, category);
        spnCategory.setAdapter(arrCategory);
    }

    public void click(View v){
        if(validate())
        {
            if(v.equals(btnNext)){
                //go to step 2(choose map)
                Intent i = new Intent(getApplicationContext(), step2_chooseBusinessLocation_owner.class);
                startActivity(i);
                String name = txtName.getText().toString(),
                        phone = txtPhone.getText().toString(),
                        description = txtDescription.getText().toString(),
                        category = spnCategory.getSelectedItem().toString();


                business.setName(name);
                business.setPhoneNumber(phone);
                business.setDescription(description);
                business.setCategory(category);
            }else if(v.equals(btnCancel)) {

            }
        }
        else
        {
            Toast.makeText(this,"Signup has Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void initialize()
    {
        name = txtName.getText().toString().trim();
        phone = txtPhone.getText().toString().trim();
        description = txtDescription.getText().toString().trim();
        category2 = spnCategory.getSelectedItem().toString().trim();
    }
    public boolean validate()
    {
        initialize();
        boolean valid = true;
        if(name.isEmpty() | name.length()>15)
        {
            txtName.setError("Please enter a valid name");
            valid = false;
        }
        if(phone.isEmpty() | phone.length()>13)
        {
            txtPhone.setError("Please enter a valid phone number");
            valid = false;
        }
        if(description.isEmpty() | description.length() > 50)
        {
            txtDescription.setError("Please enter a valid description");
            valid = false;
        }
        if(category2.isEmpty())
        {
            valid = false;
        }
        return valid;
    }

}
