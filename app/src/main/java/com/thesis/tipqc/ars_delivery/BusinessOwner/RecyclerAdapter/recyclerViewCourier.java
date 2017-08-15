package com.thesis.tipqc.ars_delivery.BusinessOwner.RecyclerAdapter;

/**
 * Created by Shade on 5/9/2016. For vehicles;
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery.addDelivery_step4_MAPS;
import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery.addDelivery_step5_COURIER;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.Main_OwnerUI;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._DeliveryPersonnel;
import com.thesis.tipqc.ars_delivery.BusinessOwner.Objects._OBJECTS;
import com.thesis.tipqc.ars_delivery.R;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.courier_main;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.deliveryInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class recyclerViewCourier extends RecyclerView.Adapter<recyclerViewCourier.ViewHolder> {
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder{

         TextView courier_name;
         TextView courier_email;
         ImageView courier_image;

        //there are so many redundant objects
        SparseArray<String> arr;
        SparseBooleanArray bool;
        //change this later


         ViewHolder(final View itemView) {
            super(itemView);
            courier_image = itemView.findViewById(R.id.courier_image);
            courier_image.setDrawingCacheEnabled(true);

            courier_name = itemView.findViewById(R.id.courier_name);
            courier_email = itemView.findViewById(R.id.courier_email);

            context = itemView.getContext();

             bool = new SparseBooleanArray();
             arr = new SparseArray<>();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if(_OBJECTS.session.getCurrentActivity().equals("Delivery Personnel")) {
                        //get the image from the selected imageview you have clicked
                        ImageView clickImage = v.findViewById(R.id.courier_image);
                        Bitmap bmap = clickImage.getDrawingCache();

                        //save it from the object class (Products)
                        _OBJECTS.session.setPhoto(bmap);

                        Intent i = new Intent(itemView.getContext(), deliveryInformation.class);
                        i.putExtra("position", position);
                        itemView.getContext().startActivity(i);
                    }else if(_OBJECTS.session.getCurrentActivity().equals("Delivery")) {


                        int size = addDelivery_step4_MAPS.selectedVehicle.size();
                        ArrayList<String> selectedVehicle = new ArrayList<>(size+1);

                        for(int i = 0; i < size; i++){
                            if(addDelivery_step4_MAPS.selectedVehicle.get(i)){
                                selectedVehicle.add(addDelivery_step4_MAPS.arrVehicles.get(addDelivery_step4_MAPS.selectedVehicle.keyAt(i)).getVehicleName());
                                arr.put(i, selectedVehicle.get(selectedVehicle.size()-1));
                            }
                        }

                        new MaterialDialog.Builder(itemView.getContext())
                                .title("Select Courier")
                                .items(selectedVehicle)
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        int key = arr.keyAt(arr.indexOfValue(text.toString()));
                                        //remove this later
                                        if(bool.get(key)){
                                            bool.put(key, false);
                                        }else{
                                            bool.put(key, true);
                                        }

                                        addDelivery_step5_COURIER.dp.put("ID "+Integer.toString(key), addDelivery_step5_COURIER.arrCourier.get(position));

                                        return false;
                                    }
                                })
                                .positiveText("Choice")
                                .show();
                        Toast.makeText(itemView.getContext(), ""+bool.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.courier_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        String name, email, key;
        String newText= Main_OwnerUI.getFilter();

        if(_OBJECTS.session.getCurrentActivity().equals("Delivery Personnel")) {
            name = courier_main.arrCourier.get(i).getFirstName() + " " + courier_main.arrCourier.get(i).getLastName();
            email = courier_main.arrCourier.get(i).getEmail();
            key = courier_main.arrCourier.get(i).getCourierKey();
        }else{
            name = addDelivery_step5_COURIER.arrCourier.get(i).getFirstName() + " " + addDelivery_step5_COURIER.arrCourier.get(i).getLastName();
            email = addDelivery_step5_COURIER.arrCourier.get(i).getEmail();
            key = addDelivery_step5_COURIER.arrCourier.get(i).getCourierKey();
        }
        if(newText.isEmpty())
        {
            viewHolder.courier_name.setText(name);
            viewHolder.courier_email.setText(email);
        }
        else if(courier_main.arrCourier.get(i).getFirstName().toLowerCase().contains(newText.toLowerCase()))
        {
            viewHolder.courier_name.setText(name);
            viewHolder.courier_email.setText(email);
        }


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://arsdelivery2017.appspot.com/Delivery Personnel/"+key);
        Glide.with(context.getApplicationContext())
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
                .into(viewHolder.courier_image);
    }


    @Override
    public int getItemCount() {
        if(_OBJECTS.session.getCurrentActivity().equals("Delivery Personnel"))
            return courier_main.arrCourier.size();
        else if(_OBJECTS.session.getCurrentActivity().equals("Delivery"))
            return addDelivery_step5_COURIER.arrCourier.size();
        else
            return 0;
    }




}