package com.novoda.meuh;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.novoda.os.FileSys;
import com.novoda.view.FileListingAdapter;

public class MooFileMgr extends ListActivity {

	protected static final String	TAG					= "[MooFileMgr]:";
	private static final int		RENAME_FILE_DIALOG	= 22;
	private FileListingAdapter		fileListAdapter;
	private int						mChosenPosition		= 9999;
	private String					mNewFileName		= null;
	private String					mCurrFileName;

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
		        MenuInflater inflater = getMenuInflater();
		        inflater.inflate(R.menu.file_actions, menu);
		        menu.setHeaderTitle(R.string.title_ammend_files);
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

				Intent intent = new Intent();
				intent.putExtra(Constants.PICKED_AUDIO_FILE_POSITION, position);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void refreshFileListAdapter() {
		fileListAdapter = new FileListingAdapter(this, FileSys.listFilesInDir_asFiles(Constants.AUDIO_FILES_DIR));
		this.setListAdapter(fileListAdapter);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		mCurrFileName = null;
		mCurrFileName = fileListAdapter.files.get(mChosenPosition).getName();
		boolean success = false;

		
		switch(item.getItemId()){
			case R.id.delete:
				Log.i(TAG, "Deleting the file =" + mCurrFileName);
				success = (new File(Constants.AUDIO_FILES_DIR + mCurrFileName)).delete();
				
				if (success) {
					Log.i(TAG, "File was renamed");
					refreshFileListAdapter();
				} else {
					Log.i(TAG, "File was not renamed");
				}
				break;
				
			case R.id.rename:
				Log.i(TAG, "We are renaming the file :" + mCurrFileName);
				showDialog(RENAME_FILE_DIALOG);
				break;
				
		}
		

		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
			case RENAME_FILE_DIALOG:
				LayoutInflater factory = LayoutInflater.from(this);
				final View textEntryView = factory.inflate(R.layout.dialog_rename, null);
				EditText contents = (EditText) textEntryView.findViewById(R.id.username_edit);

				contents.getEditableText().append(FileSys.getFilenameWithoutExtension(mCurrFileName));

				return new AlertDialog.Builder(MooFileMgr.this).setIcon(R.drawable.alert_dialog_icon).setTitle(R.string.title_rename_sound_file).setView(textEntryView)
						.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								EditText contents = (EditText) textEntryView.findViewById(R.id.username_edit);
								Editable editable = contents.getEditableText();
								mNewFileName = null;
								mNewFileName = editable.toString();

								if (mNewFileName != null) {
									if (fileListAdapter.files.get(mChosenPosition).renameTo(
											new File(Constants.AUDIO_FILES_DIR + mNewFileName + FileSys.getExtensionFromFilename(mCurrFileName)))) {
										Log.i(TAG, "Filename is: " + mNewFileName);
										Log.i(TAG, "File was renamed");
										refreshFileListAdapter();
									} else {
										Log.i(TAG, "File was not renamed");
									}
								}

							}
						}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								mNewFileName = null;
							}
						}).create();
		}

		return null;

	}

}
