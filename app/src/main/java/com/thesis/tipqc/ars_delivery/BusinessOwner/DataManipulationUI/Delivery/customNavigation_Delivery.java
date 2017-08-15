package com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery;

import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.thesis.tipqc.ars_delivery.BusinessOwner.MainUI.transaction_main;
import com.thesis.tipqc.ars_delivery.R;

import java.util.ArrayList;

public class customNavigation_Delivery extends AppCompatActivity {
    private AHBottomNavigation bottomNavigation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_navigation__delivery);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Menu 1", R.drawable.delivery_truck);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Menu 2", R.drawable.order);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Menu 3", R.drawable.people);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);



        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if(position == 1){
                    //Fragment fragment = new addDelivery_step4_MAPS();

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    //transaction.replace(R.id.content12, fragment);
                    transaction.commit();
                }
                // Do something cool here...
                return true;
            }
        });

    }
}
