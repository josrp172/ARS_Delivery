package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

import android.graphics.Bitmap;

/**
 * Created by PC on 6/25/2017.
 */

public class _Customer {

    private String firstName,
            lastName,
            email,
            mobilePhone,
            address,
            latitude,
            longitude,
            customerID;

    public _Customer() {}

    public _Customer(String lastName, String firstName, String email, String phone, String address, String latitude, String longitude){
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.mobilePhone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
}
