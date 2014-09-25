package com.yiqivr.tinderswipe.widget;

import android.view.View;

public interface HelperFlingWithProportionListener extends HelperFlingListener {
	public void flingResetOrigin(View selfView);

	public void flingOccurPercent(View selfView, int percent, boolean swipeTop);
}
