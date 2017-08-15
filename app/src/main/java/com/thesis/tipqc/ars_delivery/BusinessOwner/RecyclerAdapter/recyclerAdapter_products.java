package com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter;

/**
 * Created by Shade on 5/9/2016. For products;
 *
 *
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Transaction.addTransaction_step1;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.productInformation;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.products_main;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Transaction.addTransaction_step2;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Orders;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Products;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class recyclerAdapter_products extends RecyclerView.Adapter<recyclerAdapter_products.ViewHolder> {

    private Context context;
    private int position = 0;
    public boolean[] isSelected = new boolean[200]; //maximum products it can add

    class ViewHolder extends RecyclerView.ViewHolder{

        public int currentItem;
        public ImageView product_image;
        public TextView product_name;
        public TextView product_price;

        public ViewHolder(final View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_image.setDrawingCacheEnabled(true);
            itemView.setBackgroundColor(Color.WHITE);

            Arrays.fill(isSelected,false);

            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    position = getAdapterPosition();
                    if(_OBJECTS.session.getCurrentActivity().equals("Transaction")) {
                        //to be added
                        int colorID = ((ColorDrawable) itemView.getBackground()).getColor();
                        if(colorID == Color.YELLOW){
                            isSelected[position] = false;
                            itemView.setBackgroundColor(Color.WHITE);
                        }else if(colorID == Color.WHITE){

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(itemView.getContext());
                            //input quantity
                            final EditText input = new EditText(itemView.getContext());
                            builder1.setMessage("How many orders of this product? ");

                            //fill out the dialog box width with editText input
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            builder1.setView(input);
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            isSelected[position] = true;
                                            itemView.setBackgroundColor(Color.YELLOW);

                                            _Orders o = new _Orders();
                                            Map<String,_Products> mProd = new HashMap<>();
                                            mProd.put(addTransaction_step2.arrProducts.get(position).getProductKey(),addTransaction_step2.arrProducts.get(position));
                                            o.setProducts(mProd);
                                            o.setQuantity(input.getText().toString());
                                            //add products to object class (Orders)
                                            addTransaction_step2.arrOrders.add(o);
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
                    }else{

                        //get the image from the selected imageview you have clicked
                        ImageView clickImage = v.findViewById(R.id.product_image);
                        Bitmap bmap = clickImage.getDrawingCache();

                        //save it from the object class (Products)
                        _OBJECTS.session.setPhoto(bmap);
                        Intent i = new Intent(itemView.getContext(), productInformation.class);
                        i.putExtra("position", position);
                        itemView.getContext().startActivity(i);
                    }
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.products_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        String productKey = null, productName = null, productPrice = null;
        String newText= Main_OwnerUI.getFilter();
        if(_OBJECTS.session.getCurrentActivity().equals("Transaction")){
           productKey = addTransaction_step2.arrProducts.get(i).getProductKey();
            productName = addTransaction_step2.arrProducts.get(i).getProductName();
            productPrice = addTransaction_step2.arrProducts.get(i).getPrice();
        }else{
            productKey = products_main.arrProducts.get(i).getProductKey();
            productName = products_main.arrProducts.get(i).getProductName();
            productPrice = products_main.arrProducts.get(i).getPrice();
        }
        if(newText=="")
        {
            viewHolder.product_name.setText(productName);
            viewHolder.product_price.setText(productPrice);
        }
        else
        {
            if(productName.toLowerCase().contains(newText.toLowerCase()))
            {
                viewHolder.product_name.setText(productName);
                viewHolder.product_price.setText(productPrice);
            }
        }


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://arsdelivery2017.appspot.com/Products/"+productKey);
        Glide.with(context.getApplicationContext() /* context */)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Toast.makeText(context.getApplicationContext(), "Error in position "+(i+1), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .placeholder(R.drawable.rolling)
                .into(viewHolder.product_image);
    }


    @Override
    public int getItemCount() {
        if(_OBJECTS.session.getCurrentActivity().equals("Transaction")){
            return addTransaction_step2.arrProducts.size();
        }else{
            return products_main.arrProducts.size();
        }
    }
}