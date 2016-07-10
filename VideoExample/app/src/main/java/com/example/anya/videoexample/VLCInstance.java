package com.example.anya.videoexample;

import android.util.Log;
import org.videolan.libvlc.LibVLC;

/**
 * Created by anya on 07.07.16.
 */
public class VLCInstance {

    private static final String TAG = "VLCInctance";

    private static LibVLC sLibVLC = null;

    public synchronized static LibVLC get() throws IllegalStateException {
        if(sLibVLC == null) {
            sLibVLC = sLibVLC = new LibVLC();
        }
        Log.d(TAG, "VLC version " + sLibVLC.version());
        return sLibVLC;
    }
}
