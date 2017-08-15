package com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter;

/**
 * Created by Shade on 5/9/2016. For vehicles;
 */

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery.addDelivery_step1;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.orders_main;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.transaction_main;



public class recyclerAdapter_transaction extends RecyclerView.Adapter<recyclerAdapter_transaction.ViewHolder> {

    public static int[] images = { R.drawable.order};
    public static String customerName="";
    class ViewHolder extends RecyclerView.ViewHolder{

         ImageView transaction_image;
         TextView transaction_name;
         TextView transaction_customer;


         ViewHolder(final View itemView) {
            super(itemView);
            transaction_image = itemView.findViewById(R.id.transaction_image);
            transaction_name = itemView.findViewById(R.id.transaction_name);
            transaction_customer = itemView.findViewById(R.id.transaction_customer_name);
            transaction_image.setImageResource(images[0]);
            itemView.setBackgroundColor(Color.WHITE);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(_OBJECTS.session.getCurrentActivity().equals("Transaction")) {
                        AppCompatActivity activity = (AppCompatActivity) itemView.getContext();

                        Bundle bundle = new Bundle();
                        bundle.putString("transactionID", transaction_main.arrTransaction.get(position).getTransactionID());

                        //go to orders_main
                        Fragment fragmentTransaction = new orders_main();
                        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.cdLayout, fragmentTransaction);
                        fragmentTransaction.setArguments(bundle);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    }else if(_OBJECTS.session.getCurrentActivity().equals("Delivery")){

                        int colorID = ((ColorDrawable) itemView.getBackground()).getColor();
                        if(colorID == Color.YELLOW){
                            addDelivery_step1.isSelected.put(position, false);
                            itemView.setBackgroundColor(Color.WHITE);
                        }else if(colorID == Color.WHITE){
                            addDelivery_step1.isSelected.put(position, true);
                            itemView.setBackgroundColor(Color.YELLOW);
                        }
                    }
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
        String transactionID = "", customerName = "", key = "";
        String newText= Main_OwnerUI.getFilter();

        if(_OBJECTS.session.getCurrentActivity().equals("Transaction")){
            key = transaction_main.arrTransaction.get(i).getCustomer().keySet().toArray()[0].toString();
            transactionID = transaction_main.arrTransaction.get(i).getTransactionID();
            customerName = transaction_main.arrTransaction.get(i).getCustomer().get(key).getFirstName()+" "+
                    transaction_main.arrTransaction.get(i).getCustomer().get(key).getLastName();

        }else if(_OBJECTS.session.getCurrentActivity().equals("Delivery")){
            key = addDelivery_step1.arrTransaction.get(i).getCustomer().keySet().toArray()[0].toString();
            transactionID = addDelivery_step1.arrTransaction.get(i).getTransactionID();
            customerName = addDelivery_step1.arrTransaction.get(i).getCustomer().get(key).getFirstName()+" "+
                    addDelivery_step1.arrTransaction.get(i).getCustomer().get(key).getLastName();
        }

        if(newText.isEmpty())
        {
            viewHolder.transaction_name.setText("Transaction ID "+i+":  "+transactionID);
            viewHolder.transaction_customer.setText(customerName);
        }
        else if(customerName.toLowerCase().contains(newText.toLowerCase()))
        {
            viewHolder.transaction_name.setText("Transaction ID "+i+":  "+transactionID);
            viewHolder.transaction_customer.setText(customerName);
        }

    }


    @Override
    public int getItemCount() {
        if(_OBJECTS.session.getCurrentActivity().equals("Transaction"))
            return transaction_main.arrTransaction.size();
        else if(_OBJECTS.session.getCurrentActivity().equals("Delivery"))
            return addDelivery_step1.arrTransaction.size();

        return 0;
    }
}