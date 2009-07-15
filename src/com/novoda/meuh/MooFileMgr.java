package com.novoda.meuh;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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

	protected static final String	TAG				= "[MooFileMgr]:";
	private FileListingAdapter		fileListAdapter;
	private int						mChosenPosition	= 9999;
	private String					mNewFileName	= null;
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
		Log.i(TAG, "Name of file=" + mCurrFileName);
		boolean success = false;

		switch (item.getItemId()) {
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
				showDialog(R.layout.dialog_rename);
				break;

			case R.id.email:
				Log.i(TAG, "Emailing to kevin");
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("audio/3gpp");
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.email_subject_line));
				sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + Environment.getExternalStorageDirectory() + Constants.AUDIO_DIR + Constants.RECORDED_FILES_DIR + "/" + mCurrFileName));
				sendIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.email_text));
				startActivity(Intent.createChooser(sendIntent, this.getString(R.string.title_email)));
				break;
				
			case R.id.set_ringtone:
				String path = null;
				try {
					path = FileSys.createFilenameWithChecks(Environment.getExternalStorageDirectory() + Constants.RINGTONES_DIR, mCurrFileName);
					FileSys.copyViaChannels(new File(Environment.getExternalStorageDirectory() + Constants.AUDIO_DIR + Constants.RECORDED_FILES_DIR + "/" + mCurrFileName), new File(path));
				} catch (IOException e) {
					e.printStackTrace();
				}

				Uri newUri = addToMediaDB(new File(path));
				RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);
				Settings.System.putString(this.getContentResolver(), Settings.System.RINGTONE, newUri.toString());

		}

		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
			case R.layout.dialog_rename:
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

	private Uri addToMediaDB(File file) {
		ContentValues cv = new ContentValues();
		long current = System.currentTimeMillis();
		long modDate = file.lastModified();

		String title = "la-sound";

		// Lets label the recorded audio file as NON-MUSIC so that the file
		// won't be displayed automatically, except for in theplaylist.
		cv.put(MediaStore.Audio.Media.IS_MUSIC, "0");
		cv.put(MediaStore.Audio.Media.TITLE, title);
		Log.d(MediaStore.Audio.Media.TRACK.toString(), MediaStore.Audio.Media.TRACK.toString());
		cv.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
		cv.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
		cv.put(MediaStore.Audio.Media.DATE_MODIFIED, (int) (modDate / 1000));
		// cv.put(MediaStore.Audio.MediaColumns.DURATION,mRecordingLength);
		cv.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
		cv.put(MediaStore.Audio.Media.ARTIST, "kevin");
		cv.put(MediaStore.Audio.Media.ALBUM, "kevin");
		Log.d(TAG, "Inserting audio record: " + cv.toString());
		ContentResolver resolver = getContentResolver();
		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Log.d(TAG, "ContentURI: " + base);
		Uri result = resolver.insert(base, cv);
		Log.e(TAG, result.toString());

		if (result == null) {
			Log.i(TAG, "intent is null");
		}

		// Notify those applications such as Music listening to the
		// scanner events that a recorded audio file just created.
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
		return result;
	}

}
