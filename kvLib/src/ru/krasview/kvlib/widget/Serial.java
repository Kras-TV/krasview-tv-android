package ru.krasview.kvlib.widget;

import java.util.Map;

import com.example.kvlib.R;

import android.content.Context;
import android.view.View;
import ru.krasview.kvlib.indep.ListAccount;
import ru.krasview.kvlib.interfaces.ViewProposeListener;
import ru.krasview.kvlib.interfaces.ViewPropotionerInterface;
import ru.krasview.kvlib.widget.SerialDescription;

public class Serial extends SerialDescription implements ViewPropotionerInterface {
	
	private Map<String, Object> mMap;

	public Serial(Context context, Map<String, Object> map) {
		super(context);
		mMap = map;
	}

	@Override
	public ViewProposeListener getViewProposeListener() {
		return mViewProposeListener;
	}

	@Override
	public void init() {
		super.setTag(mMap);
		button.setVisibility(View.GONE);
		button.setText("Смотреть");
		if(ListAccount.fromLauncher){
			button.setBackgroundResource(R.drawable.selector);
		}
	}

	@Override
	public void refresh() {

	}
	
	@Override
	public void enter(){
	}

	@Override
	public void exit() {
	}

}
