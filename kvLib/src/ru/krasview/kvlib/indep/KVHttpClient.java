package ru.krasview.kvlib.indep;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

import ru.krasview.kvlib.interfaces.OnLoadCompleteListener;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

public class KVHttpClient {
	public static String getXML(String address, String params)
	{
		return getXML(addParams(address, params));
	}

	public static String getXML(String address) {
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

	protected static String addParams(String address, String params){
		if(address == null||Uri.parse(address) == null){
			return "";
		}
		if(params == null){
			params = "";
		}
		if(Uri.parse(address) != null && Uri.parse(address).getQuery() == null){
			address = address + "?" ;
		}else{
			address = address + "&" ;
		}
		if(params != null){
			address = address + params + "&";
		}
		return address;
	}

	public static void getXMLAsync(String address, String params, OnLoadCompleteListener listener)
	{
		getXMLAsyncTask task = new getXMLAsyncTask();
		task.execute(address, params, listener);
	}

	private static class getXMLAsyncTask extends AsyncTask<Object, Object, String>
	{
		OnLoadCompleteListener listener1;
		String address = null;

		@Override
		protected String doInBackground(Object... params) {
			listener1 = (OnLoadCompleteListener)params[2];
			address = (String)params[0];
			return getXML(address, (String)params[1]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			listener1.loadComplete(address, result);
			listener1.loadComplete(result);
		}
	};

	public static Bitmap getImage(String adress) {
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

	public static String getXMLFromFile(String addres, Context context) {
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

	public static HttpResponse post(String address, String id, String msg) {
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
}
