package ru.krasview.kvlib.adapter;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.indep.HTTPClient;
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.secret.ApiConst;

import android.os.AsyncTask;
import android.util.Log;

class LoadCurrentProgram extends AsyncTask<String, Void, Map<String, Object>> {
	Map<String, Object> mMap;
	CombineSimpleAdapter mAdapter;
	LoadCurrentProgram(CombineSimpleAdapter adapter, Map<String, Object> hm) {
		super();
		mMap = hm;
		mAdapter = adapter;
	}

	@Override
	protected Map<String, Object> doInBackground(String... arg0) {
		String str = HTTPClient.getXML(ApiConst.RECORD, "id="+mMap.get("id"), AuthRequestConst.AUTH_NONE);
		Map<String, Object> m;
		if(str.equals("<results status=\"error\"><msg>Can't connect to server</msg></results>")) {
			m = new HashMap<String, Object>();
			m.put("name", "<невозможно подключиться к серверу>");
			m.put("progress", Integer.parseInt("0"));
			m.put("time", "");
			return m;
		}
		if(str.equals("")){
			m = new HashMap<String, Object>();
			m.put("name", "<пусто>");
			m.put("progress", Integer.parseInt("0"));
			m.put("time", "");
			return m;
		}
		Document mDocument = Parser.XMLfromString(str);
		if(mDocument == null){
			m = new HashMap<String, Object>();
			m.put("name", "");
			m.put("time", "");
			m.put("progress", 0);
			return m;
		}
		mDocument.normalizeDocument();
		Node mainNode = mDocument.getFirstChild();
		m = new HashMap<String, Object>();
		m.put("name", Parser.getValue("name", mainNode));
		m.put("time", Parser.getValue("time", mainNode));
		try{
		m.put("progress", Integer.parseInt(Parser.getValue("percent", mainNode)));
		}catch(Exception e){
			Log.i("Debug", "неверный persent " + " id= "+mMap.get("id") + "name= " +Parser.getValue("name", mainNode));
			m.put("progress", 0);
		}
		return m;
	}

	@Override
	protected void onPostExecute(Map<String, Object> result) {
		if(result == null) {
			return;
		}
		mMap.put("current_program_name", (CharSequence) result.get("name"));
		mMap.put("current_program_progress", (Integer) result.get("progress"));
		mMap.put("current_program_time", (CharSequence)result.get("time"));
		mAdapter.notifyDataSetChanged();
	}
}
