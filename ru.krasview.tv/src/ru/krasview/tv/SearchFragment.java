package ru.krasview.tv;

import ru.krasview.kvlib.animator.NewAnimator;
import ru.krasview.kvlib.interfaces.PropotionerView;
import ru.krasview.kvlib.widget.NavigationViewFactory;

import com.example.kvlib.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class SearchFragment extends Fragment implements OnTabChangeListener
{
	
	private final static String TAB_1 = "tab1";
	private final static String TAB_2 = "tab2";
	private final static String TAB_3 = "tab3";
	TabHost host;
	
	Context mContext;
	
	String search_str ="";
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
			host = (TabHost) inflater.inflate(R.layout.my_tab_host, null) ;
			host.setup();
			TabHost.TabSpec tabSpec;
	        
	        tabSpec = host.newTabSpec(TAB_1);
	        tabSpec.setContent(TabFactory);
	        tabSpec.setIndicator("Фильмы");
	        host.addTab(tabSpec);
	        
	        tabSpec = host.newTabSpec(TAB_2);
	        tabSpec.setContent(TabFactory);
	        tabSpec.setIndicator("Сериалы");
	        host.addTab(tabSpec);
	        
	        tabSpec = host.newTabSpec(TAB_3);
	        tabSpec.setContent(TabFactory);
	        tabSpec.setIndicator("Аниме");
	        host.addTab(tabSpec);
	        
	        host.setOnTabChangedListener(this);
	        
		    return  host;
		  }
	
	TabHost.TabContentFactory TabFactory = new TabHost.TabContentFactory() {

		@Override
		public View createTabContent(String tag) {
			
			if(mContext == null){
				return null;
			}
			if(tag.equals(TAB_1)){
				NewAnimator result = new NewAnimator(mContext, new NavigationViewFactory());
				result.init("search_movie");
				return result;
			}else if(tag.equals(TAB_2)){
				NewAnimator result = new NewAnimator(mContext, new NavigationViewFactory());
				result.init("search_show");
				return result;
			}else if(tag.equals(TAB_3)){
				NewAnimator result = new NewAnimator(mContext, new NavigationViewFactory());
				result.init("search_anime");
				return result;
			}
			return null;
		}};

		public void onAttach (Activity activity){
			super.onAttach(activity);
			mContext = activity;
		}
		
	public void goSearch(String str){
		search_str = str;
		((PropotionerView)((NewAnimator)host.getCurrentView()).getCurrentView()).goSearch(str);
	}
		
	public boolean dispatchKeyEvent (KeyEvent event){
		return host.getCurrentView().dispatchKeyEvent(event);
	}
	
	public void home(){
		search_str = "";
		((NewAnimator)host.getCurrentView()).home();
	}
	
	public void refresh(){
		((NewAnimator)host.getCurrentView()).refresh();
	}

	@Override
	public void onTabChanged(String tabId) {
		//Log.i("Debug", "Открыта вкладка " + host.getCurrentTab());
		goSearch(search_str);
	}
}
