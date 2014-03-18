package ru.krasview.tv;

import ru.krasview.tv_site.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PrFragment extends PreferenceFragment {
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.kv_settings);
	  }

}
