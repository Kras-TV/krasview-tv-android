package ru.krasview.kvlib.widget;

import java.util.HashMap;

import ru.krasview.kvlib.interfaces.ViewProposeListener;
import ru.krasview.kvlib.indep.HTTPClient;
import com.example.kvlib.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SerialDescription extends RelativeLayout {
	protected ImageView image;
	protected TextView text;
	protected Button button;
	protected TextView name;

	public SerialDescription(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init1();
	}

	public SerialDescription(Context context, AttributeSet attrs) {
		super(context, attrs);
		init1();
	}

	public SerialDescription(Context context) {
		super(context);
		init1();
	}

	private void init1(){
		LayoutInflater ltInflater = ((Activity)getContext()).getLayoutInflater();
		ltInflater.inflate(R.layout.kv_serial_description, this, true);
		image = (ImageView)findViewById(R.id.image);
		text = (TextView)findViewById(R.id.text);
		name = (TextView)findViewById(R.id.name);
		button = (Button)findViewById(R.id.button);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setTag(Object tag) {
		super.setTag(tag);
		HashMap<String,Object> hm = (HashMap<String,Object>)tag;
		new ImageAsyncTask().execute((String)hm.get("img_uri"));
		text.setText((String)hm.get("description"));
		name.setText((CharSequence)hm.get("show_name"));
	}

	class ImageAsyncTask extends AsyncTask<String,Void,Bitmap> {
		@Override
		protected Bitmap doInBackground(String... params) {
			return HTTPClient.getImage(params[0]);
		}
		
		@Override 
		protected void onPostExecute(Bitmap bmp){
			image.setImageBitmap(bmp);
		}
	}

	protected ViewProposeListener mViewProposeListener;

	public void setViewProposeListener(ViewProposeListener listener) {
		mViewProposeListener = listener;
	}

	@Override public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
			KeyEvent e = new KeyEvent(event.getAction(), KeyEvent.KEYCODE_DPAD_CENTER);
			return super.dispatchKeyEvent(e);
		}
		return super.dispatchKeyEvent(event);
	}
}
