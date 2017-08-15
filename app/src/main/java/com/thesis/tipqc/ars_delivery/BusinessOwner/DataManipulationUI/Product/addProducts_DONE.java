package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Product;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;

public class addProducts_DONE extends AppCompatActivity {
    private TextView lblName, lblPrice,lblDescription, lblStock;
    private ImageView imgProductPic;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private String productKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products__done);

        lblName = (TextView)findViewById(R.id.lblName);
        lblPrice = (TextView)findViewById(R.id.lblPrice);
        lblDescription = (TextView)findViewById(R.id.lblDescription);
        lblStock = (TextView)findViewById(R.id.lblStock);
        imgProductPic = (ImageView)findViewById(R.id.imgProductPic);
        productKey = _OBJECTS.session.getProductKey();
        ref = database.getReference("Products/"+productKey);

        //get the image and set it to imageview(productPic)
        //imgProductPic.setImageBitmap(addProducts_step1.products.getPhoto());
        retrieveProductFromDatabase();
    }

    private void retrieveProductFromDatabase() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String description = null, price = null, productName = null, stock = null ;
                for(DataSnapshot productRow : dataSnapshot.getChildren()){
                    description = productRow.getKey().toString().equals("Description")?(String)productRow.getValue():description;
                    price = productRow.getKey().toString().equals("Price")?(String)productRow.getValue():price;
                    productName = productRow.getKey().toString().equals("Product Name")?(String)productRow.getValue():productName;
                    stock = productRow.getKey().toString().equals("Stock")?(String)productRow.getValue():stock;
                }
                lblName.setText(productName);
                lblPrice.setText(price);
                lblDescription.setText(description);
                lblStock.setText("Stock: "+stock);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error in retrieving from database", Toast.LENGTH_LONG).show();
            }
        });
    }
}
