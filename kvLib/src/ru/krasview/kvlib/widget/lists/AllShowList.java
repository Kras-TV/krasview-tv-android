package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.krasview.kvlib.adapter.CombineSimpleAdapter;
import ru.krasview.kvlib.adapter.LoadDataToGUITask;
import ru.krasview.kvlib.indep.ListAccount;
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.indep.consts.TypeConsts;
import ru.krasview.secret.ApiConst;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.widget.AbsListView;

public class AllShowList extends UserShowList{	
	
	int page = 1;

	public AllShowList(Context context){
		super(context);
	}
	protected AllShowList(Context context, Map<String, Object> map){
		super(context, map);
	}

	@Override
	protected String getApiAddress() {
		return ApiConst.SHOW;
	}
	
	protected int getAuthRequest(){
		return AuthRequestConst.AUTH_NONE;
	}
	
	@Override
	public void setConstData(){
		Map<String, Object> m;
		if(ListAccount.fromLauncher){
			m = new HashMap<String, Object>();
	    	m.put("type", TypeConsts.MOVIE);
	    	m.put("name", "!!Фильмы!!");
	    	data.add(m);
	    }
		
		if(account.isKrasviewAccount() && ListAccount.fromLauncher){
			m = new HashMap<String, Object>();
			m.put("type", "my_shows_all_s");
			m.put("name", "Я смотрю");
			data.add(m);
		}
		m = new HashMap<String, Object>();
		m.put("type", "alfabet_series");
	    m.put("name", "По алфавиту");
	    data.add(m);
	}
	
	private void loadNext(){
		((AllShowAdapter)getAdapter()).loadNext();
	}
	
	@Override
	protected CombineSimpleAdapter createAdapter(){
		return new AllShowAdapter(data, getApiAddress(), getAuthRequest());
	}
	
	private class AllShowAdapter extends CombineSimpleAdapter{
		
		public AllShowAdapter(List<Map<String, Object>> constData,
				String address, int auth){
			super(AllShowList.this, constData, address, auth);
		}
		
		public void loadNext(){
			mData.remove(mData.size()-1);
			notifyDataSetChanged();			
			String params = "";
			page++;
			if(mAddress == null){
				return;
			};
			params = "page=" + page + "&" + params;
			loadDataFromAddress(mAddress, params);
		}
	}
	
	@Override
	public void refresh(){
		page = 1;
		super.refresh();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void parseData(String doc, LoadDataToGUITask task) {
		Document mDocument;
		mDocument = Parser.XMLfromString(doc);
		if(mDocument == null){
			return;
		}
		mDocument.normalizeDocument();
		NodeList nListChannel = mDocument.getElementsByTagName("unit");
		int numOfChannel = nListChannel.getLength();
		
		Map<String, Object> m;
		if(data.size()>0){
			m = (Map<String, Object>)data.get(data.size()-1);
			if(m.get("type").equals("next")){
				data.remove(m);
				((Activity)getContext()).runOnUiThread(new Runnable(){
					
					@Override
					public void run(){
						getAdapter().notifyDataSetChanged();	
					}
				});
			}
		}
		for (int nodeIndex = 0; nodeIndex < numOfChannel; nodeIndex++){
			Node locNode = nListChannel.item(nodeIndex);
			m = new HashMap<String, Object>();
			m.put("id", Parser.getValue("id", locNode));
			m.put("name", Html.fromHtml(Parser.getValue("title", locNode)));
			m.put("img_uri", Parser.getValue("thumb", locNode));
			m.put("description", Parser.getValue("description", locNode));
			m.put("type", "series" );
			if(task.isCancelled()){
				return;
			}
			task.onStep(m);
		}
		m = new HashMap<String, Object>();
		m.put("type", "next" );
		m.put("name", "...");
		if(task.isCancelled()){
			return;
		}
		task.onStep(m);	
	}
	
	
	
	@Override
	public void init(){
		super.init();
		this.setOnScrollListener(new OnScrollListener(){
			
			int past = 0;
			
			@SuppressWarnings("unchecked")
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int i = (int) (firstVisibleItem + visibleItemCount)-1;
				if(i == getAdapter().getCount()-1){
					Map<String, Object> map = (Map<String,Object>)getAdapter().getItem(i);
					if(map == null||map.get("name") == null){
						return;
					}
					if(map.get("name").toString().equals("...") && i != past){
						loadNext();
						past = i;
					}
					
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}});
	}
	
	
}
