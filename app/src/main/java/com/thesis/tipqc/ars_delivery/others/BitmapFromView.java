package com.thesis.tipqc.ars_delivery.others;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.tipqc.ars_delivery.R;

/**
 * Created by PC on 7/26/2017.
 */

public class BitmapFromView{
    public Bitmap getMarkerBitmapFromView(Activity activity, @DrawableRes int resId, String name, String demand) {

        View customMarkerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custommarker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_pic);
        TextView customerName = (TextView) customMarkerView.findViewById(R.id.customer_name);
        TextView customerDemand = (TextView) customMarkerView.findViewById(R.id.customer_demand);

        markerImageView.setImageResource(resId);
        customerName.setText(name);
        customerDemand.setText(demand);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public Bitmap resizeMapIcons(Activity activity, Bitmap bitmap,int width, int height){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return resizedBitmap;
    }
}
