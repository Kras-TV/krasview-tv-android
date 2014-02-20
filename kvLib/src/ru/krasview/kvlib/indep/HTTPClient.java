package ru.krasview.kvlib.indep;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ru.krasview.kvlib.indep.AuthAccount;
import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.interfaces.FatalErrorExitListener;
import ru.krasview.kvlib.interfaces.OnLoadCompleteListener;
import ru.krasview.secret.ApiConst;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class HTTPClient
{
	static Context mContext = null;
	static FatalErrorExitListener exitListener = null;
	
	public static void setContext(Context context)
	{
		mContext = context;
	}
	
	public static void setExitListener(FatalErrorExitListener l)
	{
		exitListener = l;
	}
	
	private static String getXML(String address, String params)
	{
		if(address == null){
			return "";
		}
		if(Uri.parse(address) != null && Uri.parse(address).getQuery() == null){
			address = address + "?" ;
					//+ "login=" + URLEncoder.encode((AuthAccount.getInstance().getLogin() == null)
					//?"":AuthAccount.getInstance().getLogin()) + "&" 
					//+ "password=" + URLEncoder.encode((AuthAccount.getInstance().getPassword() == null)
					//?"":AuthAccount.getInstance().getPassword());	    	
		}else{
			address = address + "&" ;
				//	+ "login=" + URLEncoder.encode((AuthAccount.getInstance().getLogin()==null)
				//	?"":AuthAccount.getInstance().getLogin()) + "&" 
				//	+ "password=" + URLEncoder.encode((AuthAccount.getInstance().getPassword()==null)
				//	?"":AuthAccount.getInstance().getPassword());
		
		}
		if(params != null){
			address = address + params + "&";
		}
		return getXML(address);
	}
	
	@SuppressWarnings("deprecation")
	public static String getXML(String address, String params, int request_auth_type)
	{
		Log.i("Debug", "getXML" + address + " " + params );
		
		if(address == null||Uri.parse(address) == null){
			return "";
		}
		
		if(Uri.parse(address).getQuery() == null){
			address = address + "?" ;		    	
		}else{
			address = address + "&";
		}
		
		if(params != null && !params.equals("")){
			address = address + params + "&";
		}
		String auth_address = address;
		AuthAccount account = AuthAccount.getInstance();
		switch(request_auth_type){
		case AuthRequestConst.AUTH_NONE:	
			auth_address = address;
			break;
		case AuthRequestConst.AUTH_KRASVIEW:
			if(!account.isKrasviewAccount()){
				return "";
			}
			auth_address = address + "hash=" + account.getHash();	
			break;
		case AuthRequestConst.AUTH_TV:
			if(!account.isTVAccount()){
				return null;
			}else if(account.isSocialNetworkAccount()){
				auth_address = address + "hash=" + account.getTvHash(); 
			}else{
				auth_address = address 
					+ "login=" + URLEncoder.encode(AuthAccount.getInstance().getLogin()) 
					+ "&password=" + URLEncoder.encode(AuthAccount.getInstance().getPassword()); 
			}
			break;
		}
		String result = getXML(auth_address);
		if(result.equals("wrong hash")){		
			if(account.isSocialNetworkAccount()){
				exitFromApplication();
				return "";
			}
			String hash = getXML(ApiConst.KRASVIEW_AUTH + "?login=" 
					+ URLEncoder.encode(account.getLogin()) 
					+ "&password=" + URLEncoder.encode(account.getPassword()));
			AuthAccount.getInstance().setHash(hash);
			if(AuthAccount.getInstance().getHash().equals("error")){
				exitFromApplication();
				return "";
			}
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			prefs.edit().putString("pref_hash", account.getHash()).commit();			
			auth_address = address + "hash=" + account.getHash();
			result = getXML(auth_address);
		}	
		return result;
	}

	private static void exitFromApplication()
	{
		if(exitListener != null){
			exitListener.onError();
		}
	}

    private static String getXML(String address)
    {
    	String line = "";
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
           if(httpEntity!=null){
        	   line = EntityUtils.toString(httpEntity, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
        } catch (MalformedURLException e) {
            line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
        } catch (IOException e) {
            line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
        }
        return line;
    }
    
    public static HttpResponse post(String address, String id, String msg)
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(address);
        HttpResponse response = null;
        try {
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        	nameValuePairs.add(new BasicNameValuePair(id, msg));
        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	response = httpClient.execute(httpPost);
 
        } catch (UnsupportedEncodingException e) {
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return response;
    }
    
    public static String getXMLFromFile(String addres, Context context)
    {
    	String xmlString = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(addres);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            xmlString = new String(data);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return xmlString;
    }
    
	public static Bitmap getImage(String adress) 
	{
		URL url = null;
		Bitmap bmp;
		try {
			url = new URL(adress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection)url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		conn.setDoInput(true);
		try {
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		InputStream is = null;
		try {
			is = conn.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bmp = BitmapFactory.decodeStream(is);
		return bmp;
	}
	   
	public static String auth(final String address, 
			final String login, final String password)
	{
		if(login.equals("")) return address;
		if(password.equals("")) return address;
		Uri uri = Uri.parse(address);
	    Uri.Builder b = uri.buildUpon();
	    b.encodedAuthority(login + ":" + password + "@" 
	    		+ uri.getHost() + ":" + uri.getPort());
	    String url = b.build().toString();
		return url;
	}
	   
	public static String auth(final String address)
	{
		   return auth(address, "", "");
	}
	   
	public static void getXMLAsync(String address, String params, OnLoadCompleteListener listener)
	{
		getXMLAsyncTask task = new HTTPClient.getXMLAsyncTask();
	  	task.execute(address, params, listener);
	}
	    
	private static class getXMLAsyncTask extends AsyncTask<Object, Object, String>
	{
		OnLoadCompleteListener listener1;
		String address = null;			
		
		@Override
		protected String doInBackground(Object... params) 
		{
			listener1 = (OnLoadCompleteListener)params[2];
			address = (String)params[0];
			return getXML(address, (String)params[1]);	
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			listener1.loadComplete(address, result);
			listener1.loadComplete(result);
		}
	};	
}