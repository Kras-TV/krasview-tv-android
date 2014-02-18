package ru.krasview.kvlib.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.indep.ListAccount;

import com.example.kvlib.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CombineSimpleAdapter extends BaseAdapter{
	
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	protected int mCount;
	protected List<Map<String, Object>> mConstData;
	protected List<Map<String, Object>> mData = new ArrayList< Map<String, Object>>() ;
	protected String mAddress;
	protected ru.krasview.kvlib.widget.List mParent;
	
	protected int withAuth = AuthRequestConst.AUTH_NONE;
	protected ColorStateList colors = null;
	
	private boolean mRecFocus = true;
	
	protected final void parseData(String doc, LoadDataToGUITask task){
		mParent.parseData(doc, task);
	}
	
	protected void postExecute(){};
	
	public CombineSimpleAdapter(ru.krasview.kvlib.widget.List parent,
			List<Map<String, Object>> constData, String address, int auth, boolean focus){
		this(parent, constData, address, auth);
		mRecFocus = focus;
	}

	public CombineSimpleAdapter(ru.krasview.kvlib.widget.List parent,
			List<Map<String, Object>> constData, String address, int auth){
		super();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.series)
		.showImageForEmptyUri(R.drawable.series)
		.showImageOnFail(null)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
		if(constData == null){
			constData = new ArrayList< Map<String, Object>>() ;
		}
		mConstData = constData;
		mAddress = address;
		withAuth = auth;
		if(withAuth > 2 || withAuth < 0){
			withAuth=0;
		}
		mParent = parent;
		
		XmlResourceParser parser = mParent.getContext().getResources().getXml(R.color.text_selector);
		    try {
				colors = ColorStateList.createFromXml(mParent.getContext().getResources(), parser);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	    
	}
	
	public List<Map<String, Object>> getData(){
		return mData;
	}

	@SuppressWarnings("unchecked")
	@Override
    public View getView(int position, View convertView, ViewGroup parent){	
		Map<String,Object> map = (Map<String, Object>)getItem(position);
		View view;
		ViewHolder holder;
	    if(convertView == null){
	    	LayoutInflater inflater = (LayoutInflater) parent.getContext()
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	view = inflater.inflate(R.layout.kv_multi_item, parent, false);
	    	holder = new ViewHolder();
	    	holder.background = view.findViewById(R.id.top);
	    	holder.name = (TextView) view.findViewById(R.id.txt);
	    	holder.image = (ImageView) view.findViewById(R.id.image);
	    	holder.progress = ((ProgressBar)view.findViewById(R.id.progress));
	    	holder.time = ((TextView)view.findViewById(R.id.time));
	    	holder.current_program = (TextView)view.findViewById(R.id.current_program);
	    	holder.currentLayout = view.findViewById(R.id.current_layout);
	    	holder.new_series = (TextView)view.findViewById(R.id.new_series);
	    	if(ListAccount.fromLauncher){		   
	    		view.setBackgroundResource(R.drawable.selector);
	    		holder.name.setTextColor(colors);
	    		holder.current_program.setTextColor(colors);
	    		Drawable  progress_tv = mParent.getContext().getResources()
	    				.getDrawable(R.drawable.progress_tv);
	    		holder.progress.setProgressDrawable(progress_tv);
	    	}
	    	view.setTag(holder);
	    }else{
	    	view = convertView;
	    }
	    return BindView(view, map);
	}
	
	private View BindView(View view, Map<String, Object> map){
		ViewHolder holder = (ViewHolder) view.getTag();
		
		//название
	    if(map.get("name") != null){
	    	holder.name.setVisibility(View.VISIBLE);
	    	 holder.name.setText((CharSequence) map.get("name"));
	    }else{
	    	holder.name.setVisibility(View.GONE);
	    }
		
		String type = (String) map.get("type");
		if(type == null){
	    	holder.background.setVisibility(View.GONE);
	    	holder.image.setVisibility(View.GONE);
	    	holder.currentLayout.setVisibility(View.GONE);
	    	holder.new_series.setVisibility(View.GONE);
	    	return view;
	    }
	
	    if(type.equals("billing")){
	    	holder.background.setVisibility(View.VISIBLE);
	    	holder.currentLayout.setVisibility(View.GONE);
	    	holder.new_series.setVisibility(View.GONE);
	    }else{
	    	holder.background.setVisibility(View.GONE);
	    }
	    //статус
	    if(map.get("state") != null){
	    	if(map.get("state").equals("0")){
	    		holder.background.setBackgroundColor(Color.argb(100, 100, 100, 100));
	    	}else{
	    		holder.background.setBackgroundColor(Color.argb(0, 0, 0, 0));
	    	}
	    } 
	    
	   
	    //картинка
	    if(map.get("img_uri") != null){
	    	holder.image.setVisibility(View.VISIBLE);
	    	//загрузить картинку
	    	ImageLoader.getInstance().displayImage((String)map.get("img_uri"), holder.image, options, animateFirstListener);
	    }else{
	    	holder.image.setVisibility(View.INVISIBLE);
	    }
	    
	    //текущая программа
	    if(type.equals("channel")){
	    	holder.currentLayout.setVisibility(View.VISIBLE);
	    	if(map.get("current_program_name") == null){
	    		if( map.get("current_program_name_old") != null){
	    			map.put("current_program_name", map.get("current_program_name_old"));
	    		}else{
	    			map.put("current_program_name", "");
			 		map.put("current_program_time", "");
			 		map.put("current_program_progress", 0);
	    		}		
		 		notifyDataSetChanged();
	    		new LoadCurrentProgram(this, map).execute();	  	
	    	}else if(map.get("current_program_name") != null){
	    		CharSequence pr = (CharSequence)map.get("current_program_name");
	    		if(pr.equals("<пусто>")){
	    			pr = "";
	    		}
	    		holder.current_program.setText(pr);
	    		holder.progress.setProgress((Integer)map.get("current_program_progress"));	
	    		holder.time.setText((CharSequence)map.get("current_program_time"));  		
	    	}
	    }else{
	    	holder.currentLayout.setVisibility(View.GONE);
	    }
	    
	    //число новых серий
	    if(type.equals("series")){
	    	if(map.get("new_series") == null){
	    		holder.new_series.setVisibility(View.GONE);
	    		new LoadNewSeriesNumber(this,map).execute();
	    		map.put("new_series", 0);
	    	}else if(map.get("new_series") != null&&(Integer)map.get("new_series") == 0){
	    		holder.new_series.setVisibility(View.GONE);
	    	}else{
	    		holder.new_series.setText("+" + map.get("new_series"));
	    		holder.new_series.setVisibility(View.VISIBLE);
	    	}
	    }
	    
	    if(type.equals("video")){
	    	holder.image.setVisibility(View.GONE);
	    } 
	    return view;
	}
	
	 class ViewHolder{
		View background;
        TextView name;
        ImageView image;
        TextView time;
        TextView current_program;
        ProgressBar progress;
        View currentLayout;
        TextView new_series;
    }
		
	@Override
	public int getCount(){
		return mConstData.size() + mData.size();
	}
	
	public int getConstDataCount(){
		return mConstData.size();
	}

	@Override
	public Object getItem(int position){
		if(position >= 0 && position < mConstData.size()){
			return mConstData.get(position);
		}else{
			Object obj;
			try{
			obj = mData.get(position - mConstData.size());
			}catch(Exception e){
				Log.e("Debug", "" + position+" "+(position - mConstData.size()) +" "+ e.toString());
				return null;
			}
			return obj;
		}
	}

	@Override
	public long getItemId(int position){
		return position;
	}
	
	public void refresh(){
		String params = "";
		if(mAddress == null){
			return;
		};
		mData.clear();
		loadDataFromAddress(mAddress, params);
	}
	
	protected  void loadDataFromAddress(String uri, String params){	
		LoadDataFromAddressTask task = new LoadDataFromAddressTask(this);
		task.execute(uri, params);
	}
	
	@Override
	public void notifyDataSetChanged (){	
		int size = mData.size();
		super.notifyDataSetChanged();
		if(size == 1 && mRecFocus){
			mParent.requestFocus();
		}
	}
	
	public void setAddress(String address){
		mAddress = address;
	}

	protected boolean emptyList(LoadDataToGUITask task){
		return false;
	}
	
	public void editConstData(){
		if(mConstData.isEmpty()){
			return;
		}
		mConstData.clear();
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener{
		static final java.util.List<String> displayedImages 
				= Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
			if (loadedImage != null){
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay){
					FadeInBitmapDisplayer.animate(imageView, 500);
					if(displayedImages.size() > 100){
						displayedImages.remove(0);
					}
					displayedImages.add(imageUri);
					Log.i("Debug", "" + displayedImages.size());
				}
			}
		}
	}
}