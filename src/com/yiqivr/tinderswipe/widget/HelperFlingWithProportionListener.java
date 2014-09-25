package com.yiqivr.tinderswipe.widget;

import android.view.View;

public interface HelperFlingWithProportionListener {
	public void onCardExited();

	public void leftExit(Object dataObject);

	public void rightExit(Object dataObject);

	public void topExit(Object dataObject);

	public void bottomExit(Object dataObject);

	public void flingResetOrigin(View selfView);

	public void flingOccurPercent(View selfView, int percent, boolean swipeTop);
}
