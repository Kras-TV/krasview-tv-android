package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.kvlib.R;

import ru.krasview.kvlib.adapter.CombineSimpleAdapter;
import ru.krasview.kvlib.adapter.LoadDataToGUITask;
import ru.krasview.secret.ApiConst;
import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.indep.ListAccount;
import ru.krasview.kvlib.indep.Parser;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewSeriesList extends AllSeriesList {

	public NewSeriesList(Context context, Map<String, Object> map) {
		super(context, map);
	}
	
	@Override
	protected String getApiAddress(){
		return ApiConst.USER +"?series=" + getMap().get("id");
	}
	
	protected CombineSimpleAdapter createAdapter(){
		return new CombineSimpleAdapter(this, data, getApiAddress(), AuthRequestConst.AUTH_KRASVIEW){

			class LightViewHolder{
				TextView name;
				TextView comment;
				View background;
			}
			
			@SuppressWarnings("unchecked")
			@Override
		    public View getView(int position, View convertView, ViewGroup parent)
			{
				Map<String,Object> map = (Map<String, Object>)getItem(position);
				String type = (String) map.get("type");
				LightViewHolder holder;
				
				 if(convertView == null){	
					 holder = new LightViewHolder();
						LayoutInflater inflater = (LayoutInflater) parent.getContext()
					                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView = inflater.inflate(R.layout.kv_video_item, parent, false);
						holder.comment = (TextView) convertView.findViewById(R.id.comment);
						holder.background = (View)convertView.findViewById(R.id.top);
						holder.name = (TextView) convertView.findViewById(R.id.txt);
						if(ListAccount.fromLauncher){		   
							convertView.setBackgroundResource(R.drawable.selector);
							holder.name.setTextColor(colors);
						}
						convertView.setTag(holder);
				 }else{
					 holder = (LightViewHolder) convertView.getTag();
				 }
				 if(type != null && type.equals("billing")){
				    	holder.name.setVisibility(View.GONE);
				    	holder.background.setVisibility(View.VISIBLE);
				    }else{
				    	holder.name.setVisibility(View.VISIBLE);
				    	if(map.get("state")!=null && map.get("state").equals("0")){
					    	holder.background.setBackgroundColor(Color.argb(100, 100, 100, 100));
					    }else{
					    	holder.background.setBackgroundColor(Color.argb(0, 0, 0, 0));
					    }
				    	holder.background.setVisibility(View.GONE);
				    }
				 holder.name.setText( (CharSequence) map.get("name"));
				 if(type !=null&&!type.equals("video")){
					 return convertView;
				 }
				 if(type != null&&(Boolean)map.get("first")){
					 holder.comment.setText("Последняя просмотренная"); 
					 holder.comment.setVisibility(View.VISIBLE);
				 }else{
					 holder.comment.setVisibility(View.GONE);
				 }
				 return convertView;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			protected boolean emptyList(LoadDataToGUITask task){
				Map<String, Object> m;
				m = new HashMap<String, Object>();
				m.put("type", "all_series");
			    m.put("name", "Все серии");
			    m.put("id", getMap().get("id"));
				task.onStep(m);
				return true;
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void parseData(String doc, LoadDataToGUITask task){
		Document mDocument;
		mDocument = Parser.XMLfromString(doc);
		if(mDocument == null){
			return;
		}
		mDocument.normalizeDocument();
		Map<String, Object> m;
		NodeList nListChannel = mDocument.getElementsByTagName("unit");
		int numOfChannel = nListChannel.getLength();
		Node locNode0 = nListChannel.item(0);
		m = new HashMap<String, Object>();
		m.put("id", Parser.getValue("id", locNode0));
		m.put("name",  Html.fromHtml(Parser.getValue("title", locNode0)));
		m.put("uri", Parser.getValue("file", locNode0));
		m.put("first", true);
		m.put("type", "video" );
		m.put("request_time", true);
		if(task.isCancelled()){
			return;
		}
		task.onStep(m);
		for (int nodeIndex = 1; nodeIndex < numOfChannel; nodeIndex++){
			Node locNode = nListChannel.item(nodeIndex);
			m = new HashMap<String, Object>();
			m.put("id", Parser.getValue("id", locNode));
			m.put("name",  Html.fromHtml(Parser.getValue("title", locNode)));
			m.put("uri", Parser.getValue("file", locNode));
			m.put("first", false);
			m.put("type", "video" );
			m.put("request_time", true);
			if(task.isCancelled()){
				return;
			}
			task.onStep(m);
		}
	}
}
