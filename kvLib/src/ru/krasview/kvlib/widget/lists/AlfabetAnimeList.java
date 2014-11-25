package ru.krasview.kvlib.widget.lists;

import java.util.Map;

import android.content.Context;

public class AlfabetAnimeList extends AlfabetShowList {
	public AlfabetAnimeList(Context context) {
		super(context);
	}

	@Override
	protected void setType(Map<String, Object> map){
		map.put("type", "letter_anime");
	}
}
