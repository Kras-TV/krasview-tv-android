package ru.krasview.kvlib.widget;

import java.util.Map;

import ru.krasview.kvlib.indep.consts.TagConsts;
import ru.krasview.kvlib.indep.consts.TypeConsts;
import ru.krasview.kvlib.interfaces.Factory;
import ru.krasview.kvlib.widget.lists.*;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class NavigationViewFactory implements Factory {
	
	private Context c;
	
	public NavigationViewFactory(){
	}

	@Override
	public View getView(Map<String, Object> map, Context context){
		c = context;
		View view = null;
		String type = (String)map.get(TagConsts.TYPE);
		if(type == null)						        {view = get_null();	
		}else if(type.equals(TypeConsts.MAIN))	        {view = new MainList(c);
		}else if(type.equals(TypeConsts.TV))		    {view = new TVList(c);
		}else if(type.equals(TypeConsts.FAVORITE_TV))	{view = new TVFavoriteList(c);		
		}else if(type.equals(TypeConsts.MY_ALL))		{view = new UserShowList(c);
		}else if(type.equals(TypeConsts.MY_ANIME))		{view = new UserShowList(c,"anime");
		}else if(type.equals(TypeConsts.MY_SHOWS))		{view = new UserShowList(c,"series");
		}else if(type.equals(TypeConsts.DESCRIPTION))	{view = new Serial(c, map);
		}else if(type.equals("series"))					{view = new SeasonList(c, map);
		}else if(type.equals("season_list"))			{view = new SeasonList(c, map);
		}else if(type.equals(TypeConsts.ALL_SERIES))  	{view = new AllSeriesList(c, map);
		}else if(type.equals(TypeConsts.SEASON))		{view = new OneSeasonSeriesList(c, map);
		}else if(type.equals(TypeConsts.ALL_SHOW))		{view = new AllShowList(c);
		}else if(type.equals(TypeConsts.ALL_ANIME))		{view = new AllAnimeList(c);
		}else if(type.equals(TypeConsts.ALPHABET_SHOW))	{view = new AlfabetShowList(c);
		}else if(type.equals(TypeConsts.ALPHABET_ANIME)){view = new AlfabetAnimeList(c);
		}else if(type.equals(TypeConsts.LETTER_SHOW))	{view = new LetterShowList(c, map);
		}else if(type.equals(TypeConsts.LETTER_ANIME))	{view = new LetterAnimeList(c, map);
		}else if(type.equals(TypeConsts.SEARCH_SHOW))	{view = new SearchShowList(c);
		}else if(type.equals(TypeConsts.SEARCH_ANIME))	{view = new SearchAnimeList(c);
		}else if(type.equals(TypeConsts.NEW_SERIES))	{view = new NewSeriesList(c, map);
		}else if(type.equals(TypeConsts.TV_RECORD))		{view = new TVRecordList(c);
		}else if(type.equals(TypeConsts.FAVORITE_TV_RECORD))
														{view = new TVFavoriteRecordList(c);
		}else if(type.equals(TypeConsts.DATE_LIST))		{view = new DateList(c, map);
		}else if(type.equals(TypeConsts.RECORD_LIST))	{view = new RecordList(c, map);
		}else											{view = get_unknown(type);
		}
		if(implementsInterface(view, List.class)){
			((List)view).setFactory(this);
			((List)view).setOnItemClickListener(new KVItemClickListener(((List)view)));
		}
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	private static boolean implementsInterface(Object object, Class inter){
		if(inter.isInstance(object)){
		     return true;
		 }else{
			 return false;
		 }
	}
	
	private View get_unknown(String type ){
		TextView text = new TextView(c);
		text.setText("тип " + type);
		text.setTextSize(30);
		text.setGravity(Gravity.CENTER);
		//return text;
		return null;
	}
	
	private static View get_null(){
		return null;
	}
	
}
