package com.yiqivr.tinderswipe;

import java.util.ArrayList;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yiqivr.tinderswipe.widget.CircleProgress;
import com.yiqivr.tinderswipe.widget.FlingCardListener.SWIPEMODE;
import com.yiqivr.tinderswipe.widget.MultiViewPager;
import com.yiqivr.tinderswipe.widget.OnLeftRightFlingListener;
import com.yiqivr.tinderswipe.widget.OnTopBottomFlingWithProportionListener;
import com.yiqivr.tinderswipe.widget.SwipeFlingAdapterView;

public class MyActivity extends Activity implements OnLeftRightFlingListener, OnTopBottomFlingWithProportionListener {

	private ArrayList<String> al;
	private ArrayList<View> pagerView;
	private MyAdapter adapter;
	private MyPagerAdapter pagerAdapter;
	private TextView boxPic;

	private boolean addMoreExcuting;

	private MultiViewPager pager;
	private View out1, out2, shadow;
	private final int duration = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);

		SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
		boxPic = (TextView) findViewById(R.id.boxpic);
		pager = (MultiViewPager) findViewById(R.id.viewpager);
		out1 = findViewById(R.id.boxpic_out1);
		out2 = findViewById(R.id.boxpic_out2);
		shadow = findViewById(R.id.shadow);

		al = new ArrayList<String>();
		pagerView = new ArrayList<View>();
		for (int i = 1; i < 10; i++) {
			al.add(i + "");
			View v = LayoutInflater.from(MyActivity.this).inflate(R.layout.item, null);
			v.findViewById(R.id.bottom_progress).setVisibility(View.GONE);
			v.findViewById(R.id.top_progress).setVisibility(View.GONE);
			pagerView.add(v);
		}

		flingContainer.setSwipeMode(SWIPEMODE.UP_DOWN);
		flingContainer.setMaxVisible(2);
		flingContainer.setTopBottomFlingListener(this);
//		flingContainer.setLeftRightFlingListener(this);
		adapter = new MyAdapter();
		flingContainer.setAdapter(adapter);

		pager.setVisibility(View.GONE);
		out1.setAlpha(0);
		out2.setAlpha(0);

		findViewById(R.id.collect_box).setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {

				pager.setAlpha(0);
				shadow.setAlpha(0);
				shadow.setVisibility(View.VISIBLE);
				pager.setVisibility(View.VISIBLE);
				pagerAdapter = new MyPagerAdapter();
				pager.setPageMargin(-300);
				pager.setAdapter(pagerAdapter);
				pager.post(new Runnable() {

					@Override
					public void run() {
						out1.setAlpha(1);
						out2.setAlpha(1);
						out1.clearAnimation();
						out2.clearAnimation();
						int transByY = pager.getTop() - out1.getTop();
						final int bransByX = pager.getChildAt(1).getLeft() - pager.getChildAt(0).getLeft();
						out1.animate().translationY(transByY).setDuration(duration);
						shadow.animate().alpha(1).setDuration(2 * duration);
						out2.animate().translationY(transByY).setDuration(duration).withEndAction(new Runnable() {

							@Override
							public void run() {
								out2.animate().translationX(bransByX).setDuration(duration)
										.withEndAction(new Runnable() {

											@Override
											public void run() {
												pager.setAlpha(1);
												shadow.setAlpha(1);
												out1.setAlpha(0);
												out2.setAlpha(0);
											}
										});
							}
						});
					}
				});
			}
		});

		shadow.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				Log.v("", "shadow clicked!!!");
				shadow.animate().alpha(0).setDuration(2 * duration).withEndAction(new Runnable() {

					@Override
					public void run() {
						shadow.setVisibility(View.GONE);
					}
				});
				Log.e("", "pager.getChildCount() = " + pager.getChildCount());
				
			}
		});
		
	}

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
//		Toast.makeText(this, "Swipe Top:" + dataObject.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBottomCardExit(Object dataObject) {
//		Toast.makeText(this, "Swipe Bottom:" + dataObject.toString(), Toast.LENGTH_SHORT).show();
		boxPic.setBackgroundResource(Integer.valueOf(dataObject.toString()) % 2 == 0 ? R.drawable.card
				: R.drawable.card2);
		boxPic.setText(dataObject.toString());

		ObjectAnimator transAnim = ObjectAnimator.ofFloat(boxPic, "translationY", 150f, 0f);
		transAnim.setInterpolator(new OvershootInterpolator());
		transAnim.setDuration(250);
		ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(boxPic, "rotation", 0f, -5f);
		rotateAnim.setInterpolator(new OvershootInterpolator());
		rotateAnim.setDuration(150);
		AnimatorSet animSet = new AnimatorSet();
		animSet.playTogether(rotateAnim, transAnim);
		animSet.start();
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

	private Animation getAnim(boolean isFirst) {
		int duration = 2000;
		Animation animation;
		if (isFirst) {
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(duration);
		} else {
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setStartOffset(duration);
			animation.setDuration(duration);
		}

		return animation;
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
