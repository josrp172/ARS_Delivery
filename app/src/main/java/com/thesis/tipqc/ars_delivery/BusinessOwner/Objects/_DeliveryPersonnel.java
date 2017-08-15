package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

import android.graphics.Bitmap;

/**
 * Created by PC on 6/23/2017.
 */

public class _DeliveryPersonnel {


    private String courierKey,
            firstName,
            lastName,
            mobilePhone,
            email,
            address;
    private String password;
    private String businessKey;


    public  _DeliveryPersonnel(){
    }

    public _DeliveryPersonnel(String email, String firstName, String lastName, String mobileNum, String address, String password, Bitmap photo){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobilePhone = mobileNum;
        this.address = address;
        this.password = password;
    }


    public String getCourierKey() {
        return courierKey;
    }

    public void setCourierKey(String courierKey) {
        this.courierKey = courierKey;
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

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}
