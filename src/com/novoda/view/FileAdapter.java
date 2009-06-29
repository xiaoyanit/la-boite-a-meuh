package com.novoda.view;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FileAdapter extends BaseAdapter {

	private ArrayList<File>	files;
	private final Activity	activity;

	public FileAdapter(Activity activity, ArrayList<File> allRoutesAroundCurrentArea) {
		this.activity = activity;
		this.files = allRoutesAroundCurrentArea;
	}

	public int getCount() {
		return this.files.size();
	}

	public File getItem(int position) {
		return this.files.get(position);
	}

	public long getItemId(int position) {
		return this.files.hashCode();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		FileItemView todayView = new FileItemView(activity, files.get(position));
		return todayView;
	}

}
