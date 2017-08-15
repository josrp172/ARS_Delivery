package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Product;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Products;

public class addProducts_step1 extends AppCompatActivity {

    private EditText txtProductName, txtPrice, txtDescription, txtStock;
    private Button btnAdd, btnCancel;
    private Spinner spnCategory;
    private ArrayAdapter<String> arrCategory;

    public static _Products products;
    String[] productCategory = {"Technology", "Food"};
    String productName,price,description,stock,category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products_step1);

        txtProductName = (EditText)findViewById(R.id.lblProductName);
        txtPrice = (EditText)findViewById(R.id.lblPrice);
        txtDescription = (EditText)findViewById(R.id.txtDescription);
        txtStock = (EditText)findViewById(R.id.lblStock);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        spnCategory = (Spinner)findViewById(R.id.spnCategory);
        arrCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, productCategory);

        products = new _Products();

        spnCategory.setAdapter(arrCategory);
    }

    public void click(View v){
        if(validate())
        {
            if(v.equals(btnAdd)){
                saveInformation();

                Intent i = new Intent(getApplicationContext(), addProducts_step2.class);
                startActivity(i);
            }else if(v.equals(btnCancel)){

            }
        }
        else
        {
            Toast.makeText(this,"Add product failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInformation(){
        String productName = txtProductName.getText().toString(),
                price = txtPrice.getText().toString(),
                description = txtDescription.getText().toString(),
                category = spnCategory.getSelectedItem().toString(),
                stock = txtStock.getText().toString();

        products.setProductName(productName);
        products.setPrice(price);
        products.setDescription(description);
        products.setCategory(category);
        products.setStock(stock);

    }

    public void initialize()
    {
        productName = txtProductName.getText().toString().trim();
        price = txtPrice.getText().toString().trim();
        description = txtDescription.getText().toString().trim();
        category = spnCategory.getSelectedItem().toString().trim();
        stock = txtStock.getText().toString().trim();
    }
    public boolean validate()
    {
        initialize();
        boolean valid = true;
        if(productName.isEmpty() | productName.length() > 15)
        {
            txtProductName.setError("Please enter a valid product name");
            valid = false;
        }
        if(price.isEmpty() | price.length() > 20)
        {
            txtPrice.setError("Please enter a valid price");
            valid = false;
        }
        if(description.isEmpty() | description.length() > 50)
        {
            txtDescription.setError("Please enter a valid description");
            valid = false;
        }
        if(category.isEmpty())
        {
            valid = false;
        }
        if(stock.isEmpty() | stock.length() > 15)
        {
            txtStock.setError("Please enter a valid amount of stock");
            valid = false;
        }
        return valid;
    }
}
