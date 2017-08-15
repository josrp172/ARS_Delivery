package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 7/1/2017.
 * productID
 * transactionID
 * quantity
 */

public class _Orders {

    private String orderID, quantity;
    private Map<String, _Products> Products = new HashMap<>();

    public _Orders(){
    }

    public _Orders(String orderID, String quantity){
        this.orderID = orderID;
        this.quantity = quantity;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Map<String, _Products> getProducts() {
        return Products;
    }

    public void setProducts(Map<String, _Products> products) {
        Products = products;
    }
}
