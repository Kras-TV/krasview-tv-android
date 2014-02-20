package ru.krasview.tv;

import ru.krasview.kvlib.animator.NewAnimator;
import ru.krasview.kvlib.indep.AuthAccount;
import ru.krasview.kvlib.indep.HTTPClient;
import ru.krasview.kvlib.indep.HeaderAccount;
import ru.krasview.kvlib.indep.consts.IntentConst;
import ru.krasview.kvlib.indep.consts.TypeConsts;
import ru.krasview.kvlib.indep.ListAccount;
import ru.krasview.kvlib.indep.consts.RequestConst;
import ru.krasview.kvlib.indep.SearchAccount;
import ru.krasview.kvlib.interfaces.OnLoadCompleteListener;
import ru.krasview.kvlib.interfaces.PropotionerView;
import ru.krasview.kvlib.widget.List;
import ru.krasview.kvlib.widget.NavigationViewFactory;
import ru.krasview.secret.ApiConst;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;

import com.example.kvlib.R;

public class MainActivity extends KVSearchAndMenuActivity{

	NewAnimator animator;
	String start = TypeConsts.MAIN;
	FrameLayout layout;	
	
//OnCreate	
	
	@Override
    public void onCreate(Bundle savedInstanceState){
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Intent intent = getIntent();
		if(intent != null && intent.getAction() != null 
				&& intent.getAction().equals(IntentConst.ACTION_FROM_LAUNCHER)){
			ListAccount.fromLauncher = true;
		}else{
			ListAccount.fromLauncher = false;
		}
		super.onCreate(savedInstanceState);
		if(ListAccount.fromLauncher){
        	overridePendingTransition(ru.krasview.tv.R.anim.anim_enter_right, 
        			ru.krasview.tv.R.anim.anim_leave_left);
        }
		
		//Здесь обработка запуска из ланчера, поэтому здесь идет 
		//сравнение со старыми адресами
		if(ListAccount.fromLauncher){
			String address = getIntent().getExtras().getString("address");
			if(address.equals(ApiConst.TV)){
				start = TypeConsts.TV;
				getSupportActionBar().setTitle("Телевидение");
			}else if(address.equals(ApiConst.OLD_ALL_SHOW)){
				start = TypeConsts.ALL_SHOW;
				getSupportActionBar().setTitle("Сериалы");
			}else if(address.equals(ApiConst.OLD_ALL_ANIME)){
				start = TypeConsts.ALL_ANIME;
				getSupportActionBar().setTitle("Аниме");
			}
		}
		
		styleActionBar();
		
		setContentView(R.layout.activity_main_new);	
		layout = (FrameLayout) findViewById(R.id.root);
		animator = new NewAnimator(this, new NavigationViewFactory());
		layout.addView(animator);
		
		setSearchWidget();
		styleBackground();
		getPacketAndStart();
	}	
	
	ProgressDialog pd;
	private void getPacketAndStart(){
		getPrefs();	
		if(ListAccount.fromLauncher){
			animator.init(start);
			return;
		}
		//получение данных о подключенном пакете
				pd = new ProgressDialog(this);
				pd.setTitle("Подождите");
				pd.setCancelable(false);
				if(!ListAccount.fromLauncher){
					pd.show();
				}
				
				
				HTTPClient.getXMLAsync(ApiConst.USER_PACKET, "hash=" + AuthAccount.getInstance().getTvHash(), 
						new OnLoadCompleteListener(){

					@Override
					public void loadComplete(String result){
						if(!ListAccount.fromLauncher){
							pd.dismiss();
						}
						if(!result.equals("Бесплатный")){
							HeaderAccount.hh();
						}
						animator.init(start);
					}

					@Override
					public void loadComplete(String address, String result){
					}
				});
	};
	
