package com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter;

/**
 * Created by Shade on 5/9/2016. For vehicles;
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.vehicleInformation;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.vehicle_main;


public class recyclerAdapter_vehicles extends RecyclerView.Adapter<recyclerAdapter_vehicles.ViewHolder> {

    public static int[] images = { R.drawable.delivery_truck,
            R.drawable.motor_sports,
            R.drawable.suv,
            R.drawable.trucking,
            R.drawable.vespa};

    class ViewHolder extends RecyclerView.ViewHolder{

         ImageView vehicle_image;
         TextView vehicle_name;
         TextView vehicle_volume;

         ViewHolder(final View itemView) {
            super(itemView);
            vehicle_image = itemView.findViewById(R.id.vehicle_image);
            vehicle_name = itemView.findViewById(R.id.vehicle_name);
            vehicle_volume = itemView.findViewById(R.id.vehicle_email);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                    Intent i = new Intent(itemView.getContext(), vehicleInformation.class);
                    i.putExtra("position", position);
                    itemView.getContext().startActivity(i);
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
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String newText= Main_OwnerUI.getFilter();
        if(newText.isEmpty())
        {
            viewHolder.vehicle_name.setText(vehicle_main.arrVehicles.get(i).getVehicleName());
            viewHolder.vehicle_volume.setText(vehicle_main.arrVehicles.get(i).getVolumeCapacity());
        }
        else if(vehicle_main.arrVehicles.get(i).getVehicleName().toLowerCase().contains(newText.toLowerCase()))
        {
            viewHolder.vehicle_name.setText(vehicle_main.arrVehicles.get(i).getVehicleName());
            viewHolder.vehicle_volume.setText(vehicle_main.arrVehicles.get(i).getVolumeCapacity());
        }

        if(vehicle_main.arrVehicles.get(i).getType().equals("Car")) viewHolder.vehicle_image.setImageResource(images[2]);
        else if(vehicle_main.arrVehicles.get(i).getType().equals("Scooter")) viewHolder.vehicle_image.setImageResource(images[4]);
        else if(vehicle_main.arrVehicles.get(i).getType().equals("Truck")) viewHolder.vehicle_image.setImageResource(images[0]);
        else if(vehicle_main.arrVehicles.get(i).getType().equals("Van")) viewHolder.vehicle_image.setImageResource(images[3]);
        else if(vehicle_main.arrVehicles.get(i).getType().equals("Motorcycle")) viewHolder.vehicle_image.setImageResource(images[1]);
    }


    @Override
    public int getItemCount() {
        return vehicle_main.arrVehicles.size();
    }




}