package com.novoda.view;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StringListItemView extends LinearLayout {

	private TextView	mFileName;

	public StringListItemView(Activity activity, String item) {
		super(activity);

		mFileName = new TextView(activity);
		mFileName.setText(item);

		this.setOrientation(VERTICAL);
		this.setPadding(0, 8, 0, 12);

		addView(mFileName, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

	}

}
