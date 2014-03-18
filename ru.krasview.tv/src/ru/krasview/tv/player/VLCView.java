package ru.krasview.tv.player;

import java.util.ArrayList;
import java.util.Map;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.vlc.Util;


import ru.krasview.tv2.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class VLCView extends SurfaceView implements IVideoPlayer, VideoInterface{
	
	
	public final static String TAG = "ru.krasview.tv.VideoViewVLC";
	
	TVController mTVController;
	VideoController mVideoController;
	Map<String, Object> mMap;
	
	RelativeLayout parentLayout;
	
	private SurfaceHolder mSurfaceHolder = null;
	public LibVLC mLibVLC;
	public int mVideoHeight;
	private int mVideoWidth;
	private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    public int mSarNum;
    public int mSarDen;
    
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private static final int SURFACE_FROM_SETTINGS = 7;
    private int mCurrentSize = SURFACE_FROM_SETTINGS;
    
    private static final int SURFACE_SIZE = 3;

    String pref_aspect_ratio = "default";
    String pref_aspect_ratio_video = "default";
    
    private Map<Integer,String> mAudioTracksList;
    
    MediaPlayer.OnCompletionListener mOnCompletionListener;
    OnErrorListener mOnErrorListener;
	
	public VLCView(Context context){
		super(context);
		initVideoView();
	}

	public VLCView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		initVideoView();
	}
	
	public VLCView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
		initVideoView();
	}
	
	private void init(AttributeSet attrs) { 
	    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VideoView);
	    a.recycle();
	}
	
	private void initVideoView(){
		try {
			mLibVLC = Util.getLibVlcInstance();
        } catch (LibVlcException e) {
            //Log.d(TAG, "LibVLC initialisation failed");
            mLibVLC = null;
            return;
        }

		mVideoWidth = 1;
        mVideoHeight = 1;
        getHolder().setFormat(PixelFormat.RGBX_8888);
        getHolder().addCallback(mSHCallback);
        //Log.i(TAG, "Инициализация прошла успешно");
        EventHandler em = EventHandler.getInstance();
        em.addHandler(mNativeHandler);
        
       }
	
	private final SurfaceHolder.Callback mSHCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(format == PixelFormat.RGBX_8888){
              //  Log.d(TAG, "Pixel format is RGBX_8888");
            }
            else if(format == PixelFormat.RGB_565){
              //  Log.d(TAG, "Pixel format is RGB_565");
            }
            else if(format == ImageFormat.YV12){
              //  Log.d(TAG, "Pixel format is YV12");
            }
            else
              //  Log.d(TAG, "Pixel format is other/unknown");
            if(mLibVLC==null){
            	return;
            }
            mLibVLC.attachSurface(holder.getSurface(), VLCView.this, width, height);
               }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        	//Log.i("Debug", "surface created " + holder);
        	//Log.i("Debug", "surface created 1" + getHolder());
        	mSurfaceHolder = holder;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        	if(mLibVLC!=null){
        		mLibVLC.detachSurface();
        	}
        }
    };
    
     void readMedia(String str){
    	 if(mLibVLC==null){
    		 return;
    	 }
    	mLibVLC.readMedia(str, false);
    }
    
    @Override
    public void setSurfaceSize(int width, int height, int visible_width,
			int visible_height, int sar_num, int sar_den) {
    	if (width * height == 0)
            return;
    	mVideoHeight = height;
    	mVideoWidth = width;
    	mSarNum = sar_num;
    	mSarDen = sar_den;
    	mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth  = visible_width;
    	Message msg = mHandler.obtainMessage(SURFACE_SIZE);
    	mHandler.sendMessage(msg);
    }
    
    public Handler mNativeHandler = new Handler(){
    	 public void handleMessage(Message msg){
    		// Log.i("Debug", "" + msg);
    		 switch (msg.getData().getInt("event")) {
             case EventHandler.MediaPlayerPlaying:
                // Log.i("Debug", "MediaPlayerPlaying");
                 break;
             case EventHandler.MediaPlayerPaused:
                 //Log.i("Debug", "MediaPlayerPaused");
                 break;
             case EventHandler.MediaPlayerStopped:
                // Log.i("Debug", "MediaPlayerStopped");
                 break;
             case EventHandler.MediaPlayerEndReached:
            	 if(mOnCompletionListener != null){
            		 mOnCompletionListener.onCompletion(null);
            	 }
                 break;
             case EventHandler.MediaPlayerVout:
            	 //Log.i("Debug", "MediaPlayerVout");
                 break;
             case EventHandler.MediaPlayerPositionChanged:
            	 setOverlayProgress();
            	 break;
             case EventHandler.MediaPlayerEncounteredError:
            	 if(mOnErrorListener != null){
            		 mOnErrorListener.onError(null, 0, 0);
            	 }
                 break;
             default:
                 //Log.e("Debug", String.format("Event not handled (0x%x)", msg.getData().getInt("event")));
                 break;
         }
    	 }
    };
    
    public Handler mHandler = new Handler(){
    	@Override
        public void handleMessage(Message msg){
    		
    		switch (msg.what) {
            case SURFACE_SIZE:
            	changeSurfaceSize();
                break;
  
        }
    	}
    };
    
    public void changeSurfaceSize(int w, int h){
  
    }
   
    
    
