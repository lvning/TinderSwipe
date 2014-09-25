package com.yiqivr.tinderswipe.widget;


public interface OnTopBottomFlingListener extends OnBaseFlingListener {
	void onTopCardExit(Object dataObject);

	void onBottomCardExit(Object dataObject);

}
