package com.example.anya.videoexample;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.videolan.libvlc.IVLCVout;

public class MainActivity extends AppCompatActivity implements IVLCVout.Callback, PlaybackService.Client.Callback  {

    private static final String TAG = "om.example.anya.videoexample/MainActivity";
    private SurfaceView mSurfaceView;
    private FrameLayout mSurfaceFrame;
    private int mVideoHeight;
    private int mVideoVisibleWidth;
    private int mVideoVisibleHeight;
    private int mSarNum;
    private int mSarDen;
    private int mVideoWidth;
    private  PlaybackService mService ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSurfaceView = (SurfaceView) findViewById(R.id.player_surface);
        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);

        VLCApplication.getAppContext().startService(new Intent(VLCApplication.getAppContext(), PlaybackService.class));
    }

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        // store video size

        mVideoHeight = height;
        mVideoVisibleWidth  = visibleWidth;
        mVideoVisibleHeight = visibleHeight;
        mSarNum = sarNum;
        mSarDen = sarDen;
        changeSurfaceLayout();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void changeSurfaceLayout() {
        int sw;
        int sh;

        // get screen size

            sw = getWindow().getDecorView().getWidth();
            sh = getWindow().getDecorView().getHeight();


        if (mService != null) {
            final IVLCVout vlcVout = mService.getVLCVout();
            vlcVout.setWindowSize(sw, sh);
        }

        double dw = sw, dh = sh;
        boolean isPortrait;

            // getWindow().getDecorView() doesn't always take orientation into account, we have to correct the values
            isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }

        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }

        // compute the aspect ratio
        double ar, vw;
        if (mSarDen == mSarNum) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double)mVideoVisibleWidth / (double)mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * (double)mSarNum / mSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

     /*   switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_FIT_HORIZONTAL:
                dh = dw / ar;
                break;
            case SURFACE_FIT_VERTICAL:
                dw = dh * ar;
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_ORIGINAL:*/
                dh = mVideoVisibleHeight;
                dw = vw;
               // break;
      //  }

        SurfaceView surface;
        SurfaceView subtitlesSurface;
        FrameLayout surfaceFrame;

        surface = mSurfaceView;
        surfaceFrame = mSurfaceFrame;


        // set display size
        ViewGroup.LayoutParams lp = surface.getLayoutParams();
        lp.width  = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        surface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = surfaceFrame.getLayoutParams();
        lp.width = (int) Math.floor(dw);
        lp.height = (int) Math.floor(dh);
        surfaceFrame.setLayoutParams(lp);

        surface.invalidate();
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    public void onHardwareAccelerationError(IVLCVout vlcVout) {

    }

    @Override
    public void onConnected(PlaybackService service) {
        mService = service;
        startPlayback();
    }

    private void startPlayback() {
        Uri uri = Uri.parse("http://media5.krasview.ru/download/5970c1f0ce53d76.mp4");
        mService.play(uri);
        mService.getVLCVout().setVideoView(mSurfaceView);
        mService.getVLCVout().addCallback(this);
    }

    @Override
    public void onDisconnected() {
        mService = null;
    }
}
