package com.flameon.customer.Constants;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;

import io.fabric.sdk.android.Fabric;

/**
 * Created by qboxus on 10/18/2019.
 */

public class Foodies extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Fresco.initialize(this);
            Fabric.with(this, new Crashlytics());

        }catch (Exception e){

        }
    }




}
