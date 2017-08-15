package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 6/23/2017.
 * table: business
 * business name
 * phone number
 * street address
 * suburb
 * postal code
 * category
 *latitude
 * longitude
 * photo
 */

public class _Business {
    private  String name, phoneNumber, description, category, address, businessKey;
    private  double latitude, longitude;



    private Map<String, _Products> Products = new HashMap<>();
    private Map<String, _Vehicles> Vehicles = new HashMap<>();

    public _Business(){
    }

    //what about photo?

    public _Business(String name, String phoneNumber, String description, String address, String category, double latitude, double longitude){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }


    public Map<String, _Products> getProducts() {
        return Products;
    }

    public void setProducts(Map<String, _Products> products) {
        Products = products;
    }

    public Map<String, _Vehicles> getVehicles() {
        return Vehicles;
    }

    public void setVehicles(Map<String, _Vehicles> vehicles) {
        Vehicles = vehicles;
    }
}
