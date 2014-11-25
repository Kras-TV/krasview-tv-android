package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.krasview.kvlib.adapter.LoadDataToGUITask;
import ru.krasview.secret.ApiConst;
import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.kvlib.widget.List;

import android.app.Activity;
import android.content.Context;

public class TVList extends List{
	protected boolean firstRefresh;

	public TVList(Context context) {
		super(context, null);
		firstRefresh = true;
	}

	@Override
	protected String getApiAddress() {
		return ApiConst.TV;
	}
	
	protected int getAuthRequest(){
		return AuthRequestConst.AUTH_TV;
	}
	
	Timer timer;
	
	private void deleteTimer()
	{
		if(timer!=null){
			timer.cancel();
			timer.purge();
			timer=null;
		}
	}
	
	private void restartTimer()
	{
		deleteTimer();
		timer = new Timer();
		timer.schedule( new TimerTask() {
			@Override
			public void run() {
				if(getAdapter()!=null&&!firstRefresh) {
					refreshCurrentProgram();
				}
				firstRefresh = false;
			}
		}, 0, 1000*60);
	}

	@Override
	public void enter(){
		super.enter();
		restartTimer();
	}

	@Override
	public void exit(){
		super.exit();
		deleteTimer();
	}

	private void refreshCurrentProgram() {
		java.util.List<Map<String, Object>> local_data = getAdapter().getData();
		for(int i=0; i<local_data.size();i++){
			Map<String, Object> item = local_data.get(i);
			item.put("current_program_name_old", item.get("current_program_name"));
			item.remove("current_program_name");
			((Activity)getContext()).runOnUiThread(new Runnable(){

				@Override
				public void run(){
					getAdapter().notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void setConstData(){
		Map<String, Object> m;
		m = new HashMap<String, Object>();
		m.put("type", "favorite_tv");
		m.put("name", "Избранные телеканалы");
		data.add(m);

		m = new HashMap<String, Object>();
		m.put("type", "record");
		m.put("name", "Записи");
		data.add(m);
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
		NodeList nListChannel = mDocument.getElementsByTagName("channel");
		int numOfChannel = nListChannel.getLength();
		Map<String, Object> m;
		if(numOfChannel == 0) {
			m = new HashMap<String, Object>();
			m.put("name", "<пусто>");
			m.put("type", null);
			task.onStep(m);
			return;
		}
		for (int nodeIndex = 0; nodeIndex < numOfChannel; nodeIndex++) {
			Node locNode = nListChannel.item(nodeIndex);
			m = new HashMap<String, Object>();
			m.put("id", Parser.getValue("id", locNode));
			m.put("name", Parser.getValue("name", locNode));
			m.put("uri", Parser.getValue("uri", locNode));
			m.put("img_uri", Parser.getValue("image", locNode));
			m.put("state",Parser.getValue("state", locNode));
			m.put("star",Parser.getValue("star", locNode));
			m.put("type", "channel" );
			if(task.isCancelled()) {
				return;
			}
			task.onStep(m);
		}
		
	}
}
