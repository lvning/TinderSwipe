package com.yiqivr.tinderswipe;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yiqivr.tinderswipe.widget.BoxLayout;
import com.yiqivr.tinderswipe.widget.CircleProgress;
import com.yiqivr.tinderswipe.widget.FlingCardListener.SWIPEMODE;
import com.yiqivr.tinderswipe.widget.OnLeftRightFlingListener;
import com.yiqivr.tinderswipe.widget.OnTopBottomFlingWithProportionListener;
import com.yiqivr.tinderswipe.widget.SwipeFlingAdapterView;

public class MyActivity extends Activity implements OnLeftRightFlingListener, OnTopBottomFlingWithProportionListener {

	private ArrayList<String> al;
	private ArrayList<View> pagerView;
	private MyAdapter adapter;
	private MyPagerAdapter pagerAdapter;
	private BoxLayout boxLayout;

	private boolean addMoreExcuting;

	private ViewPager pager;
	private View shadow;
	private final int duration = 500;
	private final int OutInduration = 500;

	private float transByY, transByX;
	private View box;
	private SwipeFlingAdapterView flingContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);

		flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
		boxLayout = (BoxLayout) findViewById(R.id.boxlayout);
		pager = (ViewPager) findViewById(R.id.viewpager);
		shadow = findViewById(R.id.shadow);
		box = findViewById(R.id.collect_box);

		al = new ArrayList<String>();
		pagerView = new ArrayList<View>();
		for (int i = 1; i < 10; i++) {
			al.add(i + "");
		}

		flingContainer.setSwipeMode(SWIPEMODE.UP_DOWN);
		flingContainer.setMaxVisible(2);
		flingContainer.setTopBottomFlingListener(this);
