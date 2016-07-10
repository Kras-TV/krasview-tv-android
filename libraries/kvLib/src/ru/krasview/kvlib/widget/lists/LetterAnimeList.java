package ru.krasview.kvlib.widget.lists;

import java.util.Map;

import ru.krasview.secret.ApiConst;

import android.content.Context;

public class LetterAnimeList extends LetterShowList {
	public LetterAnimeList(Context context, Map<String, Object> m) {
		super(context, m);
	}

	@Override
	protected String getApiAddress(){
		return ApiConst.LETTER_ANIME + getMap().get("name");
	}
}
