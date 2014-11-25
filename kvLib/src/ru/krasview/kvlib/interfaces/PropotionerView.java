package ru.krasview.kvlib.interfaces;

import ru.krasview.kvlib.interfaces.SearchInterface;
import ru.krasview.kvlib.interfaces.ViewProposeListener;
import ru.krasview.kvlib.interfaces.ViewPropotionerInterface;

import android.view.View;
import android.widget.FrameLayout;

/** @class PropotionerView
@brief Класс-обертка для помещения View в Animator.

Позволяет складывать в Animator как View, реализующие интерфейсы
ViewPropotionerInterface и SearchInterface, так и не реализующие их.
Также позволяет содержащимся в Animator объектам View
предлагать новый View-объект аниматору.
*/
public class PropotionerView extends FrameLayout implements ViewPropotionerInterface, 
															ViewProposeListener, 
															SearchInterface{
	private ViewProposeListener mViewProposeListener;
	private ViewPropotionerInterface viewIntrerface;
	private SearchInterface searchInterface;

	public PropotionerView(View v, ViewProposeListener l){
		super(v.getContext());
		addView(v);
		if(implementsInterface(v, ViewPropotionerInterface.class)){
			viewIntrerface = (ViewPropotionerInterface)v;
		}else{
			viewIntrerface = null;
		}
		if(implementsInterface(v, SearchInterface.class)){
			searchInterface = (SearchInterface)v;
		}else{
			searchInterface = null;
		}
		setViewProposeListener(l);
	}

	@SuppressWarnings("rawtypes")
	private static boolean implementsInterface(Object object, Class inter){
		if(inter.isInstance(object)){
		     return true;
		 }else{
			 return false;
		 }
	}

//--------------------------------------------------------------------
// Implementation of ViewPropotionerInterface
//--------------------------------------------------------------------

	@Override
	public void setViewProposeListener(ViewProposeListener l){
		if(viewIntrerface != null){
			viewIntrerface.setViewProposeListener(this);
		}
		mViewProposeListener = l;
	}

	@Override
	public ViewProposeListener getViewProposeListener(){
		return mViewProposeListener;
	}

	@Override
	public void init(){
		if(viewIntrerface != null){
			viewIntrerface.init();
		}
		refresh();
	}

	@Override
	public void refresh() {
		if(viewIntrerface != null){
			viewIntrerface.refresh();
		}
	}

	@Override
	public void enter() {
		if(viewIntrerface != null){
			viewIntrerface.enter();
		}
	}

	@Override
	public void exit() {
		if(viewIntrerface != null){
			viewIntrerface.exit();
		}
	}

//--------------------------------------------------------------------
// Implementation of ViewProposeListener
//--------------------------------------------------------------------

	@Override
	public void onViewProposed(View parent, View v, String uri, int index) {
		onViewProposed(parent,  v);
	}

	@Override
	public void onViewProposed(View parent, View v) {
		getViewProposeListener().onViewProposed(this, v);
	}

//--------------------------------------------------------------------
// Implementation of SearchInterface
//--------------------------------------------------------------------

	@Override
	public void goSearch(String str) {
		if(searchInterface != null){
			searchInterface.goSearch(str);
		}
	}
}
