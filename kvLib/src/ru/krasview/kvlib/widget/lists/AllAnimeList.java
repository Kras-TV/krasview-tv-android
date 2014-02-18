package ru.krasview.kvlib.widget.lists;

import java.util.HashMap;
import java.util.Map;

import ru.krasview.kvlib.indep.AuthAccount;
import ru.krasview.kvlib.indep.ListAccount;
import ru.krasview.kvlib.indep.consts.AuthEnterConsts;
import ru.krasview.secret.ApiConst;

import android.content.Context;

public class AllAnimeList extends AllShowList{

	public AllAnimeList(Context context){
		super(context);
	}
	
	protected String getApiAddress(){
		return ApiConst.ANIME;
	}
	
	@Override
	public void setConstData(){
		Map<String, Object> m;
		if(AuthAccount.auth_type == AuthEnterConsts.AUTH_TYPE_KRASVIEW
				&& ListAccount.fromLauncher){
			m = new HashMap<String, Object>();
			m.put("type", "my_shows_all_a");
			m.put("name", "Я смотрю");
			data.add(m);
		}
		m = new HashMap<String, Object>();
		m.put("type", "alfabet_anime");
		m.put("name", "По алфавиту");
		data.add(m);
	}
}
