package ru.krasview.tv;

import java.net.URLEncoder;
import java.util.Map;

import ru.krasview.kvlib.adapter.CombineSimpleAdapter;
import ru.krasview.kvlib.indep.AuthAccount;
import ru.krasview.kvlib.indep.HTTPClient;
import ru.krasview.kvlib.indep.ListAccount;
import ru.krasview.kvlib.indep.SearchAccount;
import ru.krasview.secret.ApiConst;
import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.indep.consts.TagConsts;
import ru.krasview.kvlib.indep.consts.TypeConsts;
import ru.krasview.kvlib.interfaces.FatalErrorExitListener;
import ru.krasview.kvlib.widget.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.example.kvlib.R;

public abstract class KVSearchAndMenuActivity extends SherlockFragmentActivity
	implements FatalErrorExitListener {

	AuthAccount account = AuthAccount.getInstance();

	EditText editsearch;
	SearchFragment searchFragment;
	View searchHost;

	protected abstract void setSearch(boolean a);
	protected abstract void exit();
	protected abstract void home();
	protected abstract void refresh();
	protected abstract void requestFocus();

	@Override
	public void onError() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void setSearchWidget() {
		//установка поисковых штук
		searchHost = findViewById(R.id.search);
		FragmentManager fragmentManager = getSupportFragmentManager();
		searchFragment = (SearchFragment) fragmentManager.findFragmentById(R.id.fragment1);
		setSearch(false);
	}

	private TextWatcher textWatcher = new TextWatcher() {
		@SuppressWarnings("deprecation")
		@Override
		public void afterTextChanged(Editable s) {
			String search_string = URLEncoder.encode(s.toString());
			searchFragment.goSearch(search_string);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
		                              int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
		                          int count) {}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.kv_activity_animator, menu);
		MenuItem loginItem = menu.findItem(R.id.kv_login_item );
		String str = "";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int auth_type = prefs.getInt("pref_auth_type", AuthAccount.AUTH_TYPE_UNKNOWN);
		String login = prefs.getString("pref_login", "");
		String password = prefs.getString("pref_password", "");
		switch(auth_type) {
		case AuthAccount.AUTH_TYPE_GUEST:
			str = "Гость";
			break;
		case AuthAccount.AUTH_TYPE_TV:
			str = "Абонент";
			break;
		case AuthAccount.AUTH_TYPE_UNKNOWN:
			str = "Неизвестно";
			break;
		}
		String locLog = str;
		if(login == null||password == null) {
		} else {
			locLog = (login.equals(""))?str:login;
		}
		loginItem.setTitle(locLog);

		if(ListAccount.fromLauncher) {
			menu.findItem(R.id.kv_search_item).setVisible(false);
		}

		editsearch = (EditText) menu.findItem(R.id.kv_search_item).getActionView();
		editsearch.addTextChangedListener(textWatcher);

		MenuItem menuSearch = menu.findItem(R.id.kv_search_item);
		menuSearch.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				setSearch(true);
				editsearch.requestFocus();
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				setSearch(false);
				editsearch.setText("");
				editsearch.clearFocus();
				SearchAccount.search_string = null;
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);
				return true;
			}
		});

		requestFocus();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_game:
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if(currentapiVersion >= 11) {
				Intent settingsActivity = new Intent(getBaseContext(), PrMainActivity.class);
				startActivity(settingsActivity);
				return true;
			} else {
				Intent settingsActivity = new Intent(getBaseContext(), OldPreferenceActivity.class);
				startActivity(settingsActivity);
				return true;
			}

		case R.id.kv_login_item:
			return true;
		case R.id.exit:
			exit();
		case R.id.kv_home_item:
			home();
			return true;
		case R.id.kv_refresh_item:
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	Map<String, Object> contextMenuMap;
	CombineSimpleAdapter contextMenuAdapter;


	@SuppressWarnings("unchecked")
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		contextMenuAdapter = ((List)v).getAdapter();
		contextMenuMap = (Map<String, Object>) ((List)v).getAdapter().getItem(info.position);
		menu.setHeaderTitle((CharSequence)contextMenuMap.get("name"));

		if(contextMenuMap.get(TagConsts.TYPE) != null
		        && contextMenuMap.get(TagConsts.TYPE).equals(TypeConsts.CHANNEL)) {
			if(!account.isTVAccount()) {
				return;
			}
			if(contextMenuMap.get("star").equals("0")) {
				menu.add(Menu.NONE, 0, 0, "добавить в избранное");
			} else {
				menu.add(Menu.NONE, 1, 0, "удалить из избранного");
			}
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		int menuItemIndex = item.getItemId();
		String[] menuItems = {"add", "remove"};
		String menuItemName = menuItems[menuItemIndex];
		if(menuItemName.equals("add")) {
			if(contextMenuMap != null) {
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						String address = ApiConst.STAR;
						String params = "channel_id=" + arg0[0];
						return HTTPClient.getXML(address, params, AuthRequestConst.AUTH_TV);
					}

					@Override
					protected void onPostExecute(String result) {
						String str;
						if(result.equals("<results status=\"error\"><msg>Can't connect to server</msg></results>")) {
							str = "Невозможно подключиться к серверу";
						} else {
							contextMenuMap.put("star", "1");
							contextMenuAdapter.notifyDataSetChanged();
							str = "Канал добавлен в избранное: " + contextMenuMap.get("name");
						}

						Toast toast = Toast.makeText(getApplicationContext(),
						                             str, Toast.LENGTH_SHORT);
						toast.show();
						return;
					}
				};
				task.execute((String)contextMenuMap.get("id"));
			}
		} else if(menuItemName.equals("remove")) {
			if(contextMenuMap != null) {
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						String address = ApiConst.UNSTAR;
						String params = "channel_id=" + arg0[0];
						return HTTPClient.getXML(address, params, AuthRequestConst.AUTH_TV);
					}

					@Override
					protected void onPostExecute(String result) {
						String str;
						if(result.equals("<results status=\"error\"><msg>Can't connect to server</msg></results>")) {
							str = "Невозможно подключиться к серверу";
						} else {
							contextMenuAdapter.getData().remove(contextMenuMap);
							contextMenuAdapter.notifyDataSetChanged();
							str = "Канал удален из избранного: " + contextMenuMap.get("name");
						}

						Toast toast = Toast.makeText(getApplicationContext(),
						                             str, Toast.LENGTH_SHORT);
						toast.show();
						return;
					}
				};
				task.execute((String)contextMenuMap.get("id"));
			}
		}
		return true;
	}

}
