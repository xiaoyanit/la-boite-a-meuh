package com.novoda.view;

import java.io.File;

import com.novoda.os.FileSys;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileListItemView extends LinearLayout {

	private TextView	mFileName;

	public FileListItemView(Activity activity, File fileItem) {
		super(activity);

		mFileName = new TextView(activity);
		mFileName.setText(FileSys.getFilenameWithoutExtension(fileItem.getName()));

		this.setOrientation(VERTICAL);
		this.setPadding(0, 8, 0, 12);

		addView(mFileName, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

	}

}
