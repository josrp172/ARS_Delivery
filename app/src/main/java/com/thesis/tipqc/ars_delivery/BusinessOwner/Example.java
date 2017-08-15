package com.thesis.tipqc.ars_delivery.BusinessOwner;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by PC on 7/13/2017.
 */

public class Example {

    public static void main(String[] args){
        GoogleDirection.withServerKey("");

        String key = "AIzaSyA_Vj68cTwVJVDQiht3Aw4-sBh8QRvMvTw";

        GoogleDirection.withServerKey(key)
                .from(new LatLng(37.7681994, -122.444538))
                .to(new LatLng(37.7749003,-122.4034934))
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()) {
                            System.out.println(direction.getRouteList().toString());
                        } else {
                            // Do something
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });
    }
}
