package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.krasview.kvlib.adapter.LoadDataToGUITask;
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.kvlib.indep.AuthAccount;import ru.krasview.secret.ApiConst;
import ru.krasview.kvlib.indep.consts.AuthEnterConsts;
import ru.krasview.kvlib.widget.List;

import android.content.Context;

public class SeasonList extends List{
	
	public SeasonList(Context context, Map<String, Object> map){
		super(context, map);
	}
	
	@Override
	protected String getApiAddress() {
		return ApiConst.ALL_SEASONS +"?id=" + getMap().get("id");	
	}

	@Override
	public void setConstData(){
		Map<String, Object> m;
		if(AuthAccount.auth_type == AuthEnterConsts.AUTH_TYPE_KRASVIEW
				||AuthAccount.auth_type == AuthEnterConsts.AUTH_TYPE_KRASVIEW_SOCIAL){
			m = new HashMap<String, Object>();
			m.put("type", "new_series");
		    m.put("name", "Новые серии");
		    m.put("id", getMap().get("id"));
		    data.add(m);
			}
			m = new HashMap<String, Object>();
			m.put("type", "series_light");
		    m.put("name", "Описание");
		    m.put("id", getMap().get("id"));
		    m.put("img_uri", (String)getMap().get("img_uri"));
		    m.put("description", (String)getMap().get("description"));
		    m.put("show_name", getMap().get("name"));
		    data.add(m);
			m = new HashMap<String, Object>();
			m.put("type", "all_series");
		    m.put("name", "Все серии");
		    m.put("id", getMap().get("id"));
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
		NodeList nListChannel = mDocument.getElementsByTagName("unit");
		int numOfChannel = nListChannel.getLength();
		Map<String, Object> m;
		for (int nodeIndex = 0; nodeIndex < numOfChannel; nodeIndex++){
			Node locNode = nListChannel.item(nodeIndex);
			m = new HashMap<String, Object>();
			m.put("id", Parser.getValue("id", locNode));
			m.put("name", Parser.getValue("title", locNode));
			m.put("type", "season_series" );
			if(task.isCancelled()){
				return;
			}
			task.onStep(m);
		}
		
	}
}