private void changeSurfaceSize() {
    
	//Log.i("Debug", "changeSurfaceSize " + mCurrentSize);
	
    	  // get screen size
        int dw = ((Activity)getContext()).getWindow().getDecorView().getWidth();
        int dh = ((Activity)getContext()).getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (dw > dh && isPortrait || dw < dh && !isPortrait) {
            int d = dw;
            dw = dh;
            dh = d;
        }

        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            //Log.e("Debug", "Invalid surface size " + dw + " " + dh);
            clear();
            return;
        }

        // compute the aspect ratio
        double ar, vw;
        double density = (double)mSarNum / (double)mSarDen;
        if (density == 1.0) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double)mVideoVisibleWidth / (double)mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * density;
            ar = vw / mVideoVisibleHeight;
        }
        
        //
        //(displayHeight/displayWidth)*(arWidth/arHeight)
        
        //

        // compute the display aspect ratio
        double dar = (double) dw / (double) dh;
        double mult = dar;
        
        
        
        if(pref_aspect_ratio.equals("4:3")){
        	mult = 4/3.;
        }else if(pref_aspect_ratio.equals("11:9")){
        	mult = 11/9.;
        }else if(pref_aspect_ratio.equals("16:9")){
        	mult = 16/9.;
        };
        
        ar = ar/mult*dar;
        
        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
            	if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_FIT_HORIZONTAL:
            	dh = (int) (dw / ar);
                break;
            case SURFACE_FIT_VERTICAL:
            	dw = (int) (dh * ar);
                break;
            case SURFACE_FILL:
            	 break;
            case SURFACE_16_9:
            	 ar = 16.0 / 9.0;
                ar = ar/mult*dar;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_4_3:
            	ar = 4.0 / 3.0;
                ar = ar/mult*dar;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_ORIGINAL:
            	dh = mVideoVisibleHeight;
                dw = (int) vw;
                break;
            case SURFACE_FROM_SETTINGS:
            //	Log.i("Debug", "SURFACE_FROM_SETTINGS");
            	if(pref_aspect_ratio_video.equals("default")){
            		if (dar < ar)
                        dh = (int) (dw / ar);
                    else
                        dw = (int) (dh * ar);
            		break;
            	}
            	if(pref_aspect_ratio_video.equals("fullscreen")){
            		break;
            	}
            	if(pref_aspect_ratio_video.equals("4:3")){
            		 ar = 4.0 / 3.0;
            		 ar = ar/mult*dar;
                     if (dar < ar)
                         dh = (int) (dw / ar);
                     else
                         dw = (int) (dh * ar);
                    
            		break;
            	}
            	if(pref_aspect_ratio_video.equals("16:9")){
            		ar = 16.0 / 9.0;
            		ar = ar/mult*dar;
                    if (dar < ar)
                        dh = (int) (dw / ar);
                    else
                        dw = (int) (dh * ar);
            		break;
            	}
                break;
        }

        // force surface buffer size
        if(mSurfaceHolder!=null){
        	mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
        }else if(getHolder()!=null){
        	getHolder().setFixedSize(mVideoWidth, mVideoHeight);
        }
        

        // set display size
        LayoutParams lp = (LayoutParams) getLayoutParams();
        lp.width  = dw * mVideoWidth / mVideoVisibleWidth;
        lp.height = dh * mVideoHeight / mVideoVisibleHeight;
        setLayoutParams(lp);

        // set frame size (crop if necessary)
       /* lp = mSurfaceFrame.getLayoutParams();
        lp.width = dw;
        lp.height = dh;
        mSurfaceFrame.setLayoutParams(lp);*/

       invalidate();
    }

