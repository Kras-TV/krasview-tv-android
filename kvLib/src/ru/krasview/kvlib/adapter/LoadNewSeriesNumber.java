package ru.krasview.kvlib.adapter;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.secret.ApiConst;

import android.os.AsyncTask;

class LoadNewSeriesNumber extends AsyncTask<Void, Void, Integer> 
{
	private CombineSimpleAdapter mAdapter;
	Map<String, Object> mMap;
	LoadNewSeriesNumber(CombineSimpleAdapter adapter, Map<String, Object> hm)
	{
		super();
		mAdapter = adapter;
		mMap = hm;
	}

	@Override
	protected Integer doInBackground(Void... arg0) 
	{
		String address = ApiConst.USER+"?series=" + mMap.get("id");
		String result = Parser.getXML( address, "", AuthRequestConst.AUTH_KRASVIEW);
		if(result.equals("")){
			return 0;
		}else{
			Document mDocument;
			mDocument = Parser.XMLfromString(result);
			if(mDocument == null){
				return 0;
			}
			mDocument.normalizeDocument();
			NodeList nListChannel = mDocument.getElementsByTagName("unit");
			return nListChannel.getLength()-1;
		}
	}
	
	@Override 
	protected void onPostExecute(Integer result)
	{
		mMap.put("new_series", result);
		((CombineSimpleAdapter) mAdapter).notifyDataSetChanged();
	} 
	
}
