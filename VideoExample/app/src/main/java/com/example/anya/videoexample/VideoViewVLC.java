package com.example.anya.videoexample;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by anya on 25.06.16.
 */
public class VideoViewVLC extends SurfaceView implements IVLCVout.Callback, VideoInterface {

    public final static String TAG = "LibVLCAndroidSample/VideoViewVLC";

    private SurfaceView mSurface;
    private SurfaceHolder holder;

    private LibVLC libvlc;
    private org.videolan.libvlc.MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;


    public VideoViewVLC(Context context) {
        super(context);
        mSurface = this;
        holder = mSurface.getHolder();
        mSurface.setLayoutParams(new FrameLayout.LayoutParams(mVideoWidth, mVideoHeight));
    }

    @Override
    public void setVideoAndStart(String address) {
        this.createPlayer(address);
    }

    @Override
    public void stop() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void play() {

    }

    @Override
    public void setTVController(Object tc) {

    }

    @Override
    public void setVideoController(Object vc) {

    }

    @Override
    public void setMap(Map<String, Object> map) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean showOverlay() {
        return false;
    }

    @Override
    public boolean hideOverlay() {
        return false;
    }

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public int getLeight() {
        return 0;
    }

    @Override
    public int getTime() {
        return 0;
    }

    @Override
    public void setTime(int time) {

    }

    @Override
    public int changeSizeMode() {
        return 0;
    }

    @Override
    public String changeAudio() {
        return null;
    }

    @Override
    public String changeSubtitle() {
        return null;
    }

    @Override
    public int getAudioTracksCount() {
        return 0;
    }

    @Override
    public int getSpuTracksCount() {
        return 0;
    }

    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {

    }

    @Override
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {

    }

    @Override
    public int changeOrientation() {
        setSize(mVideoWidth, mVideoHeight);
        return 0;
    }

    @Override
    public void end() {

    }

    /*************
     * Player
     *************/

    private void createPlayer(String media) {
        releasePlayer();
        try {
            if (media.toString().length() > 0) {
                Toast toast = Toast.makeText(getContext(), media.toString(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                        0);
                toast.show();
            }

            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            libvlc = new LibVLC(options);
            //libvlc.setOnHardwareAccelerationError(this);
            holder.setKeepScreenOn(true);

            // Create media player
            mMediaPlayer = new org.videolan.libvlc.MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);

            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);
            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this);
            vout.attachViews();
            Media m = new Media(libvlc, media);
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error creating player!", Toast.LENGTH_LONG).show();
        }
    }

    // TODO: handle this cleaner
    public void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        holder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    public void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if(holder == null || mSurface == null)
            return;

        // get screen size
        int w = ((Activity)this.getContext()).getWindow().getDecorView().getWidth();
        int h = ((Activity)this.getContext()).getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        setSize(mVideoWidth, mVideoHeight);
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

    private org.videolan.libvlc.MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    private static class MyPlayerListener implements org.videolan.libvlc.MediaPlayer.EventListener {
        private WeakReference<VideoViewVLC> mOwner;

        public MyPlayerListener(VideoViewVLC owner) {
            mOwner = new WeakReference<VideoViewVLC>(owner);
        }

        @Override
        public void onEvent(org.videolan.libvlc.MediaPlayer.Event event) {
            VideoViewVLC player = mOwner.get();

            switch(event.type) {
                case org.videolan.libvlc.MediaPlayer.Event.EndReached:
                    Log.d(TAG, "MediaPlayerEndReached");
                    player.releasePlayer();
                    break;
                case org.videolan.libvlc.MediaPlayer.Event.Playing:
                case org.videolan.libvlc.MediaPlayer.Event.Paused:
                case org.videolan.libvlc.MediaPlayer.Event.Stopped:
                default:
                    break;
            }
        }
    }
}
