package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.krasview.kvlib.adapter.LoadDataToGUITask;
import ru.krasview.secret.ApiConst;
import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.indep.Parser;

import android.content.Context;

public class TVFavoriteList extends TVList 
{
	public TVFavoriteList(Context context){
		super(context);
	}

	protected int getAuthRequest(){
		return AuthRequestConst.AUTH_TV;
	}
	
	@Override
	protected String getApiAddress() {
		return ApiConst.TV;
	}
	
	@Override
	public void setConstData(){
		Map<String, Object> m;
	    m = new HashMap<String, Object>();
		m.put("type", "record_favorite");
	    m.put("name", "Записи");
	    data.add(m);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void parseData(String doc, LoadDataToGUITask task) 
	{
		Document mDocument;
		mDocument = Parser.XMLfromString(doc);
		if(mDocument == null){
			return;
		}
		mDocument.normalizeDocument();
		NodeList nListChannel = mDocument.getElementsByTagName("channel");
		int numOfChannel = nListChannel.getLength();
		Map<String, Object> m;
		if(numOfChannel==0){
			m = new HashMap<String, Object>();
			m.put("name", "<пусто>");
			m.put("type", null);
			task.onStep(m);
			return;
		}
		int num=0;
		for (int nodeIndex = 0; nodeIndex < numOfChannel; nodeIndex++){
			Node locNode = nListChannel.item(nodeIndex);
			if(Parser.getValue("star", locNode).equals("1")){
				m = new HashMap<String, Object>();
				m.put("id", Parser.getValue("id", locNode));
				m.put("name", Parser.getValue("name", locNode) );
				m.put("uri", Parser.getValue("uri", locNode));
				m.put("img_uri", Parser.getValue("image", locNode));
				//m.put("image", Parser.getImage(Parser.getValue("image", locNode)));
				m.put("state", Parser.getValue("state", locNode));
				m.put("star",Parser.getValue("star", locNode));
				m.put("type", "channel" );
				if(task.isCancelled()){
					return;
				}
				num++;
				task.onStep(m);
			}
		}
		
		if(num == 0){
			m = new HashMap<String, Object>();
			m.put("name", "нет записей");
			m.put("type", null);
			task.onStep(m);
			return;
		}
	}
}
