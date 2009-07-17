package com.novoda.view;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.meuh.R;
import com.novoda.os.FileSys;

public class AudioFileListAdapter extends BaseAdapter {

	public ArrayList<File>	files;
	private final Activity	activity;

	public AudioFileListAdapter(Activity activity, ArrayList<File> files) {
		this.activity = activity;
		this.files = files;
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
		AudioFileListAdapterView todayView = new AudioFileListAdapterView(activity, files.get(position));
		return todayView;
	}
	
	
	public class AudioFileListAdapterView extends LinearLayout {

		private TextView	mFileName;

		public AudioFileListAdapterView(Activity activity, File fileItem) {
			super(activity);
			this.setOrientation(HORIZONTAL);
			this.setPadding(0, 8, 0, 12);

			ImageView songIcon = new ImageView(activity);
			songIcon.setImageResource(R.drawable.ic_song);
			addView(songIcon, new LinearLayout.LayoutParams(48, LayoutParams.WRAP_CONTENT));
			
						
			mFileName = new TextView(activity);
			mFileName.setText(FileSys.getFilenameWithoutExtension(fileItem.getName()));
			addView(mFileName, new LinearLayout.LayoutParams(100, LayoutParams.WRAP_CONTENT));

		}

	}

}
