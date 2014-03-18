package ru.krasview.tv;

import ru.krasview.tv_site.R;

import ru.krasview.kvlib.indep.AuthAccount;
import ru.krasview.kvlib.indep.consts.IntentConst;
import ru.krasview.secret.ApiConst;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

public class GuestAuthActivity extends Activity {
	
	CheckBox box;//чек бокс "Не показывать больше это окно"
	//"pref_guest_check" - booleans
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kv_activity_guest);
		box = (CheckBox)findViewById(R.id.checkBox1);
		enter();
	}
	
	private void enter(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		prefs.edit().putString("pref_login", "")
					.putString("pref_password", "")
					.putInt("pref_auth_type", AuthAccount.AUTH_TYPE_GUEST)
					.putBoolean("pref_guest_check", box.isChecked()).commit();
		Intent a = new Intent(IntentConst.ACTION_MAIN_ACTIVITY);
		startActivity(a);
		this.setResult(RESULT_OK);
		this.finish();
	}
	
	public void onClick(View v){
		switch(v.getId()){
		case R.id.kv_auth_guest_button:
			enter();
			break;
		case R.id.kv_auth_registration_button:
			Intent b = new Intent(Intent.ACTION_VIEW);
			b.setData(Uri.parse(ApiConst.CREATE_ACCOUNT));
			startActivity(b);
			this.finish();
			break;
		}
	}

}
