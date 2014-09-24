package com.yiqivr.tinderswipe;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yiqivr.tinderswipe.widget.FlingCardListener.SWIPEMODE;
import com.yiqivr.tinderswipe.widget.OnLeftRightFlingListener;
import com.yiqivr.tinderswipe.widget.OnTopBottomFlingListener;
import com.yiqivr.tinderswipe.widget.SwipeFlingAdapterView;

public class MyActivity extends Activity implements OnLeftRightFlingListener, OnTopBottomFlingListener {

	private ArrayList<String> al;
//	private ArrayAdapter<String> arrayAdapter;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);

		SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

		al = new ArrayList<String>();
		al.add("1");
		al.add("2");
		al.add("3");
		al.add("4");
		al.add("5");
		al.add("6");
		al.add("7");
		al.add("8");
//
//		arrayAdapter = new ArrayAdapter<String>(this, R.layout.item, R.id.helloText, al);

		flingContainer.setSwipeMode(SWIPEMODE.UP_DOWN);
		flingContainer.setTopBottomFlingListener(this);
		flingContainer.setLeftRightFlingListener(this);
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
	}

	@Override
	public void onTopCardExit(Object dataObject) {
		Toast.makeText(this, "Swipe Top:" + dataObject.toString(), Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onBottomCardExit(Object dataObject) {
		Toast.makeText(this, "Swipe Bottom:" + dataObject.toString(), Toast.LENGTH_SHORT).show();
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
			View v = LayoutInflater.from(MyActivity.this).inflate(R.layout.item, null);
			TextView tv = (TextView) v.findViewById(R.id.helloText);
			tv.setBackgroundResource(Integer.valueOf(al.get(position)) % 2 == 0 ? R.drawable.card : R.drawable.card2);
			tv.setText(al.get(position));
			return v;
		}

	}

}
