package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

/**
 * Created by PC on 6/23/2017.
 * Table: Owner
 *
 * (unique key)
 * username
 * password
 * type: owner
 * first name
 * last name
 * mobile phone
 * email address
 * address
 *
 * business ID (foreign key)
 */

public class _Owner {
    private String username, firstName, lastName, mobilePhone, emailAddress, address;
    private String password;
    private String businessKey;
    private String userID;

    public _Owner(){ }

    public _Owner(String username, String lastName, String firstName, String mobilePhone, String emailAddress, String address) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobilePhone = mobilePhone;
        this.emailAddress = emailAddress;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}
