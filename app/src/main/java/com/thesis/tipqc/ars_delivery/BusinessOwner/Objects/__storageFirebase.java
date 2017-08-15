package com.thesis.tipqc.ars_delivery.BusinessOwner.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;


/**
 * Created by PC on 6/24/2017.
 */

public class __storageFirebase {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference, imageFolder, gsReference;
    public Bitmap myBitmap;

    public __storageFirebase(){

    }

    //keyName(name of the file) = user unique id
    public void uploadFileToDatabase(String storageFolder, String keyName, Uri filePath){
        storageReference = storage.getReference();
        imageFolder = storageReference.child(storageFolder);
        StorageReference images = imageFolder.child(keyName);
        images.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //successful

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //not successful
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //can be useful in displaying the progress of the upploading of file
                        //double progress = (100.0*taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                    }
                });
    }


    //
    public void getFileFromDatabase(Context c, String storageFolder, String keyName, ImageView image, final ProgressBar progress) {
        StorageReference gsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/arsdelivery2017.appspot.com/o/"
                                                                    +storageFolder+"/"+keyName+"");
        // Load the image using Glide
        Glide.with(c /* context */)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .bitmapTransform(new jp.wasabeef.glide.transformations.CropCircleTransformation(c))
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if(progress!=null) progress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);
    }

    public void getFileFromDatabase(Context c, String storageFolder, String keyName, ImageView image) {
        StorageReference gsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/arsdelivery2017.appspot.com/o/"
                +storageFolder+"/"+keyName+"");
        // Load the image using Glide
        Glide.with(c.getApplicationContext() /* context */)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(image);
    }


    //copy and paste it to the class you want to put on.
    public void getPicture(String storageFolder, String keyName){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://arsdelivery2017.appspot.com/"+storageFolder+"/"+keyName);
        try {
            final File localFile = File.createTempFile("image", "jpg"); //save file to storage or anywhere
            gsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
