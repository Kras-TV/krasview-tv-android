package ru.krasview.tv.player;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.krasview.kvlib.indep.AuthAccount;
import ru.krasview.kvlib.indep.HTTPClient;
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.secret.ApiConst;

import com.example.kvlib.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TVController extends FrameLayout {
	VideoInterface mVideo;
	Map<String, Object> mMap;

	ProgressBar mProgressBar;
	TextView mTitle;
	TextView mTime;
	TextView mInfo;
	ImageButton mStop;
	ImageButton mSize;

	private static final int SURFACE_BEST_FIT = 0;
	private static final int SURFACE_FIT_HORIZONTAL = 1;
	private static final int SURFACE_FIT_VERTICAL = 2;
	private static final int SURFACE_FILL = 3;
	private static final int SURFACE_16_9 = 4;
	private static final int SURFACE_4_3 = 5;
	private static final int SURFACE_ORIGINAL = 6;
	private static final int SURFACE_FROM_SETTINGS = 7;

	Handler mHandler = new TVControllerHandler(this);

	static final int GET_INFO = 1;

	public TVController(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}
	public TVController(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}
	public TVController(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}


	private void init() {
		LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.kv_controller_tv, this, true);
		mTitle = (TextView)findViewById(R.id.progress_overlay_tv_name);
		mTime = (TextView)findViewById(R.id.progress_overlay_tv_time);
		mInfo = (TextView)findViewById(R.id.player_overlay_info);
		mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
		mStop = (ImageButton)findViewById(R.id.progress_overlay_tv_pause);
		mStop.setOnClickListener(listener);
		mSize = (ImageButton)findViewById(R.id.progress_overlay_tv_size);
		mSize.setOnClickListener(listener);

		mProgressBar.setProgress(0);
		mTitle.setText("");
		mTime.setText("");
	}

	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//switch(arg0.getId()){
			if(arg0.getId()==R.id.progress_overlay_tv_pause) {
				//case R.id.progress_overlay_tv_pause:
				if(mVideo == null) {
					return;
				}
				if(mVideo.isPlaying()) {
					mVideo.stop();
					mStop.setBackgroundResource(R.drawable.ic_new_play);
				} else {
					mVideo.play();
					mStop.setBackgroundResource(R.drawable.ic_stop);
				}
				//break;
			} else if(arg0.getId()==R.id.progress_overlay_tv_size) {
				//case R.id.progress_overlay_tv_size:
				String msg = "";
				switch(mVideo.changeSizeMode()) {
				case SURFACE_BEST_FIT:
					msg = "Оптимально";
					break;
				case SURFACE_FIT_HORIZONTAL:
					msg = "По горизонтали";
					break;
				case SURFACE_FIT_VERTICAL:
					msg = "По вертикали";
					break;
				case SURFACE_FILL:

					msg = "Заполнение";
					break;
				case SURFACE_16_9:
					msg = "16 на 9";
					break;
				case SURFACE_4_3:
					msg = "4 на 3";
					break;
				case SURFACE_ORIGINAL:
					msg = "По центру";
					break;
				case SURFACE_FROM_SETTINGS:
					msg = "Из настроек";
					break;
				}

				((VideoActivity)getContext()).showInfo(msg, 1000);
				//break;
			}
		}
	};

	/*private void showInfo(CharSequence text, int duration) {
	    mInfo.setVisibility(View.VISIBLE);
	    mInfo.setText(text);
	    mHandler.removeMessages(FADE_OUT_INFO);
	    mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
	}
	private void showInfo(CharSequence text) {
	    mInfo.setVisibility(View.VISIBLE);
	    mInfo.setText(text);
	    mHandler.removeMessages(FADE_OUT_INFO);
	}*/

	public void setMap(Map<String, Object> map) {
		mMap = map;
		mProgressBar.setProgress(85);
		mHandler.removeMessages(GET_INFO);
		mHandler.sendEmptyMessage(GET_INFO);
		mVideo.setVideoAndStart((String) map.get("uri"));
	}

	public void setVideo(VideoInterface video) {
		mVideo = video;
		mVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				((VideoActivity)getContext()).showInfo("Невозможно воспроизвести поток");
				((VideoActivity)getContext()).showOverlay(false);
				return true;
			}
		});
		if(mVideo.getClass().equals(VLCView.class)) {
			Drawable d = getResources().getDrawable(R.drawable.po_seekbar);
			mProgressBar.setProgressDrawable(d);
		} else if(mVideo.getClass().equals(AVideoView.class)) {

		}
	}

	GetInfoTask getInfoTask;

	public void getInfo() {
		Log.i("Debug", "Получить телепрограмму");

		if (mMap == null) {
			return;
		}

		if(getInfoTask != null) {
			getInfoTask.cancel(true);
		}
		getInfoTask = new GetInfoTask();
		getInfoTask.execute((String)mMap.get("id"));
	}

	private static class TVControllerHandler extends Handler {
		TVController mTVController;

		TVControllerHandler(TVController tv) {
			super();
			mTVController = tv;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			//  case TVController.FADE_OUT_INFO:
			// 	mTVController.fadeOutInfo();
			//	break;
			case TVController.GET_INFO:
				mTVController.getInfo();
				removeMessages(GET_INFO);
				this.sendEmptyMessageDelayed(GET_INFO, 60*1000);
				break;
			}
		}
	}

	/*	 private void fadeOutInfo() {
		        mInfo.setVisibility(View.INVISIBLE);
		    }*/


	class GetInfoTask extends AsyncTask<String, Void, Node> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// mProgressBar.setProgress(0);
			//  mTitle.setText("");
			// mTime.setText("");
		}

		@Override
		protected Node doInBackground(String... params) {
			String str = HTTPClient.getXML(ApiConst.RECORD, "id=" + params[0], AuthAccount.AUTH_TYPE_TV);
			Document document = Parser.XMLfromString(str);
			if(document == null) {
				return null;
			}
			document.normalizeDocument();
			NodeList nList = document.getElementsByTagName("item");
			if(nList.getLength() == 0) {
				return null;
			}

			Node locNode = nList.item(0);

			return locNode;
		}

		@Override
		protected void onPostExecute(Node result) {
			super.onPostExecute(result);
			if(result==null) {
				return;
			}
			mTitle.setText(Parser.getValue("name", result));
			mTime.setText(Parser.getValue("time", result));

			int i;
			try {
				i = Integer.parseInt(Parser.getValue("percent", result));
			} catch(Exception e) {
				i=0;
			}
			mProgressBar.setProgress(i);
		}
	}

	public boolean dispatchKeyEvent (KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN) {
			switch(event.getKeyCode()) {
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				listener.onClick(mStop);
				return true;
			case KeyEvent.KEYCODE_MEDIA_STOP:
				mVideo.stop();
				mStop.setBackgroundResource(R.drawable.ic_new_play);
				return true;
			}
		}
		return false;
	}

	public void end() {}
}
