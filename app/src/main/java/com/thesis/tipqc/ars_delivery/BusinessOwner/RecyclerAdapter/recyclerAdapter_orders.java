package com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter;

/**
 * Created by Shade on 5/9/2016. For vehicles;
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery.addDelivery_step4_MAPS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.orders_main;


public class recyclerAdapter_orders extends RecyclerView.Adapter<recyclerAdapter_orders.ViewHolder> {

    private Context context;
    class ViewHolder extends RecyclerView.ViewHolder{

        public int currentItem;
        public ImageView order_image;
        public TextView order_name;
        public TextView order_quantity;

        public ViewHolder(final View itemView) {
            super(itemView);
            //use vehicle cardview (same as order layout)
            order_image = itemView.findViewById(R.id.vehicle_image);
            order_name = itemView.findViewById(R.id.vehicle_name);
            order_quantity = itemView.findViewById(R.id.vehicle_email);

            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.vehicle_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        String productKey, productName, quantity;

        if(_OBJECTS.session.getBusinessKey().equals("Transaction")) {
            productKey = orders_main.arrOrders.get(i).getProducts().keySet().toArray()[0].toString();
            productName = orders_main.arrOrders.get(i).getProducts().get(productKey).getProductName();
            quantity = orders_main.arrOrders.get(i).getQuantity();
        }else{
            productKey = addDelivery_step4_MAPS.arrOrders.get(i).getProducts().keySet().toArray()[0].toString();
            productName = addDelivery_step4_MAPS.arrOrders.get(i).getProducts().get(productKey).getProductName();
            quantity = addDelivery_step4_MAPS.arrOrders.get(i).getQuantity();
        }

        viewHolder.order_name.setText(productName);
        viewHolder.order_quantity.setText("Quantity: " +quantity);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://arsdelivery2017.appspot.com/Products/" + productKey);
        Glide.with(context.getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Toast.makeText(context.getApplicationContext(), "Error in position " + (i + 1), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .placeholder(R.drawable.rolling)
                .into(viewHolder.order_image);
    }


    @Override
    public int getItemCount() {
        if(_OBJECTS.session.getBusinessKey().equals("Transaction"))
            return orders_main.arrOrders.size();
        else
            return addDelivery_step4_MAPS.arrOrders.size();
    }




}