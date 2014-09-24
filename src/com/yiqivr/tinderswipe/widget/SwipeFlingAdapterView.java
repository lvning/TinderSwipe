package com.yiqivr.tinderswipe.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.yiqivr.tinderswipe.widget.FlingCardListener.SWIPEMODE;

public class SwipeFlingAdapterView extends BaseFlingAdapterView implements HelperFlingListener {

	private Adapter mAdapter;
	private int maxVisible = 2;
	private int minAdapterStack = 6;
	private int lastObjectInStack = 0;
	private OnLeftRightFlingListener lrFlingListener;
	private OnTopBottomFlingListener tbFlingListener;
	private SWIPEMODE swipeMode = SWIPEMODE.LEFT_RIGHT;

	public SwipeFlingAdapterView(Context context) {
		super(context);
	}

	public SwipeFlingAdapterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SwipeFlingAdapterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private final DataSetObserver mDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			observedChange();
		}

		@Override
		public void onInvalidated() {
			observedChange();
		}

		private void observedChange() {

			final int adapterCount = mAdapter.getCount();

			if (adapterCount <= minAdapterStack) {
				if (swipeMode == SWIPEMODE.LEFT_RIGHT) {
					if (lrFlingListener != null)
						lrFlingListener.onAdapterAboutToEmpty(adapterCount);
				} else {
					if (tbFlingListener != null)
						tbFlingListener.onAdapterAboutToEmpty(adapterCount);
				}
			}

			if (adapterCount == 0) {
				removeAllViewsInLayout();
			} else if (adapterCount == 1) {
				removeAllViewsInLayout();
				layoutChildren(0);
				setTopView();
			} else if (adapterCount <= maxVisible) {
				try {
					removeViewsInLayout(0, lastObjectInStack);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				layoutChildren(1);
			}
		}
	};

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		this.mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataSetObserver);
	}

	@Override
	public void onCardExited() {
		if (swipeMode == SWIPEMODE.LEFT_RIGHT) {
			if (lrFlingListener != null)
				lrFlingListener.removeFirstObjectInAdapter();
		} else {
			if (tbFlingListener != null)
				tbFlingListener.removeFirstObjectInAdapter();
		}
		removeAllViewsInLayout();
		layoutChildren(0);
		setTopView();
	}

	@Override
	public void leftExit(Object dataObject) {
		if (lrFlingListener != null) {
			lrFlingListener.onLeftCardExit(dataObject);
		}
	}

	@Override
	public void rightExit(Object dataObject) {
		if (lrFlingListener != null) {
			lrFlingListener.onRightCardExit(dataObject);
		}
	}

	@Override
	public void topExit(Object dataObject) {
		if (tbFlingListener != null) {
			tbFlingListener.onTopCardExit(dataObject);
		}
	}

	@Override
	public void bottomExit(Object dataObject) {
		if (tbFlingListener != null) {
			tbFlingListener.onBottomCardExit(dataObject);
		}
	}

	public void setSwipeMode(SWIPEMODE swipeMode) {
		this.swipeMode = swipeMode;
	}

	public void setLeftRightFlingListener(OnLeftRightFlingListener onFlingListener) {
		this.lrFlingListener = onFlingListener;
	}

	public void setTopBottomFlingListener(OnTopBottomFlingListener tbFlingListener) {
		this.tbFlingListener = tbFlingListener;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mAdapter == null) {
			return;
		}
		removeAllViewsInLayout();
		layoutChildren(0);
		setTopView();
	}

	private void layoutChildren(final int index) {
		int position = index;
		while (position < mAdapter.getCount() && position < maxVisible) {
			View newBottomChild = mAdapter.getView(position, null, this);
			if (newBottomChild.getVisibility() != GONE) {
				addAndMeasureChild(newBottomChild);
				lastObjectInStack = position;
			}
			position++;
		}

		positionItems(index);
	}

	private void addAndMeasureChild(View child) {

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
		if (lp == null)
			lp = new FrameLayout.LayoutParams(android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
					android.widget.FrameLayout.LayoutParams.MATCH_PARENT);

		addViewInLayout(child, 0, lp, true);

		int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(), getPaddingLeft() + getPaddingRight()
				+ lp.leftMargin + lp.rightMargin, lp.width);

		int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(), getPaddingTop() + getPaddingBottom()
				+ lp.topMargin + lp.bottomMargin, lp.height);

		child.measure(childWidthSpec, childHeightSpec);
	}

	private void positionItems(int firstIteration) {
		for (int index = firstIteration; index < getChildCount(); index++) {
			View child = getChildAt(index);

			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();

			int childLeft;
			int childTop;

			int gravity = lp.gravity;
			if (gravity == -1) {
				gravity = Gravity.TOP | Gravity.START;
			}

			final int layoutDirection = getLayoutDirection();
			final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
			final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

			switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.CENTER_HORIZONTAL:
				childLeft = (getWidth() + getPaddingLeft() - getPaddingRight() - width) / 2 + lp.leftMargin
						- lp.rightMargin;
				break;
			case Gravity.RIGHT:
				childLeft = getWidth() + getPaddingRight() - width - lp.rightMargin;
				break;
			case Gravity.LEFT:
			default:
				childLeft = getPaddingLeft() + lp.leftMargin;
				break;
			}

			switch (verticalGravity) {
			case Gravity.CENTER_VERTICAL:
				childTop = (getHeight() + getPaddingTop() - getPaddingBottom() - width) / 2 + lp.topMargin
						- lp.bottomMargin;
				break;
			case Gravity.BOTTOM:
				childTop = getHeight() - getPaddingBottom() - height - lp.bottomMargin;
				break;
			case Gravity.TOP:
			default:
				childTop = getPaddingTop() + lp.topMargin;
				break;
			}

			child.layout(childLeft, childTop, childLeft + width, childTop + height);
		}
	}

	private void setTopView() {
		if (getChildCount() > 0) {
			Log.d("", "lastObjectInStack = " + lastObjectInStack);
			View tv = getChildAt(lastObjectInStack);
			View nextTv = getChildAt(lastObjectInStack - 1);
			
			if (tv != null) {
				FlingCardListener flingCardListener = new FlingCardListener(swipeMode, tv, nextTv, getWidth(),
						getHeight(), tv.getX(), tv.getY(), tv.getHeight(), tv.getWidth(), mAdapter.getItem(0), this);
				tv.setOnTouchListener(flingCardListener);
			}
			
		}
	}

	public void setMaxVisible(int maxVisible) {
		this.maxVisible = maxVisible;
	}

	public void setMinStackInAdapter(int minAdapterStack) {
		this.minAdapterStack = minAdapterStack;
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new FrameLayout.LayoutParams(getContext(), attrs);
	}

}
