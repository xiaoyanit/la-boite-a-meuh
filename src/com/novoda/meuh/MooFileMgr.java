package com.novoda.meuh;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

	private static final String	TAG				= "[MooFileMgr]:";
	private FileListingAdapter	mFileListAdapter;
	private int					mChosenPosition	= 9999;
	private String				mNewFileName	= null;
	private static String		sCurrFileName;

	public static String		RINGTONES_DIR;
	public static String		AUDIO_FILES_DIR;
	public static String		TMP_AUDIO_DIR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiomgr);

		AUDIO_FILES_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_created_sounds);
		RINGTONES_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_created_ringtones);
		TMP_AUDIO_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_tmp);

		initFileListing();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		sCurrFileName = null;
		sCurrFileName = mFileListAdapter.files.get(mChosenPosition).getName();
		Log.i(TAG, "Name of file=" + sCurrFileName);
		boolean success = false;

		switch (item.getItemId()) {
			case R.id.delete:
				Log.i(TAG, "Deleting the file =" + sCurrFileName);
				success = (new File(AUDIO_FILES_DIR + sCurrFileName)).delete();

				if (success) {
					Log.i(TAG, "File was renamed");
					refreshFileListAdapter();
				} else {
					Log.i(TAG, "File was not renamed");
				}
				break;

			case R.id.rename:
				Log.i(TAG, "We are renaming the file :" + sCurrFileName);
				showDialog(R.layout.dialog_rename);
				break;

			case R.id.email:
				Log.i(TAG, "Emailing to kevin");
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("audio/3gpp");
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.email_subject_line));
				sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + AUDIO_FILES_DIR + sCurrFileName));
				sendIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.email_text));
				startActivity(Intent.createChooser(sendIntent, this.getString(R.string.title_email)));
				break;

			case R.id.set_ringtone:
				String path = null;
				path = FileSys.createFilenameWithChecks(RINGTONES_DIR, sCurrFileName);
				FileSys.copyViaChannels(new File(AUDIO_FILES_DIR + sCurrFileName), new File(path));

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

				contents.getEditableText().append(FileSys.getFilenameWithoutExtension(sCurrFileName));

				return new AlertDialog.Builder(MooFileMgr.this).setIcon(R.drawable.alert_dialog_icon).setTitle(R.string.title_rename_sound_file).setView(textEntryView)
						.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								EditText contents = (EditText) textEntryView.findViewById(R.id.username_edit);
								Editable editable = contents.getEditableText();
								mNewFileName = null;
								mNewFileName = editable.toString();

								if (mNewFileName != null) {
									if (mFileListAdapter.files.get(mChosenPosition).renameTo(
											new File(AUDIO_FILES_DIR + mNewFileName + FileSys.getExtensionFromFilename(sCurrFileName)))) {
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
		mFileListAdapter = new FileListingAdapter(this, FileSys.listFilesInDir(AUDIO_FILES_DIR));
		this.setListAdapter(mFileListAdapter);
	}

	private Uri addToMediaDB(File file) {
		ContentValues cv = new ContentValues();
		long current = System.currentTimeMillis();
		long modDate = file.lastModified();

		String title = file.getName();

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
