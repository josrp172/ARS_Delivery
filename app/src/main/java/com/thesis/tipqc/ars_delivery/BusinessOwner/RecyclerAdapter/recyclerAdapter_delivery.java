package com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter;

/**
 * Created by Shade on 5/9/2016. For delivery;
 * change this
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.delivery_main;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.orders_main;


public class recyclerAdapter_delivery extends RecyclerView.Adapter<recyclerAdapter_delivery.ViewHolder> {

    public static int[] images = { R.drawable.order};

    class ViewHolder extends RecyclerView.ViewHolder{

        public int currentItem;
        public ImageView delivery_image;
        public TextView delivery_name;
        public TextView delivery____;

        public ViewHolder(final View itemView) {
            super(itemView);
            delivery_image = itemView.findViewById(R.id.transaction_image);
            delivery_name = itemView.findViewById(R.id.transaction_name);
            delivery____ = itemView.findViewById(R.id.transaction_customer_name);
            delivery_image.setImageResource(images[0]);

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
                .inflate(R.layout.transaction_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String deliveryID;
        String newText= Main_OwnerUI.getFilter();
        deliveryID = delivery_main.arrDelivery.get(i).toString();
        if(newText.isEmpty())
        {
            viewHolder.delivery_name.setText(deliveryID);
        }
        else if(delivery_main.arrDelivery.get(i).toString().contains(newText.toLowerCase()))
        {
            viewHolder.delivery_name.setText(deliveryID);
        }

    }


    @Override
    public int getItemCount() {
        return delivery_main.arrDelivery.size();
    }




}