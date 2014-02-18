package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.krasview.kvlib.adapter.LoadDataToGUITask;
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.kvlib.widget.List;
import ru.krasview.secret.ApiConst;

import android.content.Context;
import android.text.Html;

public class DateList extends List{
	
	public DateList(Context context, Map<String, Object> map){
		super(context, map);
	}

	@Override
	protected String getApiAddress() {
		return ApiConst.DAYS +"?"+"id=" + getMap().get("id");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parseData(String doc, LoadDataToGUITask task) {
		int num = 0;
		Document mDocument;
		mDocument = Parser.XMLfromString(doc);
		if(mDocument == null){
			return;
		}
		mDocument.normalizeDocument();
		NodeList nListChannel = mDocument.getElementsByTagName("item");
		int numOfChannel = nListChannel.getLength();
		Map<String, Object> m;
		for (int nodeIndex = 0; nodeIndex < numOfChannel; nodeIndex++){
			Node locNode = nListChannel.item(nodeIndex);
			m = new HashMap<String, Object>();
			m.put("id", Parser.getValue("id", locNode));
			m.put("channel_id", getMap().get("id"));
			m.put("name",  Html.fromHtml(Parser.getValue("date", locNode)));
			m.put("type",  "record_list");
			if(task.isCancelled()){
				return;
			}
			task.onStep(m, null);
			num++;;
		}
		if(num==0){
			m = new HashMap<String, Object>();
			m.put("name", "нет записей");
			m.put("type", null);
			task.onStep(m);
			return;
		}
		
	}

}
