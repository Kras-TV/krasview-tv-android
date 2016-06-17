package ru.krasview.kvlib.adapter;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

public class LoadDataToGUITask extends AsyncTask<String, Map<String, Object>, Void> 
{	
	private CombineSimpleAdapter mAdapter;
	
	public LoadDataToGUITask(CombineSimpleAdapter adapter){
		super();
		mAdapter = adapter;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Void doInBackground(String... params) 
	{
		String str = params[0];	
		if(str.equals("<results status=\"error\"><msg>Can't connect to server</msg></results>")){
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("name", "<невозможно подключиться к серверу>");
			m.put("type", null);
			onStep(m);
			return null;
		}	
		if(str.equals("")){
			if(mAdapter.emptyList(this)){
				return null;
			}
			Map<String, Object> m = new HashMap<String, Object>();
			m = new HashMap<String, Object>();
			m.put("name", "<пусто>");
			m.put("type", null);
			onStep(m);
			return null;
		}
		if(str.equals("auth failed")){
			Map<String, Object> m = new HashMap<String, Object>();
			m = new HashMap<String, Object>();
			m.put("name", "<неверный логин или пароль>");
			m.put("type", null);
			onStep(m);
			return null;
		}
		mAdapter.parseData(str, this);
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Map<String, Object>... progress) 
	{			
		if(progress.length == 1){
			mAdapter.mData.add(progress[0]);		
		}
		if(progress.length == 2){
			mAdapter.mData.add(0,progress[0]);		
		}
		mAdapter.notifyDataSetChanged();
     }

	@Override
	protected void onPostExecute(Void result)
	{
		if(isCancelled()){
			return;
		}
		mAdapter.postExecute();	
	}
	
	public void onStep(Map<String, Object>... m) 
	{
		publishProgress(m);
	}
	
}