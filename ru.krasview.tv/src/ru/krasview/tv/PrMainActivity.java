package ru.krasview.tv;

import ru.krasview.kvlib.indep.ListAccount;

import com.example.kvlib.R;

import android.app.Activity;
import android.os.Bundle;

public class PrMainActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(ListAccount.fromLauncher) {
			getActionBar().setIcon(R.drawable.kv_logo);
			getActionBar().setLogo(R.drawable.kv_logo);
			getActionBar().setTitle("Настройки");
		}

		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new PrFragment()).commit();
	}
}
