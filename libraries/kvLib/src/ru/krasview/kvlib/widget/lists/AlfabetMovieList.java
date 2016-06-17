package ru.krasview.kvlib.widget.lists;

import java.util.Map;

import android.content.Context;

public class AlfabetMovieList extends AlfabetShowList {

	public AlfabetMovieList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void setType(Map<String, Object> map){
		map.put("type", "letter_movie");
	}

}
