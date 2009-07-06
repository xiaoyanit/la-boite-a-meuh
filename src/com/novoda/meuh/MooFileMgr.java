package com.novoda.meuh;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.novoda.os.FileSys;
import com.novoda.view.FileListingAdapter;

public class MooFileMgr extends ListActivity {

	protected static final String	TAG				= "[MooFileMgr]:";
	private FileListingAdapter		fileListAdapter;
	private int						mChosenPosition	= 9999;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiomgr);
		ListView lv = getListView();

		fileListAdapter = new FileListingAdapter(this, FileSys.listFilesInDir_asFiles(Constants.AUDIO_FILES_DIR));
		this.setListAdapter(fileListAdapter);

		lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("Edit sound file");
				menu.add(0, 1, 0, "Rename");
				menu.add(0, 2, 0, "Delete");
				menu.add(0, 3, 0, "Email to friend");
				
			}

		});
		

		lv.setLongClickable(true);
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				Log.i(TAG, "long click");
				getListView().showContextMenu();
				return true;
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "Item clicked");
				Log.i(TAG, "Item position" + position);
				Log.i(TAG, "Item id" + id);
				
				mChosenPosition = position;
			}
		});
	}
	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    ContextMenuInfo menuInfo = (ContextMenuInfo) item.getMenuInfo(); 
		Log.i(TAG, "We are renaming the file =" + item);
		Log.i(TAG, "We are renaming the file id =" + item.getItemId());
		Log.i(TAG, "We are renaming the file id =" + item.getMenuInfo());
		Log.i(TAG, "We are renaming the file at position =" + mChosenPosition);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {

			if (data.getAction() == Constants.RENAME_FILE) {
				if (resultCode == RESULT_OK) {

				}
			}

		}

	}

}
