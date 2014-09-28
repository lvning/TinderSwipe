package com.yiqivr.tinderswipe;

import java.util.ArrayList;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yiqivr.tinderswipe.widget.CircleProgress;
import com.yiqivr.tinderswipe.widget.FlingCardListener.SWIPEMODE;
import com.yiqivr.tinderswipe.widget.OnLeftRightFlingListener;
import com.yiqivr.tinderswipe.widget.OnTopBottomFlingWithProportionListener;
import com.yiqivr.tinderswipe.widget.SwipeFlingAdapterView;

public class MyActivity extends Activity implements OnLeftRightFlingListener, OnTopBottomFlingWithProportionListener {

	private ArrayList<String> al;
	private MyAdapter adapter;
	private TextView boxPic;

	private boolean addMoreExcuting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);

		SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
		boxPic = (TextView) findViewById(R.id.boxpic);

		al = new ArrayList<String>();
		for (int i = 1; i < 10; i++) {
			al.add(i + "");
		}

		flingContainer.setSwipeMode(SWIPEMODE.UP_DOWN);
		flingContainer.setMaxVisible(2);
		flingContainer.setTopBottomFlingListener(this);
//		flingContainer.setLeftRightFlingListener(this);
		adapter = new MyAdapter();
		flingContainer.setAdapter(adapter);
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
