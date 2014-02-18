package ru.krasview.kvlib.interfaces;

import java.util.Map;

import android.content.Context;
import android.view.View;

public interface Factory {
	public View getView(Map<String, Object> map, Context context);
}
