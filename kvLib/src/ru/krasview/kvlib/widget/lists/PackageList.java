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
import ru.krasview.kvlib.indep.Parser;
import ru.krasview.kvlib.interfaces.OnLoadCompleteListener;
import ru.krasview.kvlib.widget.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PackageList extends List{
	
	OnLoadCompleteListener mOnLoadCompleteListener;

	public PackageList(Context context, Map<String, Object> map){
		super(context, map);
	}
	
	@Override
	protected boolean showBilling(){
		return false;
	}
	
	@Override
	protected int getAuthRequest(){
		return AuthRequestConst.AUTH_TV;
	}
	
	@Override
	protected String getApiAddress() {
		return ApiConst.PACKET;
	}
	
	@Override
	public void setConstData(){
		Map<String, Object> m;
		m = new HashMap<String, Object>();
		m.put("type", "favorite_tv");
	    m.put("name", "Избранные телеканалы");
	    m.put("name", "пакет \"Полный\"");
		m.put("id", "1" );
		m.put("product", "portion" );
		String sku = "month.1";
		m.put("sku", sku);
	    data.add(m);
	}
	
	@Override
	protected CombineSimpleAdapter createAdapter(){
		return new CombineSimpleAdapter(this, data, getApiAddress(), getAuthRequest()){
			
			class PackageViewHolder 
			{
		        TextView name;
		        TextView price;
		        TextView productType;
		    }
			
			@SuppressWarnings("unchecked")
			@Override
		    public View getView(int position, View convertView, ViewGroup parent){
				Map<String,Object> map = (Map<String, Object>)getItem(position);
				PackageViewHolder holder;
				if(convertView == null){	
					LayoutInflater inflater = (LayoutInflater) parent.getContext()
				                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.kv_multi_item_billing, parent, false);
					holder = new PackageViewHolder();
					holder.name = (TextView) convertView.findViewById(R.id.txt);
					holder.price = (TextView) convertView.findViewById(R.id.price);
					holder.productType = (TextView) convertView.findViewById(R.id.productType);
					convertView.setTag(holder);
				 }else{
					 holder = (PackageViewHolder) convertView.getTag();
				 }
				 holder.name.setText( (CharSequence) map.get("name"));
				 holder.price.setText( (CharSequence) map.get("price"));
				 if(((CharSequence) map.get("productType")) == null 
						 || ((CharSequence) map.get("productType")).equals("")){
					 holder.productType.setVisibility(GONE);
				 }else{
					 holder.productType.setVisibility(VISIBLE);
				 }
				 holder.productType.setText( (CharSequence) map.get("productType"));
				 return convertView;
			}

			@Override
			protected void postExecute(){
				complite("");
			}	
		};	
	}
	
	
	public OnLoadCompleteListener getOnLoadCompleteListener(){
		return mOnLoadCompleteListener;
	}
	
	public void setOnLoadCompleteListener(OnLoadCompleteListener listener){
		mOnLoadCompleteListener = listener;
	}
	
	private void complite(String result)
	{
		if(mOnLoadCompleteListener != null){
			mOnLoadCompleteListener.loadComplete(result);
		}
	}

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
		for (int nodeIndex = 0; nodeIndex < numOfChannel; nodeIndex++){
			Node locNode = nListChannel.item(nodeIndex);
			Map<String, Object> m = new HashMap<String, Object>();		
			m.put("name", Parser.getValue("name", locNode) );
			m.put("id", Parser.getValue("id", locNode) );
			m.put("product", "subscription" );
			String sku = "test" + "." + (String) m.get("product") + "." + (String) m.get("id");
			m.put("sku", sku);
			if(task.isCancelled()){
				return;
			}
			m = new HashMap<String, Object>();		
			m.put("name", Parser.getValue("name", locNode));
			m.put("id", Parser.getValue("id", locNode) );
			m.put("product", "portion" );
			sku = "test" + "." + (String) m.get("product") + "." + (String) m.get("id");
			m.put("sku", sku);
			if(task.isCancelled()){
				return;
			}
		}
		
	}
}
