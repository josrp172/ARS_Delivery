package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

import android.util.SparseBooleanArray;

import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 8/2/2017.
 */

public class _Delivery {
    private Map<String, _Transaction> transaction = new HashMap<>();  // key -> (lat,lng) value -> transaction
    private Map<String, String> realRoutes = new HashMap<>(); //key -> vehicle ID  value -> routes
    private Map<String, _Vehicles> vehicle = new HashMap<>();
    private Map<String, _DeliveryPersonnel> deliveryPersonnel = new HashMap<>(); //key -> vehicle ID  value -> courier
    private Map<String, String> vehicleTransaction = new HashMap<>();

    public _Delivery(){}

    public Map<String, _Transaction> getTransaction() {
        return transaction;
    }

    public void setTransaction(Map<String, _Transaction> transaction) {
        this.transaction = transaction;
    }

    public Map<String, String> getRealRoutes() {
        return realRoutes;
    }

    public void setRealRoutes(Map<String, String> realRoutes) {
        this.realRoutes = realRoutes;
    }


     public Map<String, _DeliveryPersonnel> getDeliveryPersonnel() {
        return deliveryPersonnel;
    }

    public void setDeliveryPersonnel(Map<String, _DeliveryPersonnel> deliveryPersonnel) {
        this.deliveryPersonnel = deliveryPersonnel;
    }

    public Map<String, _Vehicles> getVehicle() {
        return vehicle;
    }

    public void setVehicle(Map<String, _Vehicles> vehicle) {
        this.vehicle = vehicle;
    }


    public Map<String, String> getVehicleTransaction() {
        return vehicleTransaction;
    }

    public void setVehicleTransaction(Map<String, String> vehicleTransaction) {
        this.vehicleTransaction = vehicleTransaction;
    }
}
