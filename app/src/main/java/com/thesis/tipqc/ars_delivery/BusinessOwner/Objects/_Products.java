package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

import android.graphics.Bitmap;

/**
 * Created by PC on 6/25/2017.
 */

public class _Products {

    private String businessKey;

    private String productKey,
            productName,
            price,
            category,
            description,
            stock,
            volume;

    public _Products(){

    }

    public _Products(String productName, String category, String description, String stock, String price, String selectedCategory){
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.category = selectedCategory;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

}
