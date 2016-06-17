package ru.krasview.kvlib.animator;

import android.content.Context;
import android.util.AttributeSet;

import android.widget.ViewAnimator;

public abstract class LeftRightAnimator extends ViewAnimator {
	
	private AnimatorData data = new AnimatorData();

	public LeftRightAnimator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public LeftRightAnimator(Context context) {
		super(context);
	}
	
	@Override
	protected void onSizeChanged (int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		
		data.setData(w, 1000);
	}
	
	@Override
	final public void showNext(){
		if(this.getDisplayedChild() < this.getChildCount()-1){
			super.showNext();
			getCurrentView().requestFocus();
		}
	}
	
	@Override
	final public void showPrevious(){
		if(this.getDisplayedChild() > 0){
			super.showPrevious();
			getCurrentView().requestFocus();
		}
	}
	
	
	private boolean showNext_boolean(){
		if(this.getDisplayedChild() < this.getChildCount()-1){
			super.showNext();
			return true;
		}
		return false;
	}
	
	private boolean showPrevious_boolean(){
		if(this.getDisplayedChild() > 0){
			super.showPrevious();
			return true;
		}
		return false;
	}
	
	protected boolean goLeft(){
		setInAnimation(data.getInAnimLeft());
    	setOutAnimation(data.getOutAnimLeft());
		return showPrevious_boolean();
	}
	
	protected boolean goRight(){
		setInAnimation(data.getInAnimRight());
    	setOutAnimation(data.getOutAnimRight());
		return showNext_boolean();
	}


}
