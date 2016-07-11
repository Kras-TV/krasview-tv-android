package com.example.anya.videoexample;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.*;

public class SimpleActivity extends Activity {
    public final static String TAG = "LibVLCAndroidSample/VideoActivity";

    private String mFilePath;

    private VideoViewVLC mSurface;

    File mFile;

    /*************
     * Activity
     *************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager myAssetManager = getApplicationContext().getAssets();

        String filename = "radioastron.mp4";

        mFile = new File(getExternalFilesDir(null), filename);
        mFilePath = mFile.getPath();

        if(!mFile.exists()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = myAssetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }

        Log.d("MyVLC", "exists" + mFile.exists());

        FrameLayout frame = (FrameLayout) findViewById(R.id.player_surface_frame);

        mSurface = new VideoViewVLC(this);
        mSurface.setLayoutParams(new FrameLayout.LayoutParams(400, 300));
        frame.addView(mSurface);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSurface.setVideoAndStart("http://media5.krasview.ru/download/354c6945d7af5e5.mp4");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurface.releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSurface.releasePlayer();
    }
}

