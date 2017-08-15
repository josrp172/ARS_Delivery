package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

/**
 * Created by PC on 6/25/2017.
 */

public class _Vehicles {


    private String vehicleName,
            mpg,
            type,
            licencePlate,
            volumeCapacity,
            vehicleID;


    public _Vehicles(){

    }

    public _Vehicles(String vehicleName, String type, String mpg, String volumeCapacity, String licencePlate){
        this.vehicleName = vehicleName;
        this.type = type;
        this.mpg = mpg;
        this.volumeCapacity = volumeCapacity;
        this.licencePlate = licencePlate;
    }


    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getMpg() {
        return mpg;
    }

    public void setMpg(String mpg) {
        this.mpg = mpg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getVolumeCapacity() {
        return volumeCapacity;
    }

    public void setVolumeCapacity(String volumeCapacity) {
        this.volumeCapacity = volumeCapacity;
    }
}
