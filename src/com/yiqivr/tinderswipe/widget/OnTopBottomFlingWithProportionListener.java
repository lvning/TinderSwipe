package com.yiqivr.tinderswipe.widget;

import android.view.View;

/**
 * @author lvning
 * @version create time:2014-9-25_上午10:20:00
 * @Description TODO
 */
public interface OnTopBottomFlingWithProportionListener extends OnTopBottomFlingListener {

	public void onFlingSuccessPercent(View selfView, int percent, boolean flingTop);
	
	public void onFlingResetOrigin(View selfView);
}
