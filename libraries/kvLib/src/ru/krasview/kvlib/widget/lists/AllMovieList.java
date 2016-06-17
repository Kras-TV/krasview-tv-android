package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.Map;

import ru.krasview.kvlib.indep.ListAccount;
import ru.krasview.secret.ApiConst;

import android.content.Context;

public class AllMovieList extends AllShowList {

	public  AllMovieList(Context context, Map<String, Object> map) {
		super(context, map);
	}
	
	protected String getApiAddress(){
		return "http://krasview.ru/public/movie";
	}
	
	@Override
	public void setConstData(){
		Map<String, Object> m;
		m = new HashMap<String, Object>();
		m.put("type", "alfabet_movie");
		m.put("name", "По алфавиту");
		data.add(m);
	}

}