	//настройка actionbar-a 
		private void styleActionBar(){
			if(ListAccount.fromLauncher){
				getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.drawable.dark_header)); 
				getSupportActionBar().setIcon(R.drawable.kv_logo);
				getSupportActionBar().setLogo(R.drawable.kv_logo);
		}else{
				getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background)); 
			}	
		}
		
		//настройка фона
		@SuppressWarnings("deprecation")
		private void styleBackground(){
			if(ListAccount.fromLauncher){
			final GradientDrawable grad;
			grad = new GradientDrawable(Orientation.TL_BR, new int[] { 	getResources().getColor(R.color.black_2), //светлый в центре
			getResources().getColor(R.color.black_1)});//темный с краю
			grad.setGradientType(GradientDrawable.RADIAL_GRADIENT);
			grad.setGradientRadius(100);
			grad.setGradientCenter(0.5f, 0.5f);
			layout.setBackgroundDrawable(grad);
			final ViewTreeObserver observer = layout.getViewTreeObserver();
		    observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
				@Override
				public void onGlobalLayout() {
					grad.setGradientRadius(layout.getWidth());
				}
		    } );
		    }else{
				layout.setBackgroundColor(Color.rgb(20, 20, 20));
			}
		}
		
		SharedPreferences prefs;
		
		private void getPrefs(){
			
			prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			
			String tv_pl = prefs.getString("video_player_tv", "VLC");
			if(tv_pl.equals("Стандартный плеер")){
				prefs.edit().putString("video_player_tv", "std").commit();
			}
			tv_pl = prefs.getString("video_player_serial", "VLC");
			if(tv_pl.equals("Стандартный плеер")){
				prefs.edit().putString("video_player_serial", "std").commit();
			}
			
			AuthAccount.getInstance().setType(
					prefs.getInt("pref_auth_type", AuthAccount.AUTH_TYPE_UNKNOWN));
			if(account.isUnknownAccount()){
				prefs.edit().putBoolean("pref_now_logout", true).commit();
				Intent a = new Intent(this, MainAuthActivity.class);
				startActivity(a);
				this.finish();
				return;
			}
			prefs.edit().putBoolean("pref_now_logout", false)
			.putInt("pref_last_interface", MainAuthActivity.INTERFACE_KRASVIEW).commit();  
			HTTPClient.setContext(this);
			HTTPClient.setExitListener(this);
			AuthAccount.getInstance().setLogin(prefs.getString("pref_login", ""));
			AuthAccount.getInstance().setPassword(prefs.getString("pref_password", ""));
			AuthAccount.getInstance().setHash(prefs.getString("pref_hash", "1"));	
			AuthAccount.getInstance().setTvHash(prefs.getString("pref_hash_tv", "1"));
			}
	
	String pref_orientation = "default";
	@Override
	public void onResume(){
		super.onResume();
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		pref_orientation = prefs.getString("orientation", "default");
		if(pref_orientation.equals("default")){
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    	}else if(pref_orientation.equals("album")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	}else if(pref_orientation.equals("book")){
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	}
	}

	@Override
    public void onBackPressed() {
        super.onBackPressed();
        if(ListAccount.fromLauncher){
        	overridePendingTransition(ru.krasview.tv.R.anim.anim_enter_left, 
        			ru.krasview.tv.R.anim.anim_leave_right);
        }
    }
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK){
			return;
		}
		switch(requestCode){
		case RequestConst.REQUEST_CODE_BILLING:
			((PropotionerView)animator.getCurrentView()).enter();
			break;
		case RequestConst.REQUEST_CODE_VIDEO:
			if (data == null) {return;}
		    int index = data.getIntExtra("index", 0);
		    ListAccount.currentList.setSelection(index + ((List)ListAccount.currentList).getAdapter().getConstDataCount());
			break;
		}
	}
	
	public boolean dispatchKeyEvent (KeyEvent event){
		
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT&&ListAccount.fromLauncher&&animator.hasFocus()){
			return this.dispatchKeyEvent(new KeyEvent(event.getAction(), KeyEvent.KEYCODE_BACK));
		}
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			boolean result;
			if(animator.getVisibility() == View.VISIBLE){
				result = animator.dispatchKeyEvent(event);			
			}else{
				result = searchFragment.dispatchKeyEvent(event);	
			}
			if(result){
				return result;
			}
		}
		return super.dispatchKeyEvent(event);		
	}
	
	@Override
	public void onError() {
		exit();
	}
	
	@Override
	protected final void requestFocus(){
		animator.requestFocus();
	};
	
	@Override
	protected final void exit(){
		HeaderAccount.shh();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		prefs.edit().putBoolean("pref_now_logout", true).putString("pref_hash", "").putString("pref_hash_tv", "").commit();
		CookieSyncManager.createInstance(this.getApplication());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
		Intent a = new Intent(this, MainAuthActivity.class);
		startActivity(a);
		this.finish();
	}
	
	@Override
	protected final void home(){
		if(ListAccount.fromLauncher){
			finish();
			return;
		}
		if(animator.getVisibility() == View.VISIBLE){
			animator.home();
		}else{
			editsearch.setText("");
            editsearch.clearFocus();
            SearchAccount.search_string = null;
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);
			searchFragment.home();
		}
	}
	
	@Override
	protected final void refresh(){
		if(animator.getVisibility() == View.VISIBLE){
			animator.refresh();
		}else{
			searchFragment.refresh();
		}
	}
	
	@Override
	protected final void setSearch(boolean a){
		if(a){
			animator.setVisibility(View.GONE);
			searchHost.setVisibility(View.VISIBLE);
		}else{
			animator.setVisibility(View.VISIBLE);
			searchHost.setVisibility(View.GONE);
		}
	} 
}
