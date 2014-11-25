package ru.krasview.kvlib.animator;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import ru.krasview.kvlib.interfaces.Factory;
import ru.krasview.kvlib.interfaces.PropotionerView;
import ru.krasview.kvlib.interfaces.ViewProposeListener;

/** @class NewAnimator
@brief Специализированное расширение класса Animator.

Хранит View в оболочках из PropotionerView,
поэтому может вызывать функции из соответствующих интерфейсов этих объектов.
Также  отвечает за добавление предложенных View.
*/
// нужно доделать: добавление и получение любого вью в обертке из PropotionerView
// обработку нажатий клавиш и прикосновений можно вынести в родительский класс,
// так как эти функции не зависят от PropotionerView.

public class NewAnimator extends LeftRightAnimator implements ViewProposeListener {
	private Factory mFactory;
	private String mStartType;

	public NewAnimator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private NewAnimator(Context context) {
		super(context);
	}

	public NewAnimator(Context context, Factory factory) {
		this(context);
		setFactory(factory);
	}

	final public void setFactory(Factory factory){
		mFactory = factory;
	}

	final protected Factory getFactory(){
		return mFactory;
	}

	public void init(String type) {
		mStartType = type;
		removeAllViews();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("type", type);
		if(mFactory!=null){
		View v = mFactory.getView(m, getContext());
			if(v != null){
				PropotionerView view;
				view = new PropotionerView(v, this);
				view.init();
				this.addView(view);
				view.enter();
			}
		}
	}

	public void refresh() {
		if(getCurrentView().getClass().equals(PropotionerView.class)) {
			((PropotionerView)getCurrentView()).refresh();
		}
	}

	public void home() {
		init(mStartType);
	}

//--------------------------------------------------------------------
// Override of Super Class Methods
//--------------------------------------------------------------------

private float fromPosition;
	@Override
	public boolean dispatchTouchEvent (MotionEvent event) {
		super.dispatchTouchEvent(event);

		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			fromPosition = event.getX();
			return true;
		case MotionEvent.ACTION_UP:
			float toPosition = event.getX();
			if(Math.abs(fromPosition - toPosition) < 200) {
				break;
			}
			if (fromPosition > toPosition){
				goRight();
			}
			else if (fromPosition < toPosition){
				goLeft();
			}
		}
		return false;
	}

	@Override
	public boolean dispatchKeyEvent (KeyEvent event) {
		boolean handled = super.dispatchKeyEvent(event);
		if(!handled){
			if((event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
					||event.getKeyCode() == KeyEvent.KEYCODE_BACK)
					&& event.getAction() == KeyEvent.ACTION_UP
					&& getDisplayedChild()!=0) {
				goLeft();
				return true;
			}
		}
		return handled;
	}

//--------------------------------------------------------------------
// Override of Animator Methods
//--------------------------------------------------------------------

	@Override
	protected boolean goLeft() {
		PropotionerView v = (PropotionerView)getCurrentView();
		boolean a = super.goLeft();
		if(a){
			v.exit();
			((PropotionerView)getCurrentView()).enter();
		}
		return a;
	}

	@Override
	protected boolean goRight() {
		PropotionerView v = (PropotionerView)getCurrentView();
		boolean a = super.goRight();
		if(a){
			v.exit();
			((PropotionerView)getCurrentView()).enter();
		}
		return a;
	}
	
//--------------------------------------------------------------------
// Implementation of ViewProposeListener
//--------------------------------------------------------------------

	@Override
	public void onViewProposed(View parent, View v, String uri, int index) {
		onViewProposed(parent, v);
	}

	@Override
	public void onViewProposed(View parent, View v) {
		if(parent != getCurrentView()){
			return;
		}
		for(int i = getChildCount()-1; i > getDisplayedChild(); i--) {
			removeViewAt(i);
		}
		if(v == null) {
			return;
		}
		PropotionerView pv = new PropotionerView(v, this);
		pv.init();
		super.addView(pv);
		goRight();
		getCurrentView().requestFocus();
	}
}
