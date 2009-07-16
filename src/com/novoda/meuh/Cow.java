/**********************************************
 * 
 *  Copyright (C) 2009 Novoda
 *  
 *  la-boite-a-meuh
 *  :Cow
 *  
 *  A demonstration of Raw Audio playback
 *  and Orientation listening.
 *  Tilt the cow head to make a moo!
 * 
 *    
 *  http://www.novoda.com/blog
 * 
 */
package com.novoda.meuh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.novoda.meuh.media.SoundPoolMgr;
import com.novoda.os.FileSys;

public class Cow extends Activity {

	private static final String	TAG				= "[Moo]:";

	private int					mOrientation	= 0;
	private MooOnRotationEvent	mOnRotationEvent;
	private View				mView;
	private SoundPoolMgr		mMgr;
	private Menu				mOptMenu;
	private boolean				mIsMooChanging	= false;

	public static String		AUDIO_FILES_DIR;
	public static String		TMP_AUDIO_DIR;
	private static String		TMP_FILE;
	private static String		EMAIL_TMP;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mView = new CowHeadView(this);
		setContentView(mView);

		AUDIO_FILES_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_created_sounds);
		TMP_AUDIO_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_tmp);
		TMP_FILE = this.getString(R.string.filename_tmp);
		EMAIL_TMP =  this.getString(R.string.filename_emailed_tmp);

		Log.i(TAG, "Calling intent [" + getIntent().getDataString() + "], action[" + getIntent().getAction() + "]");

		if (getIntent().getData() != null) {
			Log.i(TAG, "The data within the intent: " + getIntent().getData());
			Log.i(TAG, "The encoded query within the intent: " + getIntent().getData().getEncodedQuery());
			Log.i(TAG, "The suthority within the intent: " + getIntent().getData().getAuthority());

			if (getIntent().getData().getAuthority().equals(Constants.NATIVE_GMAIL_AUTHORITY)) {
				copyAttachmentToTmpDir(getIntent().toURI(), AUDIO_FILES_DIR + EMAIL_TMP + Constants.FILE_EXT);
			}

		}

		initSoundPool();
		mOnRotationEvent = new MooOnRotationEvent(this);

		if (!mOnRotationEvent.canDetectOrientation()) {
			Toast.makeText(this, "Can't moo :(", 1000);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mOnRotationEvent.enable();
	}

	@Override
	protected void onStop() {
		mOnRotationEvent.disable();
		super.onStop();
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
			case R.layout.dialog_new:
				LayoutInflater factory = LayoutInflater.from(this);
				final View textEntryView = factory.inflate(R.layout.dialog_new, null);

				EditText contents = (EditText) textEntryView.findViewById(R.id.filename_edit);
				contents.getEditableText().append(TMP_FILE);

				return new AlertDialog.Builder(Cow.this).setIcon(R.drawable.alert_dialog_icon).setTitle(R.string.title_save_sound).setView(textEntryView).setPositiveButton(
						R.string.dialog_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								EditText contents = (EditText) textEntryView.findViewById(R.id.filename_edit);
								Editable editable = contents.getEditableText();
								String mNewFileName = editable.toString();

								String path = FileSys.createFilenameWithChecks(AUDIO_FILES_DIR, mNewFileName + Constants.FILE_EXT);
								FileSys.copyViaChannels(new File(TMP_AUDIO_DIR + TMP_FILE + Constants.FILE_EXT), new File(path));

							}
						}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create();

		}

		return null;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		mOptMenu = menu;

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_actions, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent();
		switch (item.getItemId()) {
			case R.id.record:
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.MOO_SOUND_3;
				intent.setClassName(getBaseContext(), "com.novoda.meuh.Cow");
				startActivityForResult(new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION), Constants.PICK_NEW_SOUND_REQUEST);
				mOptMenu.findItem(R.id.save).setEnabled(true);
				return true;

			case R.id.select:
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.MOO_SOUND_3;
				intent.setClassName(getBaseContext(), "com.novoda.meuh.MooFileMgr");
				startActivityForResult(intent, Constants.PICK_SOUND_REQUEST);
				return true;

			case R.id.save:
				mOptMenu.findItem(R.id.save).setEnabled(false);
				showDialog(R.layout.dialog_new);
				return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == Constants.PICK_SOUND_REQUEST && resultCode == RESULT_OK) {
			Log.i(TAG, "This is what is in data " + intent.getData());
			Log.i(TAG, "This is the action " + intent.getAction());
			Log.i(TAG, "This is the ID " + intent.getIntExtra(Constants.PICKED_AUDIO_FILE_POSITION, 999));

			ArrayList<File> files = FileSys.listFilesInDir(AUDIO_FILES_DIR);
			File file = files.get(intent.getIntExtra(Constants.PICKED_AUDIO_FILE_POSITION, 999));
			Log.i(TAG, "file I want is " + file.getAbsolutePath());
			SoundPoolMgr.SELECTED_MOO_FILE = file.getAbsolutePath();

			initSoundPool();
		}

		if (requestCode == Constants.PICK_NEW_SOUND_REQUEST && resultCode == RESULT_OK) {
			Log.i(TAG, intent.toURI());
			Log.i(TAG, "request code from recording =" + requestCode);
			Log.i(TAG, "result code from recording =" + resultCode);

			Cursor cursor = managedQuery(Uri.parse(intent.toURI()), null, null, null, null);
			startManagingCursor(cursor);
			cursor.moveToLast();

			if (copyToAudioDir(cursor.getString(Constants.COLUMN_RELATIVE_FILE_LOCATION))) {
				SoundPoolMgr.SELECTED_MOO_FILE = TMP_AUDIO_DIR + TMP_FILE + Constants.FILE_EXT;
				initSoundPool();
			}
		}

	}

	private void initSoundPool() {
		mMgr = new SoundPoolMgr(this);
		mMgr.init();
	}

	private void copyAttachmentToTmpDir(String attachmentURI, String dst) {
		Log.i(TAG, "The encoded path within the intent: " + getIntent().getData().getEncodedPath());
		Log.i(TAG, "The decoded query within the intent: " + getIntent().getData().getPath());

		for (String item : getIntent().getData().getPathSegments()) {
			Log.i(TAG, "path segment:  " + item);
		}

		if (new File(TMP_AUDIO_DIR + EMAIL_TMP + Constants.FILE_EXT).exists()) {
			boolean delete = new File(TMP_AUDIO_DIR + TMP_FILE + Constants.FILE_EXT).delete();
			Log.i(TAG, "The delete has been done: " + delete);
		}

		try {
			String path = FileSys.copyInputStreamToFile(getContentResolver().openInputStream(Uri.parse(attachmentURI)), new File(dst));
			// You have to make the created file readable.
			Runtime.getRuntime().exec("chmod 666 " + path);

		} catch (IOException e) {
			Log.e(TAG, "There was a problem copying the attached file to the audio dir", e);
		}
	}

	private boolean copyToAudioDir(String src) {
		if (new File(TMP_AUDIO_DIR + TMP_FILE + Constants.FILE_EXT).exists()) {
			boolean delete = new File(TMP_AUDIO_DIR + TMP_FILE + Constants.FILE_EXT).delete();
			Log.i(TAG, "The delete has been done: " + delete);
		}

		String dst = FileSys.createFilenameWithChecks(TMP_AUDIO_DIR, TMP_FILE + Constants.FILE_EXT);

		if (dst == null) {
			return false;
		} else {
			FileSys.copyViaChannels(new File(src), new File(dst));
		}

		return true;
	}

	/***
	 * Draws the cow head depending on the orientation
	 * 
	 */
	private class CowHeadView extends View {
		private Paint	mPaint	= new Paint();
		private Bitmap	bg;

		private Bitmap	cowHead;
		private int		cowHeadXOffset;
		private int		cowHeadYOffset;

		public CowHeadView(Context context) {
			super(context);

			bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
			cowHead = BitmapFactory.decodeResource(getResources(), R.drawable.cow);
			cowHeadXOffset = cowHead.getWidth() / 2;
			cowHeadYOffset = cowHead.getHeight() / 2;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			Paint paint = mPaint;
			canvas.drawBitmap(bg, 0, 0, paint);
			paint.setAntiAlias(true);
			int w = canvas.getWidth();
			int h = canvas.getHeight();
			int cx = (w / 2);
			int cy = (h / 2);

			canvas.translate(cx, cy);
			canvas.rotate(-1 * mOrientation);
			canvas.drawBitmap(cowHead, -cowHeadXOffset, -cowHeadYOffset, paint);
		}

	}

	/***
	 * Used for interpreting the movements of the device into 'mooPower' The
	 * sound is distorted depending on the amount of 'mooPower'
	 * 
	 */
	private class MooOnRotationEvent extends OrientationEventListener {

		private boolean	isMooing;
		private int		mooPower;

		public MooOnRotationEvent(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {

			mOrientation = orientation;
			if (mView != null) {
				mView.invalidate();
			}

			if (orientation > 60 && orientation < 300) {
				isMooing = true;
				mooPower++;
			} else {
				isMooing = false;
			}

			if (!isMooing && mooPower > 0) {

				Log.i(TAG, "Power " + mooPower);
				float speed = 1.0f;

				if (mooPower > 15)
					speed = 0.5f;
				else if (mooPower > 13)
					speed = 0.7f;
				else if (mooPower > 10)
					speed = 1.0f;
				else if (mooPower > 5)
					speed = 1.5f;
				else if (mooPower > 3)
					speed = 1.9f;

				moo(speed);
			}

		}

		public void moo(float speed) {
			if (!mIsMooChanging) {
				mMgr.playSound(speed);
			}
			mooPower = 0;

		}
	}

}