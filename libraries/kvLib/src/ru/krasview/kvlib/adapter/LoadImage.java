package ru.krasview.kvlib.adapter;

import java.util.Map;

import ru.krasview.kvlib.indep.HTTPClient;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

class LoadImage extends AsyncTask<String, Void, Bitmap> {
	ImageView mImage;
	Map<String, Object> mMap;
	CombineSimpleAdapter mAdapter;

	LoadImage(CombineSimpleAdapter adapter, ImageView image, Map<String, Object> hm) {
		super();
		mImage = image;
		mMap = hm;
		mAdapter = adapter;
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {
		return HTTPClient.getImage(arg0[0]);
	}

	@Override
	protected void onPostExecute(Bitmap bmp) {
		mMap.put("image", bmp);
		mAdapter.notifyDataSetChanged();
	}
}
