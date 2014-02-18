package ru.krasview.kvlib.indep;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.krasview.kvlib.indep.AuthAccount;
import ru.krasview.kvlib.indep.consts.AuthEnterConsts;
import ru.krasview.kvlib.indep.consts.AuthRequestConst;
import ru.krasview.kvlib.interfaces.FatalErrorExitListener;
import ru.krasview.kvlib.interfaces.OnLoadCompleteListener;
import ru.krasview.secret.ApiConst;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class Parser
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
	
	public static boolean isOnline() 
	{ 
	    ConnectivityManager cm = (ConnectivityManager)mContext
	    		.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo nInfo = cm.getActiveNetworkInfo(); 
	    if (nInfo != null && nInfo.isConnected()){
	        return true; 
	    }else{  
	        return false;
	    }
	 }
	
	@SuppressWarnings("deprecation")
	public static String getXML(String address, String params)
	{
		if(address == null){
			return "";
		}
		if(Uri.parse(address) != null && Uri.parse(address).getQuery() == null){
			address = address + "?" 
					+ "login=" + URLEncoder.encode((AuthAccount.login==null)
					?"":AuthAccount.login) + "&" 
					+ "password=" + URLEncoder.encode((AuthAccount.password==null)
					?"":AuthAccount.password);	    	
		}else{
			address = address + "&" 
					+ "login=" + URLEncoder.encode((AuthAccount.login==null)
					?"":AuthAccount.login) + "&" 
					+ "password=" + URLEncoder.encode((AuthAccount.password==null)
					?"":AuthAccount.password);
		
		}
		if(params != null){
			address = address + "&" + params;
		}
		return getXML(address);
	}
	
	@SuppressWarnings("deprecation")
	public static String getXML(String address, String params, int request_auth_type)
	{
		if(address == null||Uri.parse(address) == null){
			return "";
		}
		
		if(Uri.parse(address).getQuery() == null){
			address = address + "?" ;		    	
		}else{
			address = address + "&";
		}
		
		if(params != null&&!params.equals("")){
			address = address + params + "&";
		}
		String auth_address = address;
		switch(request_auth_type){
		case AuthRequestConst.AUTH_NONE:	
			auth_address = address;
			break;
		case AuthRequestConst.AUTH_KRASVIEW:
			if(AuthAccount.auth_type != AuthEnterConsts.AUTH_TYPE_KRASVIEW && AuthAccount.auth_type != AuthEnterConsts.AUTH_TYPE_KRASVIEW_SOCIAL){
				return "";
			}
			auth_address = address + "hash=" + AuthAccount.hash;	
			break;
		case AuthRequestConst.AUTH_TV:
			if(AuthAccount.auth_type != AuthEnterConsts.AUTH_TYPE_KRASVIEW 
				&& AuthAccount.auth_type != AuthEnterConsts.AUTH_TYPE_TV 
				&& AuthAccount.auth_type != AuthEnterConsts.AUTH_TYPE_KRASVIEW_SOCIAL){
				return null;
			}else if( AuthAccount.auth_type == AuthEnterConsts.AUTH_TYPE_KRASVIEW_SOCIAL){
				auth_address = address + "hash=" + AuthAccount.tv_hash; 
			}else{
				auth_address = address 
					+ "login=" + URLEncoder.encode(AuthAccount.login) 
					+ "&password=" + URLEncoder.encode(AuthAccount.password); 
			}
			break;
		}
		String result = getXML(auth_address);
		if(result.equals("wrong hash")){		
			if(AuthAccount.auth_type == AuthEnterConsts.AUTH_TYPE_KRASVIEW_SOCIAL){
				exitFromApplication();
				return "";
			}else if(AuthAccount.auth_type == AuthEnterConsts.AUTH_TYPE_KRASVIEW_SOCIAL){
				return "";
			}
			AuthAccount.hash = getXML(ApiConst.KRASVIEW_AUTH+"?login=" 
			+ URLEncoder.encode(AuthAccount.login) 
			+ "&password=" + URLEncoder.encode(AuthAccount.password));
			if(AuthAccount.hash.equals("error")){
				exitFromApplication();
				return "";
			}
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			prefs.edit().putString("pref_hash", AuthAccount.hash).commit();			
			auth_address = address + "hash=" + AuthAccount.hash;
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
    
	public static Document XMLfromString(String xml)
	{
	    Document doc = null;
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        try {
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes("UTF8"));
	        doc = db.parse(is);	 
	        } catch (ParserConfigurationException e) {
	            System.out.println("XML parse error: " + e.getMessage());
	            return null;
	        } catch (SAXException e) {
	            System.out.println("Wrong XML file structure: " + e.getMessage());
	            return null;
	        } catch (IOException e) {
	            System.out.println("I/O exeption: " + e.getMessage());
	            return null;
	        }	 
	        return doc;
	}
	
	public static String getValue(String tag, Node node)
	{	
		Node n = node.getAttributes().getNamedItem(tag);
		if(n!=null){
			return node.getAttributes().getNamedItem(tag).getTextContent();
		}
		NodeList nlList  = ((Element)node).getElementsByTagName(tag);
		try{
		if(nlList.item(0).getFirstChild() == null){
			return "null";
		}}catch(Exception e){
			return null;
		}
		return nlList.item(0).getFirstChild().getNodeValue();		
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
		getXMLAsyncTask task = new Parser.getXMLAsyncTask();
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