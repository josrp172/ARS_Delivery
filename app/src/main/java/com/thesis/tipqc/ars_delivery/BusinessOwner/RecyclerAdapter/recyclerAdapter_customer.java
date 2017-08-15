package com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter;

/**
 * Created by Shade on 5/9/2016. For vehicles;
 */

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Transaction.addTransaction_step1;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._Customer;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.customerInformation;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.customer_main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class recyclerAdapter_customer extends RecyclerView.Adapter<recyclerAdapter_customer.ViewHolder> {

    public static int[] images = { R.drawable.people};
    private int selectedPos = 0;
    public static String customerName,customerEmail;
    class ViewHolder extends RecyclerView.ViewHolder{

         ImageView customer_image;
         TextView customer_name;
         TextView customer_email;
         ViewHolder(final View itemView) {
            super(itemView);
            customer_image = itemView.findViewById(R.id.customer_image);
            customer_name = itemView.findViewById(R.id.customer_name);
            customer_email =itemView.findViewById(R.id.customer_email);
            customer_image.setImageResource(images[0]);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(_OBJECTS.session.getCurrentActivity().equals("Transaction")) {

                        //customer
                        Map<String, _Customer> mCustomer = new HashMap<>();
                        mCustomer.put(addTransaction_step1.arrCustomer.get(position).getCustomerID(), addTransaction_step1.arrCustomer.get(position));
                        addTransaction_step1.transaction.setCustomer(mCustomer);

                        notifyItemChanged(selectedPos);
                        selectedPos = getLayoutPosition();
                        notifyItemChanged(selectedPos);

                    }else{
                        //go to customerInformation activity
                        Intent iCustomer = new Intent(itemView.getContext(), customerInformation.class);
                        iCustomer.putExtra("position", position);
                        itemView.getContext().startActivity(iCustomer);
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.customer_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        if(_OBJECTS.session.getCurrentActivity().equals("Transaction"))
            viewHolder.itemView.setSelected(selectedPos == i);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        // String customerName, customerEmail;
        String newText= Main_OwnerUI.getFilter();

        if(_OBJECTS.session.getCurrentActivity().equals("Transaction")){
            customerName = addTransaction_step1.arrCustomer.get(i).getFirstName() + " " + addTransaction_step1.arrCustomer.get(i).getLastName();
            customerEmail = addTransaction_step1.arrCustomer.get(i).getEmail();
        }else{
            customerName = customer_main.arrCustomer.get(i).getFirstName() + " " + customer_main.arrCustomer.get(i).getLastName();
            customerEmail = customer_main.arrCustomer.get(i).getEmail();
        }
        if(newText.isEmpty())
        {
            viewHolder.customer_name.setText(customerName);
            viewHolder.customer_email.setText(customerEmail);
        }
        else
        {
            if((customerName.toLowerCase()).contains(newText.toLowerCase()))
            {
                viewHolder.customer_name.setText(customerName);
                viewHolder.customer_email.setText(customerEmail);
            }
        }

        if(_OBJECTS.session.getCurrentActivity().equals("Transaction")) {
            if (selectedPos == i)
                viewHolder.itemView.setBackgroundColor(Color.YELLOW);
            else
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {
        if(_OBJECTS.session.getCurrentActivity().equals("Transaction")){
            return addTransaction_step1.arrCustomer.size();
        }else{
            return  customer_main.arrCustomer.size();
        }
    }


}