package com.novoda.moo.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.novoda.moo.demo.os.FileSys;
import com.novoda.moo.demo.view.AudioFileListAdapter;

public class FileMgr extends ListActivity {

	private static final String		TAG				= "[MooFileMgr]";
	private AudioFileListAdapter	mFileListAdapter;
	private int						mChosenPosition	= 9999;
	private String					mNewFileName	= null;
	private ListView				listView;
	private static String			sCurrFileName;

	public static String			RINGTONES_DIR;
	public static String			AUDIO_FILES_DIR;
	public static String			TMP_AUDIO_DIR;
	private static String			DEFAULT_AUDIO_DIR;
	private Button					banner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filemgr_demo);
		
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			Log.d(TAG, "SDCard installed - Checking for sound directories");
			AUDIO_FILES_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_created_sounds);
			RINGTONES_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_created_ringtones);
			TMP_AUDIO_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_tmp);
			DEFAULT_AUDIO_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_default_audio);

			new File(AUDIO_FILES_DIR).mkdirs();
			new File(RINGTONES_DIR).mkdirs();
			new File(TMP_AUDIO_DIR).mkdirs();
			new File(DEFAULT_AUDIO_DIR).mkdirs();

			initFileListing();
		} else {
			Log.i(TAG, "No SDCard installed, no sound files can be saved.");
			showDialog(R.layout.dialog_sdcard_warning);
		}

		banner = (Button) findViewById(R.id.mooAdvert);
		banner.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.novoda.com"));
				startActivity(viewIntent);
			}
		});

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		sCurrFileName = null;
		sCurrFileName = mFileListAdapter.files.get(mChosenPosition).getName();
		Log.i(TAG, "Name of file=" + sCurrFileName);
		boolean success = false;

		
		switch (item.getItemId()) {
			case R.id.delete:
				
				if(new File(DEFAULT_AUDIO_DIR + sCurrFileName).exists()){
					showDialog(R.layout.dialog_not_on_default_file);
				}else{
					Log.d(TAG, "Deleting file [" + sCurrFileName + "]");
					success = (new File(AUDIO_FILES_DIR + sCurrFileName)).delete();
					
					if (success) {
						// TODO: DELETE entry in Media DB via URI
						Log.i(TAG, "File [" + sCurrFileName + "] was deleted");
						refreshFileListAdapter();
					} else {
						Log.e(TAG, "File  [" + sCurrFileName + "] was not deleted");
					}
				}
				break;

			case R.id.rename:
				
				if(new File(DEFAULT_AUDIO_DIR + sCurrFileName).exists()){
					showDialog(R.layout.dialog_not_on_default_file);
				}else{
					Log.d(TAG, "Renaming file [" + sCurrFileName + "]");
					showDialog(R.layout.dialog_rename);
				}
				break;

			case R.id.email:
				Log.i(TAG, "Sending file[" + sCurrFileName + "] file as attachment in email");
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("audio/3gpp");
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.email_subject_line));
				sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + AUDIO_FILES_DIR + sCurrFileName));
				sendIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.email_text));
				startActivity(Intent.createChooser(sendIntent, this.getString(R.string.title_email)));
				break;

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

				return new AlertDialog.Builder(FileMgr.this).setIcon(R.drawable.alert_dialog_icon).setTitle(R.string.title_rename_sound_file).setView(textEntryView)
						.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								EditText contents = (EditText) textEntryView.findViewById(R.id.username_edit);
								Editable editable = contents.getEditableText();
								mNewFileName = null;
								mNewFileName = editable.toString();

								if (mNewFileName != null) {
									if (mFileListAdapter.files.get(mChosenPosition).renameTo(
											new File(AUDIO_FILES_DIR + mNewFileName + FileSys.getExtensionFromFilename(sCurrFileName)))) {
										Log.d(TAG, "Filename[" + sCurrFileName + "] was renamed to [" + mNewFileName + "] from dialog contents");
										// TODO: UPDATE name in Media DB via URI
										refreshFileListAdapter();
									} else {
										Log.e(TAG, "Filename[" + sCurrFileName + "] could not be renamed to [" + mNewFileName + "] from dialog contents");
									}
								}

							}
						}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								mNewFileName = null;
							}
						}).create();

			case R.layout.dialog_help:
				LayoutInflater factory2 = LayoutInflater.from(this);
				final View textEntryView2 = factory2.inflate(R.layout.dialog_help, null);

				return new AlertDialog.Builder(FileMgr.this).setIcon(android.R.drawable.ic_dialog_info).setTitle(R.string.title_help).setView(textEntryView2).setPositiveButton(
						R.string.dialog_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create();

			case R.layout.dialog_sdcard_warning:
				LayoutInflater factory3 = LayoutInflater.from(this);
				final View textEntryView3 = factory3.inflate(R.layout.dialog_sdcard_warning, null);

				return new AlertDialog.Builder(FileMgr.this).setIcon(android.R.drawable.ic_dialog_info).setTitle(R.string.title_sdcard_warning).setView(textEntryView3)
						.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						}).create();
				
			case R.layout.dialog_not_on_default_file:
				LayoutInflater factory4 = LayoutInflater.from(this);
				final View textEntryView4 = factory4.inflate(R.layout.dialog_not_on_default_file, null);

				return new AlertDialog.Builder(FileMgr.this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(textEntryView4)
					.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					}).create();
		}

		return null;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.file_mgr_actions, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.help:
				Log.v(TAG, "User selected Help");
				showDialog(R.layout.dialog_help);
				return true;

		}
		return false;
	}

	private void initFileListing() {
		listView = getListView();
		refreshFileListAdapter();

		listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.file_mgr_file_actions, menu);
				menu.setHeaderTitle(R.string.title_ammend_file);
			}
		});

		listView.setLongClickable(true);
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				Log.v(TAG, "List item[" + pos + "] has been long clicked");
				mChosenPosition = pos;
				getListView().showContextMenu();
				return true;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.v(TAG, "List item[" + position + "] has been clicked with ID of[" + id + "]");

				Intent intent = new Intent();
				intent.putExtra(Constants.PICKED_AUDIO_FILE_POSITION, position);
				Log.i(TAG, "Row title : " + ((TextView) view.findViewById(R.id.row_title)).getText());
				intent.putExtra(Constants.PICKED_AUDIO_FILE_NAME, view.findViewById(R.id.row_title).toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		});

	}

	private void refreshFileListAdapter() {
		mFileListAdapter = new AudioFileListAdapter(this, FileSys.listFilesInDir(AUDIO_FILES_DIR));
		mFileListAdapter = addSoundDefaults(mFileListAdapter);
		this.setListAdapter(mFileListAdapter);
	}
	
	private AudioFileListAdapter addSoundDefaults(AudioFileListAdapter fileListAdapter) {
		String defaultSoundFile = Environment.getExternalStorageDirectory() +  this.getString(R.string.dir_default_audio) + "carlcow.wav";
		File defaultSoundFile1 = new File(defaultSoundFile);
		
		if(!defaultSoundFile1.exists()){
			writeResToFile(this.getResources().openRawResource(R.raw.carlthecow), defaultSoundFile);
		}
		
		ArrayList<File> listFilesInDir = FileSys.listFilesInDir(Environment.getExternalStorageDirectory() +  this.getString(R.string.dir_default_audio));
		fileListAdapter.files.addAll(listFilesInDir);
	    return fileListAdapter;
	}

	private File writeResToFile(InputStream ins, String fileName) {
		int size;
		File file = null;
		
		try {
			Runtime.getRuntime().exec("chmod 666 " + Environment.getExternalStorageDirectory() +  this.getResources().getString(R.string.dir_default_audio));
			file = new File(fileName);
			size = ins.available();
            byte[] buffer = new byte[size];
            ins.read(buffer);
            ins.close();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (NotFoundException e1) {
            Log.e(TAG, "Problem with creating default files on external storage", e1);
        } catch (IOException e2) {
            Log.e(TAG, "Problem with creating default files on external storage", e2);
        }
        
        return file;
	}

}
