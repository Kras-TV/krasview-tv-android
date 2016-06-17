package ru.krasview.kvlib.widget.lists;

import java.util.Map;

import ru.krasview.secret.ApiConst;

import android.content.Context;

public class OneSeasonSeriesList extends AllSeriesList {

	public OneSeasonSeriesList(Context context, Map<String, Object> map) {
		super(context, map);
	}
	
	@Override
	protected String getApiAddress(){
		return ApiConst.SEASON +"?id=" + getMap().get("id");
	}
	
}
