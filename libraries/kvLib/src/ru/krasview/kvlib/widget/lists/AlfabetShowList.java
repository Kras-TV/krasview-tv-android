package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.Map;

import ru.krasview.kvlib.widget.List;

import android.content.Context;

public class AlfabetShowList extends List{
	private String abc = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" 
			+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public AlfabetShowList(Context context){
		super(context, null);
	}

	protected void setType(Map<String, Object> map)
	{
		map.put("type", "letter_series");
	}

	@Override
	public void setConstData() 
	{
		int size = abc.length();
		
		Map<String, Object> m;
		for(int i = 0; i < size; i++ ){
			m = new HashMap<String, Object>();
			setType(m);
		    m.put("name", "" + abc.charAt(i));
		    data.add(m);
		}
	}
}
