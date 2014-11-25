package ru.krasview.kvlib.widget;

import java.util.Map;

import ru.krasview.kvlib.adapter.CombineSimpleAdapter;
import ru.krasview.kvlib.indep.ListAccount;
import ru.krasview.kvlib.indep.consts.IntentConst;
import ru.krasview.kvlib.indep.consts.RequestConst;
import ru.krasview.kvlib.indep.consts.TagConsts;
import ru.krasview.kvlib.indep.consts.TypeConsts;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class KVItemClickListener implements OnItemClickListener {
	private List mList;

	KVItemClickListener(List list) {
		mList = list;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			itemClick(parent, view, position, id);
	}

	@SuppressWarnings("unchecked")
	protected void itemClick(AdapterView<?> parent, View view, int position, long id) {
		Map<String, Object> m = (Map<String, Object>)parent.getItemAtPosition(position);
		String type = (String)m.get(TagConsts.TYPE);
		if(type == null){
			return;
		}

		if(type != null && type.equals(TypeConsts.BILLING)) {
			Intent intent = new Intent();
			intent.setAction(IntentConst.ACTION_BILLING);
			((Activity)mList.getContext()).startActivityForResult(intent, RequestConst.REQUEST_CODE_BILLING);
			return;
		}else if(type != null && (type.equals(TypeConsts.VIDEO)
				|| type.equals(TypeConsts.CHANNEL)) || type.equals(TypeConsts.TV_RECORD_VIDEO)) {
			Intent intent;
			if(playInSystemChoice(type)) {
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse((String) m.get("uri")), "video/*");
				((Activity)mList.getContext()).startActivity(intent);
			} else {
				intent = new Intent();
				intent.setAction(IntentConst.ACTION_VIDEO_LIST);
				intent.putExtra("index", position - ((CombineSimpleAdapter)parent.getAdapter()).getConstDataCount());
				boolean rt = false;
				if(m.get("request_time") != null && (Boolean)m.get("request_time") == true){
					rt = true;
				}
				intent.putExtra("request_time", rt);
				ListAccount.adapterForActivity = new SimpleAdapter(mList.getContext(), mList.getAdapter().getData(), 0, null, null);
				ListAccount.currentList = mList;
				((Activity)mList.getContext()).startActivityForResult(intent, RequestConst.REQUEST_CODE_VIDEO);
			}
			return;
		}
		if(mList.getFactory() != null && mList.getViewProposeListener()!= null){
			mList.getViewProposeListener().onViewProposed(parent, mList.getFactory().getView(m, mList.getContext()));
		}
	}

	private boolean playInSystemChoice(String t) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mList.getContext());
		String player;
		if(t.equals(TypeConsts.CHANNEL)||t.equals(TypeConsts.TV_RECORD_VIDEO)) {
			player = prefs.getString("video_player_tv", "VLC");
		} else if(t.equals(TypeConsts.VIDEO)) {
			player = prefs.getString("video_player_serial", "std");
		} else {
			return false;
		}
		if(player.equals("system")) {
			return true;
		}else{
			return false;
		}
	}
}
