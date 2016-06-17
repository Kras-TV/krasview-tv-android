package ru.krasview.kvlib.interfaces;

import android.view.View;

public interface ViewProposeListener {
	public void onViewProposed(View parent, View v, String uri, int index);
	public void onViewProposed(View parent, View v);
}
