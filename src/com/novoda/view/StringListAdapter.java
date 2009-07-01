package com.novoda.view;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class StringListAdapter extends BaseAdapter {

	private ArrayList<String>	strings;
	private final Activity	activity;
	
	public StringListAdapter(Activity activity, ArrayList<String> strings) {
		this.activity = activity;
		this.strings = strings;
	}
	
	public int getCount() {
		return this.strings.size();
	}

	public Object getItem(int position) {
		return this.strings.get(position);
	}

	public long getItemId(int id) {
		return this.strings.hashCode();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		StringListItemView todayView = new StringListItemView(activity, strings.get(position));
		return todayView;
	}

}