//		flingContainer.setLeftRightFlingListener(this);
		adapter = new MyAdapter();
		flingContainer.setAdapter(adapter);

		pager.setVisibility(View.GONE);

		box.setOnClickListener(boxClickListener);

		shadow.setOnClickListener(shadowClickListener);

	}

	private OnClickListener boxClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (pagerView.size() == 0) {
				return;
			}
			box.setOnClickListener(null);
			pager.setAlpha(0);
			shadow.setAlpha(0);
			shadow.setVisibility(View.VISIBLE);
			pager.setVisibility(View.VISIBLE);
			pagerAdapter = new MyPagerAdapter();
			pager.setPageMargin(-300);
			pager.setAdapter(pagerAdapter);

			boxLayout.bringToFront();
			box.bringToFront();

			pager.post(new Runnable() {

				@Override
				public void run() {
					transByY = pager.getTop() - boxLayout.getTop() - boxLayout.getChildTop();
					int collectItemsSize = pagerView.size();
					int animType = BoxLayout.ONE_COLLECT_ANIM;
					switch (collectItemsSize) {
					case 1:
						animType = BoxLayout.ONE_COLLECT_ANIM;
						break;
					case 2:
						animType = BoxLayout.TWO_COLLECT_ANIM;
						transByX = pager.getChildAt(1).getLeft() - pager.getChildAt(0).getLeft();
						break;
					default:
						animType = BoxLayout.MORE_COLLECT_ANIM;
						transByX = pager.getChildAt(1).getLeft() - pager.getChildAt(0).getLeft();
						break;
					}

					boxLayout.popOut(animType, transByY, transByX, new Runnable() {

						@Override
						public void run() {
							flingContainer.bringToFront();
							shadow.bringToFront();
							pager.setAlpha(1);
							pager.bringToFront();
							box.bringToFront();
							findViewById(R.id.rl_content).requestLayout();
							shadow.setOnClickListener(shadowClickListener);
							pager.setOnClickListener(shadowClickListener);
						}
					});

					shadow.animate().alpha(1).setDuration(2 * OutInduration);
				}
			});

		}
	};

	private OnClickListener shadowClickListener = new View.OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			shadow.setOnClickListener(null);
			shadow.animate().alpha(0).setDuration(2 * duration).withEndAction(new Runnable() {

				@Override
				public void run() {
					shadow.setVisibility(View.GONE);
				}
			});
			Log.e("", "pager.getChildCount() = " + pager.getChildCount());

			int animType = BoxLayout.MORE_PAGER_ANIM_LEFT;
			int pagerChildCount = pager.getChildCount();
			pager.setVisibility(View.GONE);
			shadow.bringToFront();
			boxLayout.bringToFront();
			box.bringToFront();
			switch (pagerChildCount) {
			case 1:
				animType = BoxLayout.ONE_PAGER_ANIM;
				break;
			case 2:
				boolean leftCenter = (pager.getCurrentItem() == 0);
				if (pagerView.size() > 2) {
					animType = leftCenter ? BoxLayout.MORE_PAGER_ANIM_LEFT : BoxLayout.MORE_PAGER_ANIM_RIGHT;
				} else {
					animType = leftCenter ? BoxLayout.TWO_PAGER_ANIM_LEFT : BoxLayout.TWO_PAGER_ANIM_RIGHT;
				}
				break;
			default:
				animType = BoxLayout.MORE_PAGER_ANIM_OTHER;
				break;
			}
			boxLayout.setReadyPullIn(animType, transByY, transByX, new Runnable() {

				@Override
				public void run() {
					boxLayout.bringToFront();
					flingContainer.bringToFront();
					shadow.bringToFront();
					box.bringToFront();
					findViewById(R.id.rl_content).requestLayout();
					box.setOnClickListener(boxClickListener);
				}
			});

		}

	};

	@Override
	public void removeFirstObjectInAdapter() {
		Log.d("LIST", "removed object!");
		al.remove(0);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLeftCardExit(Object dataObject) {
		Toast.makeText(this, "Left!" + dataObject.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRightCardExit(Object dataObject) {
		Toast.makeText(this, "Right!" + dataObject.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAdapterAboutToEmpty(int itemsInAdapter) {
		// Ask for more data here
		System.out.println("Almost empty!!! itemsInAdapter = " + itemsInAdapter);
		netWorkSimulation();
	}

	private void netWorkSimulation() {
		if (addMoreExcuting)
			return;
		addMoreExcuting = true;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				ArrayList<String> moreData = new ArrayList<String>();
				for (int i = 10; i < 20; i++) {
					moreData.add(i + "");
				}

				al.addAll(moreData);
				adapter.notifyDataSetChanged();
				addMoreExcuting = false;
			}
		}, 1000);
	}

	@Override
	public void onTopCardExit(Object dataObject) {
	}

	@Override
	public void onBottomCardExit(Object dataObject) {
		if (pagerView.size() == 0) {
			boxLayout
					.setBackImage(Integer.valueOf(dataObject.toString()) % 2 == 0 ? R.drawable.card : R.drawable.card2);
		} else if (pagerView.size() == 1) {
			boxLayout.setCenterImage(Integer.valueOf(dataObject.toString()) % 2 == 0 ? R.drawable.card
					: R.drawable.card2);
		} else {
			boxLayout
					.setForeImage(Integer.valueOf(dataObject.toString()) % 2 == 0 ? R.drawable.card : R.drawable.card2);
		}
		View v = LayoutInflater.from(MyActivity.this).inflate(R.layout.no_circle_item, null);
		TextView tv = (TextView) v.findViewById(R.id.helloText);
		tv.setText(dataObject.toString());
		pagerView.add(v);
		if (pagerAdapter != null)
			pagerAdapter.notifyDataSetChanged();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return al.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return al.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v("", "getView--------");
			View v = LayoutInflater.from(MyActivity.this).inflate(R.layout.item, null);
			TextView tv = (TextView) v.findViewById(R.id.helloText);
			tv.setBackgroundResource(Integer.valueOf(al.get(position)) % 2 == 0 ? R.drawable.card : R.drawable.card2);
			tv.setText(al.get(position));
			CircleProgress top = (CircleProgress) v.findViewById(R.id.top_progress);
			CircleProgress bottom = (CircleProgress) v.findViewById(R.id.bottom_progress);
			top.setTag(0);
			bottom.setTag(0);
			top.setAlpha(0);
			bottom.setAlpha(0);
			return v;
		}

	}

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pagerView.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(pagerView.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(pagerView.get(position));
			return pagerView.get(position);
		}

	}

	@Override
	public void onFlingSuccessPercent(View selfView, int percent, boolean flingTop) {
		CircleProgress top = (CircleProgress) selfView.findViewById(R.id.top_progress);
		CircleProgress bottom = (CircleProgress) selfView.findViewById(R.id.bottom_progress);
		if (top != null && bottom != null) {
			if (flingTop) {
				if ((Integer) top.getTag() == 0) {
					top.setTag(1);
					top.animate().alpha(1).setDuration(500l);
				}
				if ((Integer) bottom.getTag() == 1) {
					bottom.setTag(0);
					bottom.setCurProgress(0);
					bottom.animate().alpha(0).setDuration(500l);
				}
				top.setCurProgress(percent);
			} else {
				if ((Integer) top.getTag() == 1) {
					top.setTag(0);
					top.setCurProgress(0);
					top.animate().alpha(0).setDuration(500l);
				}
				if ((Integer) bottom.getTag() == 0) {
					bottom.setTag(1);
					bottom.animate().alpha(1).setDuration(500l);
				}
				bottom.setCurProgress(percent);
			}
		}

	}

	@Override
	public void onFlingResetOrigin(View selfView) {
		CircleProgress top = (CircleProgress) selfView.findViewById(R.id.top_progress);
		CircleProgress bottom = (CircleProgress) selfView.findViewById(R.id.bottom_progress);
		if ((Integer) top.getTag() == 1) {
			top.setTag(0);
			top.animate().alpha(0).setDuration(500l);
		}
		if ((Integer) bottom.getTag() == 1) {
			bottom.setTag(0);
			bottom.animate().alpha(0).setDuration(500l);
		}
	}

}
