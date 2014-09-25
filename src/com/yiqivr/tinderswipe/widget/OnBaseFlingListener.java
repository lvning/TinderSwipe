package com.yiqivr.tinderswipe.widget;

public interface OnBaseFlingListener {
	void removeFirstObjectInAdapter();

	void onAdapterAboutToEmpty(int itemsInAdapter);
}
