package ru.krasview.tv.player;

import java.util.Map;

import org.videolan.vlc.Util;

import ru.krasview.kvlib.indep.ListAccount;

import com.example.kvlib.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
public class VideoActivity extends Activity {
	
	RelativeLayout mFrame;
	SurfaceView mVideoSurface;
	RelativeLayout mOverlayFrame;
	
	int current;	
	String mLocation;
	String mType;
	CharSequence mTitle_value;
	Bitmap mIcon_value;
	
	TextView mInfo;
	TextView mTitle;
	ImageView mIcon;
	
	private final Handler mHandler = new VideoPlayerHandler(this);
	
	final static String ACTION_VIDEO_LIST = "ru.krasview.tv.PLAY_VIDEO_LIST";
	
    private static final int FADE_OUT = 1;
    private static final int FADE_OUT_INFO = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		overridePendingTransition(ru.krasview.tv.R.anim.anim_enter_right, ru.krasview.tv.R.anim.anim_leave_left);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kv_b_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mFrame = (RelativeLayout)findViewById(R.id.player_surface_frame);
        mOverlayFrame = (RelativeLayout)findViewById(R.id.overlay_frame);
        mInfo = (TextView) findViewById(R.id.player_overlay_info);
        mTitle = (TextView)findViewById(R.id.player_overlay_title);      
        mIcon = (ImageView)findViewById(R.id.player_overlay_icon); 
        initLayout();
    }
	
	@SuppressWarnings("unchecked")
	private void initLayout(){
		Map<String, Object> map;
		  if (getIntent().getAction() != null && getIntent().getAction() == ACTION_VIDEO_LIST){
	        	current = getIntent().getIntExtra("index", 0);   	
	        }else{
	        	current =0;
	        }
		if(ListAccount.adapterForActivity == null){
			map = null;
			return;
		}else{
			map = (Map<String, Object>)ListAccount.adapterForActivity.getItem(current);
		}
        mType = (String) map.get("type");
		getPrefs();
		
		if(mType.equals("video")){
       	 	pref_video_player = pref_video_player_serial;
        }else if(mType.equals("channel")||mType.equals("tv_record")){
       	 	pref_video_player = pref_video_player_tv;
        }
       
        
        if(mVideoSurface == null){
       	 if(pref_video_player.equals("VLC")){
       		 mVideoSurface = new VLCView(this);
       	 }else{
       		 mVideoSurface = new AVideoView(this);
       	 }     		
    		mFrame.addView(mVideoSurface);
    	}
		
		
		if (mType == null){
        	return;
        }
		if(mType.equals("channel")){
			TVController v = new TVController(this);
			((VideoInterface)mVideoSurface).setTVController(v);
			mOverlayFrame.addView(v);
		}else{
			VideoController v = new VideoController(this);			
			((VideoInterface)mVideoSurface).setVideoController(v);
			mOverlayFrame.addView(v);
		}
		
		
		((VideoInterface)mVideoSurface).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				showOverlay(false);
				KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
				VideoActivity.this.dispatchKeyEvent(event);
			}
		});
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        
        if (getIntent().getAction() != null && getIntent().getAction() == ACTION_VIDEO_LIST){
        	current = getIntent().getIntExtra("index", 0);
        	
        }
        getPrefs();
        if(pref_orientation.equals("default")){
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    	}else if(pref_orientation.equals("album")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	}else if(pref_orientation.equals("book")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	}
        
        if(getIntent() != null){
        	start(getIntent().getBooleanExtra("request_time", false));
        }
                
   }
	
	
	
	@Override 
	protected void onStart(){
		super.onStart();
		
	}
	
	String pref_video_player_serial;
	String pref_video_player_tv;
	String pref_video_player;
	String pref_orientation;
	public static SharedPreferences prefs;
	private void getPrefs(){
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		pref_video_player_serial = prefs.getString("video_player_serial", "Стандартный плеер");
		pref_video_player_tv = prefs.getString("video_player_tv", "VLC");
		pref_orientation = prefs.getString("orientation", "default");
	}
	
	@SuppressWarnings("unchecked")
	private void start(boolean request_time){
		hideInfo();
		Map<String, Object> map = (Map<String, Object>)ListAccount.adapterForActivity.getItem(current);
		//map.put("request_time", request_time);
		map.put("rt", request_time);
		mLocation = (String) map.get("uri");
    		
    	mTitle_value = (CharSequence) map.get("name");
    	if(map.get("image")!=null&&map.get("image").getClass().equals(Bitmap.class))
    	{
    		mIcon_value = (Bitmap)map.get("image");
    	}
    	else{
    		mIcon_value = null;
    	}
    	if(mLocation == null && mType == null){
    		return;
    	}
    	
    	 mTitle.setText(mTitle_value);
         if(mIcon_value != null){
         	mIcon.setImageBitmap(mIcon_value);
         	mIcon.setVisibility(View.VISIBLE);
         }
        // Log.i("Debug", "VideoActivity mVideoSurface = " + mVideoSurface);
         ((VideoInterface)mVideoSurface).setMap(map);
     	//((VideoInterface)mVideoSurface).setVideoAndStart(mLocation); 
         showOverlay();
	}
	
	 private static class VideoPlayerHandler extends Handler{
	     
		 VideoActivity mActivity;
		 
		 VideoPlayerHandler(VideoActivity activity){
			 mActivity = activity;
		 }
		 

	        @Override
	        public void handleMessage(Message msg) {
	        	
	            switch (msg.what) {
	         
	                case FADE_OUT:
	                	mActivity.hideOverlay();
	                	
	                case FADE_OUT_INFO:
	                	mActivity.hideInfo();
	               
	            }
	        }
	    };
	    
	    
	    @Override
	    public void onStop(){
	    	super.onStop();
	    	((VideoInterface)mVideoSurface).stop();
	    }
	    
	    @Override
	    public boolean dispatchKeyEvent (KeyEvent event){
	    	showOverlay();
	    	
	    if(event.getKeyCode()== KeyEvent.KEYCODE_DPAD_CENTER){
			return true;}
	    	
	    	if(event.getAction() == KeyEvent.ACTION_DOWN){
	    		switch(event.getKeyCode()){
	    			case KeyEvent.KEYCODE_DPAD_DOWN:
	    				current++;
	    				if(current>=ListAccount.adapterForActivity.getCount()){
	    					current=0;
	    				}
	    				start(false);
	    				return true;
	    			case KeyEvent.KEYCODE_DPAD_UP:
	    				current--;
	    				if(current<0){
	    					current=ListAccount.adapterForActivity.getCount()-1;
	    				}
	    				start(false);
	    				return true;
	    				
	    			
	    		}
	    		
	    		
	    	}
	    	if(event.getAction() == KeyEvent.ACTION_UP){
	    		switch(event.getKeyCode()){
	    			case KeyEvent.KEYCODE_BACK:
	    			this.onBackPressed();
	    			return true;
	    			case KeyEvent.KEYCODE_DPAD_LEFT:
	    				
	    				if(mType.equals("channel")){
	    				    this.onBackPressed();
	    				    return true;
	    				}
	    			}
	    		}
	    	return mVideoSurface.dispatchKeyEvent(event);
			//return super.dispatchKeyEvent(event);    	
	    }
	    
	   public void hideOverlay(){
	    	mOverlayFrame.setVisibility(View.GONE);
	    	dimStatusBar(true);
	   }
	   
	   @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	    private void dimStatusBar(boolean dim) {
		   //false - show
	        if (!Util.isHoneycombOrLater() || !Util.hasNavBar())
	            return;
	        int layout = 0;
	        if (!Util.hasCombBar() && Util.isJellyBeanOrLater())
	           // layout = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
	         layout = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
	        if(mVideoSurface!=null){
	        mVideoSurface.setSystemUiVisibility(
	                (dim ? (Util.hasCombBar()
	                        ? View.SYSTEM_UI_FLAG_LOW_PROFILE
	                        : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
	                    : View.SYSTEM_UI_FLAG_VISIBLE) | layout);
	        }
	    }
	   
	   public void showOverlay(){
		   	   showOverlay(true);
	   }
	   
	   public void showOverlay(boolean hide){
		   mOverlayFrame.setVisibility(View.VISIBLE);
		   if(mVideoSurface!=null){
			   ((VideoInterface)mVideoSurface).showOverlay();
		   }
		   dimStatusBar(false);
		   mHandler.removeMessages(FADE_OUT);
		   if(hide){
			   mHandler.sendEmptyMessageDelayed(FADE_OUT, 3000);	  
		   }
	   }
	   
	   public boolean dispatchTouchEvent (MotionEvent ev){
		   showOverlay();
		   return super.dispatchTouchEvent(ev);
	   }
	   
	   @Override
	   public void onDestroy(){
		    super.onDestroy();
		    ((VideoInterface)mVideoSurface).end();
	   }
	   
	   @Override
	   public void onBackPressed(){
		   Intent intent = new Intent();
		    intent.putExtra("index", current);
		    setResult(RESULT_OK, intent);
		    super.onBackPressed();
		    overridePendingTransition(ru.krasview.tv.R.anim.anim_enter_left, ru.krasview.tv.R.anim.anim_leave_right);
	   }
	   
	   @Override
	   public void onConfigurationChanged(Configuration newConfig){
		   super.onConfigurationChanged(newConfig);
		   ((VideoInterface)mVideoSurface).changeOrientation();
	   }

	public void showInfo(String msg, int i) {
		// TODO Auto-generated method stub
		mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(msg);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, i);
	}
	
	public void showInfo(CharSequence text) {
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.removeMessages(FADE_OUT);
    }
	
	private void hideInfo(){
		mInfo.setVisibility(View.GONE);
	}
	  

}
