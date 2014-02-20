package ru.krasview.tv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.kvlib.R;

import ru.krasview.kvlib.adapter.CombineSimpleAdapter;
import ru.krasview.kvlib.indep.AuthAccount;
import ru.krasview.kvlib.indep.HTTPClient;
import ru.krasview.kvlib.indep.HeaderAccount;
import ru.krasview.kvlib.interfaces.OnLoadCompleteListener;
import ru.krasview.kvlib.widget.lists.PackageList;
import ru.krasview.secret.ApiConst;
import ru.krasview.secret.Billing;
import ru.krasview.tv.billing.util.IabHelper;
import ru.krasview.tv.billing.util.IabHelper.QueryInventoryFinishedListener;
import ru.krasview.tv.billing.util.IabResult;
import ru.krasview.tv.billing.util.Inventory;
import ru.krasview.tv.billing.util.Purchase;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

public class BillingActivity extends Activity implements OnLoadCompleteListener, OnItemClickListener {

	IabHelper mHelper;
	PackageList mList;
	
	private void setResultAndFinish(){
		HeaderAccount.hideHeader = true;
		Toast.makeText(this, "Пакет подключен", Toast.LENGTH_LONG).show();
		setResult(RESULT_OK);
		this.onBackPressed();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));
		setContentView(R.layout.activity_billing);
		FrameLayout layout = (FrameLayout)findViewById(R.id.layout);
		mList = new PackageList(this, null);
		mList.init();
		mList.setOnLoadCompleteListener(this);
		mList.setOnItemClickListener(this);
		
		layout.addView(mList);
		String base64EncodedPublicKey = Billing.base64EncodedPublicKey;
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			   public void onIabSetupFinished(IabResult result) {
			      if (!result.isSuccess()) {
			         // Oh noes, there was a problem.
			         Log.i("Debug", "Problem setting up In-app Billing: " + result);
			      }     
			      mList.refresh();
			      Log.i("Debug", "Hooray, IAB is fully set up!");
			      
			   }
			});
	}
	
	QueryInventoryFinishedListener 
	   mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
	   public void onQueryInventoryFinished(IabResult result, Inventory inventory)   
	   {
	      if (result.isFailure()) {
	         // Обработка ошибки
	    	  Log.i("Debug", "Произошла ошибка");
	         return;
	       }
	       go(inventory);
	      
	   }
	};
	
	@SuppressWarnings("unchecked")
	private void go(Inventory inventory){
		CombineSimpleAdapter adapter = mList.getAdapter();
    	for(int i = 0; i < adapter.getCount(); i++){
    		Map<String,Object> map = (Map<String, Object>)adapter.getItem(i);
			String id = (String)map.get("sku");
			if(inventory.hasDetails(id)){
				String productType = inventory.getSkuDetails(id).getType();
				if(productType.equals("subs")){
					productType = "подписка на месяц";
				}else if(productType.equals("inapp")){
					productType = "на месяц";
				}else{
					productType = "";
				}
				
				map.put("inMarket", true);
				map.put("productType", productType);
				map.put("price", inventory.getSkuDetails(id).getPrice());				
				adapter.notifyDataSetChanged();
			}
			else{
				String productType = (String)map.get("product");
				if(productType == null){
					
				}else{
					if(productType.equals("subscription")){
						productType = "подписка на месяц";
					}else if(productType.equals("portion")){
						productType = "на месяц";
					}else{
						productType = "";
					}
					map.put("productType", productType);
				}		
				map.put("inMarket", false);
				map.put("price", "Недоступно");
				adapter.notifyDataSetChanged();
			}
			
    	}
    }
	
	class PurchaseFinishedListener implements IabHelper.OnIabPurchaseFinishedListener{
		Map<String, Object> m;
		PurchaseFinishedListener(Map<String, Object> m){
			this.m = m;
		}
		 @SuppressWarnings("unchecked")
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
		   {
			   //обарабтываем прошедшую выплату
			   Log.i("Debug", " purchasing: " + result);
			   
		      if (result.isFailure()) {
		    	  //если ошибка
		         Log.i("Debug", "Error purchasing: " + result);
		         return;
		      }      
		      
		      new SendOnServerTask().execute(m);
		   }
	}
	
	
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();
    }

	@SuppressWarnings("unchecked")
	@Override
	public void loadComplete(String result) {	
		List<String> additionalSkuList = new ArrayList<String>();
	    CombineSimpleAdapter adapter = mList.getAdapter();
	    for(int i = 0; i < adapter.getCount(); i++){
	    	Map<String,Object> map = (Map<String, Object>)adapter.getItem(i);
			//потестить
			additionalSkuList.add((String)map.get("sku"));
	    }
	    mHelper.queryInventoryAsync(true, additionalSkuList,
	    			mQueryFinishedListener);
	}

	@Override
	public void loadComplete(String address, String result) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			CombineSimpleAdapter adapter = (CombineSimpleAdapter) parent.getAdapter();
		
			Map<String, Object> m = (Map<String, Object>)adapter.getItem(position);
			if(m.get("inMarket") == null || !(Boolean)m.get("inMarket")){
				return;
			}else{
				//Запуск процесса покупки
				IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener 
				   = new PurchaseFinishedListener(m) ;
							
				mHelper.launchPurchaseFlow(BillingActivity.this, (String)m.get("sku"), 10001,   
				    	   mPurchaseFinishedListener, Billing.LAUNCH_PUNCHASE_FLOW_EXTRA);
				       
			}
	}
	
	private class SendOnServerTask extends AsyncTask<Map<String, Object>,Void,String>{

		@Override
		protected String doInBackground(Map<String, Object>... maps) {
			String packet = (String)maps[0].get("id");
			String hash = AuthAccount.getInstance().getTvHash();
			String address = ApiConst.SUBSCRIBE;
			String secret = Billing.getSecret(hash, packet);
			String params = "packet=" + packet + "&" + "secret=" + secret;
			String result = HTTPClient.getXML(address, params, AuthAccount.AUTH_TYPE_KRASVIEW);
			return result;
		}
		
		
		@Override
		protected void onPostExecute(String result){
			if(result.equals("ok"))
			{
				setResultAndFinish();
			}
		}
		
	 
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    // Pass on the activity result to the helper for handling
	    // NOTE: handleActivityResult() will update the state of the helper,
	    // allowing you to make further calls without having it exception on you
	    if (mHelper.handleActivityResult(requestCode, resultCode, data)) {
	        Log.d("Debug", "onActivityResult handled by IABUtil.");
	        //handlePurchaseResult(requestCode, resultCode, data);
	        return;
	    }

	    // What you would normally do
	    // ...
	}
}
