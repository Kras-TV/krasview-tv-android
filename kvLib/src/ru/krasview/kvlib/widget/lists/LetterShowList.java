package ru.krasview.kvlib.widget.lists;

import java.util.Map;

import ru.krasview.secret.ApiConst;

import android.content.Context;

public class LetterShowList extends AllShowList{
	
	//не убирать
	@Override
	public void setConstData(){
		
	}

	public LetterShowList(Context context, Map<String, Object> map) {
		super(context, map);
	}
	
	@Override
	protected String getApiAddress() {
		return ApiConst.LETTER_SHOW + getMap().get("name");
	}

}
