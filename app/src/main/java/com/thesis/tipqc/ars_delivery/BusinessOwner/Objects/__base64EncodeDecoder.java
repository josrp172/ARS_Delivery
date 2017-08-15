package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by PC on 6/24/2017.
 */

public class __base64EncodeDecoder {

    //encode Image into base64
    public static String encodeTobase64(Bitmap image){
        Bitmap imagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }

    //decode image into bitmap
    public static Bitmap decodeBase64(String input){
        byte[] decodedByte = Base64.decode(input,0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
