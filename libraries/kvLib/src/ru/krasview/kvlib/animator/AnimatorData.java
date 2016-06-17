package ru.krasview.kvlib.animator;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimatorData {
	private Animation inAnimLeft;
	private Animation outAnimLeft;
	private Animation inAnimRight;
	private Animation outAnimRight;

	public AnimatorData() {
	}
	
	public void setData(int w, int time){
		inAnimLeft = new TranslateAnimation(-w, 0, 0, 0);
    	inAnimLeft.setDuration(time);
    	outAnimLeft = new TranslateAnimation(0, w , 0, 0);
    	outAnimLeft.setDuration(time);
    	
    	inAnimRight = new TranslateAnimation(w, 0, 0, 0);
    	inAnimRight.setDuration(time);
    	outAnimRight = new TranslateAnimation(0, -w , 0, 0);
    	outAnimRight.setDuration(time);
	}
	
	public Animation getInAnimLeft(){
		return inAnimLeft;
	}
	
	public Animation getInAnimRight(){
		return inAnimRight;
	}
	
	public Animation getOutAnimLeft(){
		return outAnimLeft;
	}
	
	public Animation getOutAnimRight(){
		return outAnimRight;
	}
	
}