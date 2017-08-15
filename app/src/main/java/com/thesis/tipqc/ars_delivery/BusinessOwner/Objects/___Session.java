package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;



import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by PC on 6/24/2017.
 */

public class ___Session {
    protected String businessKey;
    protected String userKey;
    protected String productKey;

    //owner
    protected String ownerEmail, ownerID, ownerName;

    private Bitmap photo;

    private String currentActivity = "";

    public ___Session() {
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(String currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void setBusinessKey(String businessKey){
        this.businessKey = businessKey;
    }

    public String getBusinessKey(){
        return businessKey;
    }



    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }


    //OWNER methods session
    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }


    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }


    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
