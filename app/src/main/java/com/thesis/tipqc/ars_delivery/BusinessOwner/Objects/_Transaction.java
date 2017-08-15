package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 7/1/2017.
 * customerID
 * date bought
 * volume capacity
 * delivery ID
 * progress ID
 */


public class _Transaction {
    private Map<String, _Customer> Customer = new HashMap<>();

    private Map<String, _Orders> Orders = new HashMap<>();

    private String dateBought, transactionID;

    public _Transaction(){

    }

    public _Transaction( String dateBought, String transactionID){
        this.dateBought = dateBought;
        this.transactionID = transactionID;
    }

    public String getDateBought() {
        return dateBought;
    }

    public void setDateBought(String dateBought) {
        this.dateBought = dateBought;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Map<String, _Orders> getOrders() {
        return Orders;
    }

    public void setOrders(Map<String, _Orders> orders) {
        Orders = orders;
    }

    public Map<String, _Customer> getCustomer() {
        return Customer;
    }

    public void setCustomer(Map<String, _Customer> customer) {
        Customer = customer;
    }
}
