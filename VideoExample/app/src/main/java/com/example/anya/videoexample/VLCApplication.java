package com.example.anya.videoexample;

import android.app.Application;
import android.content.Context;

/**
 * Created by anya on 07.07.16.
 */
public class VLCApplication extends Application {
    private static VLCApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getAppContext()
    {
        return instance;
    }
}
