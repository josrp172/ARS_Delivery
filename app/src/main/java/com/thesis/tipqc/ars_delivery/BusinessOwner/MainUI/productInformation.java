package com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.products_main;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Products;

public class productInformation extends AppCompatActivity {
    private TextView lblProductName, lblPrice, lblStock, lblDescription;
    private ImageView imgProductPic;
    String productKey;

    _Products products = new _Products();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        lblProductName = (TextView)findViewById(R.id.lblProductName);
        lblPrice = (TextView)findViewById(R.id.lblPrice);
        lblStock = (TextView)findViewById(R.id.lblStock);
        lblDescription = (TextView)findViewById(R.id.lblDescription);
        imgProductPic = (ImageView)findViewById(R.id.imgProductPic);


        getInformation();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                builder1.setMessage("Are you sure you want to delete this");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String businessKey = ownerLogin.business.getBusinessKey();
                                //remove products information from the database
                                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().getRoot().child("Business/"+businessKey+"/Products").child(productKey);
                                db_node.removeValue();

                                //remove products pic from the storage
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference productsReference = storage.getReferenceFromUrl("gs://arsdelivery2017.appspot.com/Products/"+productKey);
                                productsReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //successfully deleted the picture.
                                    }
                                });



                                //go back from the main UI
                                Intent i = new Intent(getApplicationContext(), Main_OwnerUI.class);
                                _OBJECTS.session.setCurrentActivity("Products");
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    private void getInformation(){
        int position = getIntent().getIntExtra("position", -1);
        if(position!=-1){
            String productName = products_main.arrProducts.get(position).getProductName(),
                    price = products_main.arrProducts.get(position).getPrice(),
                    stock = products_main.arrProducts.get(position).getStock(),
                    description = products_main.arrProducts.get(position).getDescription();
            productKey = products_main.arrProducts.get(position).getProductKey();


            imgProductPic.setImageBitmap(_OBJECTS.session.getPhoto());

            lblProductName.setText(productName);
            lblPrice.setText(price);
            lblStock.setText(stock);
            lblDescription.setText(description);

        }else{
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }
}
