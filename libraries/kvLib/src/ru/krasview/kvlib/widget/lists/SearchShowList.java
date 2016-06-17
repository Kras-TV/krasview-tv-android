package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.text.Html;

import ru.krasview.kvlib.adapter.LoadDataToGUITask;
import ru.krasview.kvlib.interfaces.SearchInterface;
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.secret.ApiConst;
import ru.krasview.kvlib.widget.List;

public class SearchShowList extends List implements SearchInterface{

	public SearchShowList(Context context) {
		super(context, null);
	}

	@Override
	public void goSearch(String str) {
		getAdapter().setAddress(getApiAddress() + "?search=" + str);
		this.refresh();
	}
	
	@Override
	protected String getApiAddress() {
		return ApiConst.SHOW;	
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
			m.put("name", Html.fromHtml(Parser.getValue("title", locNode)));
			m.put("img_uri", Parser.getValue("thumb", locNode));
			m.put("description", Parser.getValue("description", locNode));
			m.put("type", "series" );
			if(task.isCancelled()){
				return;
			}
			task.onStep(m);	
		}
		
	}

}
