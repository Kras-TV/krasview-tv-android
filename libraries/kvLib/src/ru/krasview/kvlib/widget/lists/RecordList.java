package ru.krasview.kvlib.widget.lists;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.krasview.kvlib.adapter.LoadDataToGUITask;
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.secret.ApiConst;
import ru.krasview.kvlib.widget.List;

import android.content.Context;
import android.text.Html;
import android.util.Log;

public class RecordList extends List {
	public RecordList(Context context, Map<String, Object> map) {
		super(context, map);
	}

	@Override
	protected String getApiAddress() {
		return ApiConst.RECORD +"?id=" + getMap().get("channel_id") 
				+"&date=" + getMap().get("id");
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
		NodeList nListChannel = mDocument.getElementsByTagName("item");
		int numOfChannel = nListChannel.getLength();
		Map<String, Object> m;
		for (int nodeIndex = 0; nodeIndex < numOfChannel; nodeIndex++){
			Node locNode = nListChannel.item(nodeIndex);
			if(Parser.getValue("record", locNode) != null){
				Log.i("Debug", "запись передачи " + Parser.getValue("record", locNode));
			}
			m = new HashMap<String, Object>();
			m.put("id", Parser.getValue("id", locNode));
			java.util.Date date = (new java.util.Date(1000*Long.parseLong(Parser.getValue("start", locNode))));
			DateFormat df = new SimpleDateFormat("HH:mm");
			String reportDate = df.format(date);
			m.put("name", reportDate + " "+ Html.fromHtml(Parser.getValue("name", locNode)));
			m.put("uri", Parser.getValue("record", locNode));
			if(task.isCancelled()){
				return;
			}
			if(Parser.getValue("record", locNode) == null){
				m.put("state", "0");
			}else{
				m.put("type", "tv_record");
				task.onStep(m);
			}
		}
		if(data.size() == 0){
			m = new HashMap<String, Object>();
			m.put("name", "<нет записей>");
			m.put("type", null);
			task.onStep(m);
			return;
		}
		
	}
}
