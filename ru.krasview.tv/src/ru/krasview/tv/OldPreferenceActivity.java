package ru.krasview.tv;

import ru.krasview.tv_site.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class OldPreferenceActivity extends PreferenceActivity {

	  @SuppressWarnings("deprecation")
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.kv_settings);
	  }
	}