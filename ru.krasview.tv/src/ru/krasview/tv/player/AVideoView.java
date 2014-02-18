package ru.krasview.tv.player;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.VideoView;

public class AVideoView extends VideoView implements VideoInterface {
	
	TVController mTVController;
	VideoController mVideoController;
	Map<String, Object> mMap;
	MediaPlayer mMediaPlayer;
	
	int mVideoWidth = 100;
    int mVideoHeight = 100;
    
    int dw = 1;
    int dh = 1;
    
    boolean stopped = false;
	
	private static final int SHOW_PROGRESS = 2;
	private static final int SURFACE_SIZE = 3;
	
	 private static final int SURFACE_BEST_FIT = 0;
	    private static final int SURFACE_FIT_HORIZONTAL = 1;
	    private static final int SURFACE_FIT_VERTICAL = 2;
	    private static final int SURFACE_FILL = 3;
	    private static final int SURFACE_16_9 = 4;
	    private static final int SURFACE_4_3 = 5;
	    private static final int SURFACE_ORIGINAL = 6;
	    private static final int SURFACE_FROM_SETTINGS = 7;
	    private int mCurrentSize = SURFACE_FROM_SETTINGS;
	    
	    String pref_aspect_ratio = "default";
	    String pref_aspect_ratio_video = "default";

	public AVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}
	
	public AVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}
	
	public AVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init(){
		setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
			  @Override
			  public void onPrepared(MediaPlayer pMp) {
				  
				  mMediaPlayer = pMp;//use a global variable to get the object
				  mVideoWidth = pMp.getVideoWidth();
				  mVideoHeight = pMp.getVideoHeight();
				    
				  changeSize();
			  }
			});
		this.setFocusable(false);
		this.setClickable(false);

	}
	
	private void getPrefs(){
		SharedPreferences prefs;
	     prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
	     pref_aspect_ratio = prefs.getString("aspect_ratio", "default");
	     if(mMap.get("type").equals("video")){

	    	 	pref_aspect_ratio_video = prefs.getString("aspect_ratio_video", "default");
	        }else{
	        	pref_aspect_ratio_video = prefs.getString("aspect_ratio_tv", "default");

	        }
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	
		if(dw*dh == 1){
			return;
		}
		setMeasuredDimension(dw, dh);

	}

	@Override
	public void setVideoAndStart(String address) {
		if(address == null){		
			address="null";
		}
		setVideoPath(address);
		if(!this.isPlaying()){
			start();
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		((VideoView)this).stopPlayback();
		stopped = true;
		}

	@Override
	public void setTVController(TVController tc) {
		// TODO Auto-generated method stub
		mTVController = tc;
		mTVController.setVideo(this);
	}
	@Override
	public void setMap(Map<String, Object> map){
		mMap = map;
		if(mTVController != null){
			mTVController.setMap(mMap);
		}
		if(mVideoController != null){
			mVideoController.setMap(mMap);
		}
		getPrefs();
		stopped = false;
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		
		if(stopped){
			String mLocation = (String) mMap.get("uri");
			stopped = false;
			this.setVideoAndStart(mLocation);
		}
		
		start();
	}

	@Override
	public void setVideoController(VideoController vc) {
		// TODO Auto-generated method stub
		mVideoController = vc;
		mVideoController.setVideo(this);
	}

	@Override
	public boolean showOverlay() {
		mHandler.removeMessages(SHOW_PROGRESS);
		mHandler.sendEmptyMessage(SHOW_PROGRESS);
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
		return this.getCurrentPosition();
	}

	@Override
	public int getLeight() {
		// TODO Auto-generated method stub
		 Log.i("Debug", "" + this.getDuration());
		return this.getDuration();
	}

	@Override
	public int getTime() {
		// TODO Auto-generated method stub
		return this.getCurrentPosition();
	}

	@Override
	public void setTime(int time) {
		// TODO Auto-generated method stub
		this.seekTo(time);
	}
	
	public Handler mHandler = new Handler(){
    	@Override
        public void handleMessage(Message msg){
    		switch (msg.what) {
            case SURFACE_SIZE:
            	//changeSurfaceSize();
                break;
            case SHOW_PROGRESS:
            	setOverlayProgress();
            	break;
        }
    	}
    };

    private void setOverlayProgress(){

   	 if(mVideoController != null){
   		 mVideoController.showProgress();
   		 }
   	 mHandler.removeMessages(SHOW_PROGRESS);
   	 Message msg = mHandler.obtainMessage(SHOW_PROGRESS);
   	 mHandler.sendMessageDelayed(msg, 1000);
   	 return;
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
	public int changeSizeMode() {
		mCurrentSize++;
		if(mCurrentSize>SURFACE_FROM_SETTINGS){
			mCurrentSize = 0;
		}
		
		changeSize();
		
		return mCurrentSize;
	}
	
	private void changeSize(){
		
		calcSize();
		getHolder().setFixedSize(dw, dh); 
        forceLayout();
        invalidate(); 
	}
	
	@SuppressWarnings("deprecation")
	private void calcSize(){
		
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if(currentapiVersion>=13){
			Point size = new Point();
			display.getSize(size);
			dw = size.x;
			dh = size.y;
		}else{
			dw = display.getWidth();
			dh = display.getHeight();
		}
			
		// dw = ((Activity)getContext()).getWindow().getDecorView().getWidth();
	     //dh = ((Activity)getContext()).getWindow().getDecorView().getHeight();
	   //  Log.i("Debug", "calc size " + dw + "x" + dh);
		
      boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
       if (dw > dh && isPortrait || dw < dh && !isPortrait) {
           int d = dw;
           dw = dh;
           dh = d;
       }
       
       if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
          
           return;
       }
       
       double ar = (double)mVideoWidth/(double)mVideoHeight;
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
		
  
       
		switch(mCurrentSize){
		 	
		 	case SURFACE_BEST_FIT: 
		 		if(ar>dar){
		 			dh = (int) (dw/ar);
		 		}else{
		 			dw = (int)(dh*ar);
		 		}
		 	case SURFACE_FILL:		 		
		 		break;
		 	case SURFACE_ORIGINAL:
		 		dw = mVideoWidth;
		 		dh = mVideoHeight;
		 		break;
		 	case SURFACE_4_3:
		 		ar = 4.0 / 3.0;
               ar = ar/mult*dar;
               if (dar < ar)
                   dh = (int) (dw / ar);
               else
                   dw = (int) (dh * ar);
		 		break;
		 		
		 	case SURFACE_16_9:
		 		ar = 16.0 / 9.0;
               ar = ar/mult*dar;
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
           case SURFACE_FROM_SETTINGS: 
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
	}

	@Override
	public int getAudioTracksCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String changeAudio() {
		// TODO Auto-generated method stub
		return "Следующая дорожка";
	}

	@Override
	public int getSpuTracksCount() {
		// TODO Auto-generated method stub
		
		return 0;
	}

	@Override
	public int changeOrientation() {
		changeSize();
		return 0;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		if(mTVController != null){
			mTVController.end();
		}
		if(mVideoController != null){
			mVideoController.end();
		}
		
		mHandler.removeMessages(SHOW_PROGRESS);
	}

	@Override
	public String changeSubtitle() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