private void setOverlayProgress(){
	 if (mLibVLC == null) {
         return;
     }
	 if(mVideoController != null){
		 mVideoController.showProgress();
		 }


	 return;
}
    
    public boolean isPlaying(){
    	 if(mLibVLC==null){
    		 return false;
    	 }
    	return mLibVLC.isPlaying();
    }
    
    public void play(){
    	 if(mLibVLC==null){
    		 return;
    	 }
    	mLibVLC.play();
    }
    
    public void pause(){
    	 if(mLibVLC==null){
    		 return;
    	 }
    	mLibVLC.pause();
    }
    
    @Override
    public void stop(){
    	 if(mLibVLC==null){
    		 return;
    	 }
    	mLibVLC.stop();
    }
    
    public void finalize(){
    	mHandler.removeMessages(SURFACE_SIZE);
    	 if(mLibVLC==null){
    		 return;
    	 }
    	mLibVLC.finalize();
    }
    
    public void clear(){
    	setSurfaceSize(1,1,1,1,0,0);
    }

	@Override
	public void setVideoAndStart(String address) {
		// TODO Auto-generated method stub
		clear();
		 if(mLibVLC==null){
			 //Log.e("Debug", "Должно появиться сообщение");
				((VideoActivity)getContext()).showInfo("Архитектура не поддерживается \nПопробуйте другой плеер");
				((VideoActivity)getContext()).showOverlay(false);
    		 return;
    	 }
		mLibVLC.readMedia(address);
		
		if(mVideoController != null){
			Log.i("Debug", "Проверить число треков");
			 mVideoController.checkTrack();
			 }
	}

	@Override
	public void setTVController(TVController tc) {
		// TODO Auto-generated method stub
		mTVController = tc;
		mTVController.setVideo(this);
	}
	
	@Override
	public void setMap(Map<String, Object> map){
		//Log.i("Debug", "setMap");
		mMap = map;
		if(mTVController != null){
			mTVController.setMap(mMap);
		}
		if(mVideoController != null){
			mVideoController.setMap(mMap);
		}
		
		 SharedPreferences prefs;
	     prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
	     pref_aspect_ratio = prefs.getString("aspect_ratio", "default");
	     if(mMap.get("type").equals("video")){
	    	// Log.i("Debug", "video");
	    	 	pref_aspect_ratio_video = prefs.getString("aspect_ratio_video", "default");
	        }else{
	        	pref_aspect_ratio_video = prefs.getString("aspect_ratio_tv", "default");
	        	//Log.i("Debug", "channel" );
	        }
	  //   Log.i("Debug", pref_aspect_ratio_video);
	        
		
	}
	
	@Override
	 public boolean dispatchKeyEvent (KeyEvent event){
		if(mTVController!=null){
    		return mTVController.dispatchKeyEvent(event);
    	}
		if(mVideoController!=null){
    		return mVideoController.dispatchKeyEvent(event);
    	}
		 return true;
	 }

	@Override
	public void setVideoController(VideoController vc) {
		// TODO Auto-generated method stub
		mVideoController = vc;
		mVideoController.setVideo(this);
	}

	@Override
	public boolean showOverlay() {
		setOverlayProgress();
		return true;
	}

	@Override
	public boolean hideOverlay() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getProgress() {
		// TODO Auto-generated method stub
		 if(mLibVLC==null){
    		 return 0;
    	 }
		return (int) mLibVLC.getTime();
	}

	@Override
	public int getLeight() {
		// TODO Auto-generated method stub
		 if(mLibVLC==null){
    		 return 0;
    	 }
		
		return (int) mLibVLC.getLength();
	}

	@Override
	public int getTime() {
		// TODO Auto-generated method stub
		 if(mLibVLC==null){
    		 return 0;
    	 }
		return (int) mLibVLC.getTime();}

	@Override
	public void setTime(int time) {
		// TODO Auto-generated method stub
		 if(mLibVLC==null){
    		 return;
    	 }
		mLibVLC.setTime(time);
	}

	@Override
	public int changeSizeMode() {
		// TODO Auto-generated method stub
		mCurrentSize++;
		if(mCurrentSize>SURFACE_FROM_SETTINGS){
			mCurrentSize = 0;
		}
		 setSurfaceSize(mVideoWidth, mVideoHeight,
				 		mVideoVisibleWidth, mVideoVisibleHeight, 
				 		mSarNum, mSarDen) ;
		return mCurrentSize;
	}

	@Override
	public int getAudioTracksCount() {
		mAudioTracksList = mLibVLC.getAudioTrackDescription();
		// TODO Auto-generated method stub
		 if(mLibVLC==null){
    		 return 0;
    	 }
		return mLibVLC.getAudioTracksCount();
	}

	@Override
	public String changeAudio() {
		 if(mLibVLC==null){
    		 return null;
    	 }
		// TODO Auto-generated method stub
		
		int i = 0;
        int listPosition = 0;
        
        ArrayList<Map.Entry<Integer,String>> list = new ArrayList<Map.Entry<Integer,String>>();
        
      //  Log.i("Debug", "size " + mAudioTracksList.entrySet().size());
        
		 for(Map.Entry<Integer, String> entry :  mAudioTracksList.entrySet()){

			 list.add(entry);
			 
             if(entry.getKey() == mLibVLC.getAudioTrack()){
                 listPosition = i;
             }
             i++;
		 }
		// Log.i("Debug", "listPosition " + listPosition);
		 int nextPosition = listPosition + 1;
		 if(nextPosition >= mAudioTracksList.entrySet().size()){
			 nextPosition = 0;
		 }
		 mLibVLC.setAudioTrack(list.get(nextPosition).getKey());
		 
		 return list.get(nextPosition).getValue();
		
	}

	@Override
	public int getSpuTracksCount() {
		// TODO Auto-generated method stub
		 if(mLibVLC == null){
    		 return 0;
    	 }
		return mLibVLC.getSpuTracksCount();
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		// TODO Auto-generated method stub
		mOnCompletionListener = listener;
	}
	
	public void videoComplete(){
		if(mOnCompletionListener!=null){
			mOnCompletionListener.onCompletion(null);
		}
	}
	
	@Override
	public int changeOrientation() {
		   //Log.i("Debug", "AVideoView configuration change");
		   this.clear();
		return 0;
	}

	@Override
	public void end() {
		//mLibVLC.destroy();
		
		if(mTVController != null){
			mTVController.end();
		}
		if(mVideoController != null){
			mVideoController.end();
		}
		
		EventHandler em = EventHandler.getInstance();
        em.removeHandler(mNativeHandler);
	}

	@Override
	public void setOnErrorListener(OnErrorListener l) {
		mOnErrorListener = l;
	}

	@Override
	public String changeSubtitle() {
		// TODO Auto-generated method stub
		return "Субтитры";
	}
	

}
