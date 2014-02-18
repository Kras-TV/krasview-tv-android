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
import ru.krasview.kvlib.widget.List;

import android.content.Context;
import android.text.Html;

public class UserShowList extends List 
{	
	String mSection;

	public UserShowList(Context context) 
	{
		super(context, null);
	}
	
	protected UserShowList(Context context, Map<String, Object> map) 
	{
		super(context, map);
	}
	
	public UserShowList(Context context, String section) 
	{
		super(context, null);
		mSection = section;
	}
	
	@Override
	protected int getAuthRequest(){
		return AuthRequestConst.AUTH_KRASVIEW;
	}
	
	@Override
	protected String getApiAddress() {
		return ApiConst.USER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parseData(String doc, LoadDataToGUITask task) {
		Map<String, Object> m;
		boolean empty = true;
		if(doc.equals("")){
			m = new HashMap<String, Object>();
			m.put("name", "<пусто>");
			m.put("type", null );
			task.onStep(m);
			empty = false;
			return;
		}
		if(doc.equals("error")){
			m = new HashMap<String, Object>();
			m.put("name", "<ошибка авторизации>");
			m.put("type", null );
			task.onStep(m);
			empty = false;
			return;
		}
		Document mDocument;
		mDocument = Parser.XMLfromString(doc);
		if(mDocument == null){
			return;
		}
		mDocument.normalizeDocument();
		NodeList nListChannel = mDocument.getElementsByTagName("unit");
		int numOfChannel = nListChannel.getLength();
		
		if(numOfChannel == 0){
			m = new HashMap<String, Object>();
			m.put("name", "<нет подписок>");
			m.put("type", null );
			task.onStep(m);
			empty = false;
			return;
		}
		for (int nodeIndex = 0; nodeIndex < numOfChannel; nodeIndex++){
			Node locNode = nListChannel.item(nodeIndex);
			m = new HashMap<String, Object>();
			String s = Parser.getValue("section", locNode);
			if(mSection == null||s.equals(mSection)){
				m.put("id", Parser.getValue("id", locNode));
				m.put("name", Html.fromHtml(Parser.getValue("title", locNode)));
				m.put("img_uri", Parser.getValue("thumb", locNode));
				m.put("description", Parser.getValue("description", locNode));
				m.put("type", "series" );
				if(task.isCancelled()){
					return;
				}
				task.onStep(m);
				empty = false;
			}					
		}
		if(empty == true){
			m = new HashMap<String, Object>();
			m.put("name", "<нет подписок>");
			m.put("type", null );
			task.onStep(m);
		}
		
	}
}
