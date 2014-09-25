package com.yiqivr.tinderswipe.widget;


public interface HelperFlingListener {
	public void onCardExited();

	public void leftExit(Object dataObject);

	public void rightExit(Object dataObject);

	public void topExit(Object dataObject);

	public void bottomExit(Object dataObject);
	
}
