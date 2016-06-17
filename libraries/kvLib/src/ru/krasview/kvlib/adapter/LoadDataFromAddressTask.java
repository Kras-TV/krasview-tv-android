package ru.krasview.kvlib.adapter;

import ru.krasview.kvlib.indep.HTTPClient;
import android.os.AsyncTask;

class LoadDataFromAddressTask extends AsyncTask<String, Void, String>
{
	private CombineSimpleAdapter mAdapter;
	LoadDataFromAddressTask(CombineSimpleAdapter adapter){
		super();
		mAdapter = adapter;
	}
	@Override
	protected String doInBackground(String... arg0) 
	{
		String str = HTTPClient.getXML(arg0[0], arg0[1], mAdapter.withAuth);
		return str;
	}
	
    protected void onPostExecute(String result) 
    {
    	if(result != null){
    		LoadDataToGUI(result);
    	}
    }
    
    private void LoadDataToGUI(String str)
	{
		LoadDataToGUITask task = new LoadDataToGUITask(mAdapter);
		task.execute(str);
	}
}