package com.novoda.meuh;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.novoda.os.FileSys;
import com.novoda.view.FileListingAdapter;

public class MooFileMgr extends ListActivity {

	protected static final String	TAG	= "[MooFileMgr]:";
	private FileListingAdapter		fileListAdapter;

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

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {

				Intent intent = getIntent();
				intent.setAction(Constants.PICK_SOUND);
				intent.putExtra(Constants.PICKED_AUDIO_FILE_POSITION, position);
				setResult(RESULT_OK, intent);
				MooFileMgr.this.finish();
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

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, data.toURI());
		Intent intent = getIntent();
		Log.i(TAG, "request code from recording =" + requestCode);
		Log.i(TAG, "result code from recording =" + resultCode);
	}

}
