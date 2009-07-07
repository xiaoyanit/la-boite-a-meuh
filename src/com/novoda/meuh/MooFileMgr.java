package com.novoda.meuh;

import java.io.File;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.novoda.meuh.media.SoundPoolMgr;
import com.novoda.os.FileSys;
import com.novoda.view.FileListingAdapter;

public class MooFileMgr extends ListActivity {

	private static final int	OPT_EMAIL_FRIEND	= 3;
	private static final int	OPT_DELETE_FILE	= 2;
	private static final int	OPT_RENAME_FILE	= 1;
	protected static final String	TAG				= "[MooFileMgr]:";
	private FileListingAdapter		fileListAdapter;
	private int						mChosenPosition	= 9999;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiomgr);
		initFileListing();
	}

	private void initFileListing() {
		ListView lv = getListView();
		refreshFileListAdapter();

		lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("Edit sound file");
				menu.add(0, OPT_RENAME_FILE, 0, "Rename");
				menu.add(0, OPT_DELETE_FILE, 0, "Delete");
				menu.add(0, OPT_EMAIL_FRIEND, 0, "Email to friend");
				
			}

		});

		lv.setLongClickable(true);
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				Log.i(TAG, "long click");
				mChosenPosition = pos;
				getListView().showContextMenu();
				return true;
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "Item clicked");
				Log.i(TAG, "Item position" + position);
				Log.i(TAG, "Item id" + id);
				

			}
		});
	}

	private void refreshFileListAdapter() {
		fileListAdapter = new FileListingAdapter(this, FileSys.listFilesInDir_asFiles(Constants.AUDIO_FILES_DIR));
		this.setListAdapter(fileListAdapter);
	}
	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(TAG, "We are renaming the file =" + item);
		Log.i(TAG, "We are renaming the file id =" + item.getItemId());
		Log.i(TAG, "We are renaming the file id =" + item.getMenuInfo());
		Log.i(TAG, "We are renaming the file at position =" + mChosenPosition);
		String fileName = fileListAdapter.files.get(mChosenPosition).getName();
		Log.i(TAG, "We are renaming the file name at position =" + fileName);
		
		
		
		if (item.getItemId() == OPT_DELETE_FILE) {
		   boolean success = (new File(Constants.AUDIO_FILES_DIR + "/" +fileName)).delete();
		    if (!success) {
				Log.i(TAG, "failed to delete");
		    }else{
		    	Log.i(TAG, "Deleted file =" + fileName);
		    }
			
		}
		
		refreshFileListAdapter();
		return true;
	}

}
