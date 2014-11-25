package ru.krasview.kvlib.widget.lists;

import ru.krasview.secret.ApiConst;
import android.content.Context;

public class SearchAnimeList extends SearchShowList {
	public SearchAnimeList(Context context) {
		super(context);
	}

	@Override
	protected String getApiAddress() {
		return ApiConst.ANIME;
	}
}
