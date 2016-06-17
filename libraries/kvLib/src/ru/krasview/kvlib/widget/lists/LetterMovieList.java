package ru.krasview.kvlib.widget.lists;

import java.util.Map;

import ru.krasview.secret.ApiConst;

import android.content.Context;

public class LetterMovieList extends LetterShowList {

	public LetterMovieList(Context context, Map<String, Object> map) {
		super(context, map);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected String getApiAddress(){
		return "http://krasview.ru/public/movie/letter/" + getMap().get("name");
	}

}
