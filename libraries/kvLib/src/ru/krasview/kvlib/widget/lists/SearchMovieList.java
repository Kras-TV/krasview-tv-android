package ru.krasview.kvlib.widget.lists;

import ru.krasview.secret.ApiConst;
import android.content.Context;

public class SearchMovieList extends SearchShowList {

	public SearchMovieList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected String getApiAddress() {
		return "http://krasview.ru/public/movie/";
	}

}
