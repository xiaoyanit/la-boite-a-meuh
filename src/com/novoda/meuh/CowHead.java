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
import android.os.Debug;
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

public class CowHead extends Activity {

	private static final String	TAG				= "[CowHead]";

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

//		try {
//			String path = "/sdcard/la-boite-a-meuh/media/audio/meuhs/cowhead_trace.trace";
//			Runtime.getRuntime().exec("chmod 666 " + path);
//		} catch (IOException e) {
//			Log.e(TAG, "There was a problem copying the attached file to the audio dir", e);
//		}
		
		Debug.startMethodTracing("cowhead_trace");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mView = new CowHeadView(this);
		setContentView(mView);

		Log.i(TAG, "Intent passed to CowHead: Data String[" + getIntent().getDataString() + "], Action[" + getIntent().getAction() + "]");

		if (getIntent().getData() != null) {
			Log.d(TAG, "Intent passed to CowHead: Data[" + getIntent().getData() + "]");
			Log.d(TAG, "Intent passed to CowHead: Encoded query[ " + getIntent().getData().getEncodedQuery() + "]");
			Log.d(TAG, "Intent passed to CowHead: authority [" + getIntent().getData().getAuthority() + "]");

			if (getIntent().getData().getAuthority().equals(Constants.NATIVE_GMAIL_AUTHORITY)) {
				Log.d(TAG, "Intention originates from GMail, URI[" + getIntent().toURI() + "]");
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
	protected void onStart() {
		super.onStart();
		AUDIO_FILES_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_created_sounds);
		TMP_AUDIO_DIR = Environment.getExternalStorageDirectory() + this.getString(R.string.dir_tmp);
		TMP_FILE = this.getString(R.string.filename_tmp);
		EMAIL_TMP = this.getString(R.string.filename_emailed_tmp);
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
			case R.layout.dialog_save_new_sound:
				LayoutInflater factory = LayoutInflater.from(this);
				final View textEntryView = factory.inflate(R.layout.dialog_save_new_sound, null);

				EditText contents = (EditText) textEntryView.findViewById(R.id.filename_edit);
				contents.getEditableText().append(TMP_FILE);

				return new AlertDialog.Builder(CowHead.this).setIcon(R.drawable.alert_dialog_icon).setTitle(R.string.title_save_sound).setView(textEntryView).setPositiveButton(
						R.string.dialog_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								EditText contents = (EditText) textEntryView.findViewById(R.id.filename_edit);
								Editable editable = contents.getEditableText();
								String mNewFileName = editable.toString();

								String path = FileSys.createFilenameWithChecks(AUDIO_FILES_DIR, mNewFileName + Constants.FILE_EXT);
								FileSys.copyViaChannels(new File(TMP_AUDIO_DIR + TMP_FILE + Constants.FILE_EXT), new File(path));
								Log.i(TAG, "Saved new sound called [" + mNewFileName + Constants.FILE_EXT + "] to [" + path + "]");
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
		inflater.inflate(R.menu.cow_head_actions, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent();
		switch (item.getItemId()) {
			case R.id.record:
				Log.v(TAG, "User selected to Record a new sound");
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.CUSTOM_MOO_SOUND;
				intent.setClassName(getBaseContext(), "com.novoda.meuh.CowHead");
				startActivityForResult(new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION), Constants.PICK_NEW_SOUND_REQUEST);
				mOptMenu.findItem(R.id.save).setVisible(true);
				return true;

			case R.id.select:
				Log.v(TAG, "User selected to select a listed sound");
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.CUSTOM_MOO_SOUND;
				intent.setClassName(getBaseContext(), "com.novoda.meuh.FileMgr");
				startActivityForResult(intent, Constants.PICK_SOUND_REQUEST);
				return true;

			case R.id.save:
				Log.v(TAG, "User selected to save a sound");
				mOptMenu.findItem(R.id.save).setVisible(false);
				showDialog(R.layout.dialog_save_new_sound);
				return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == Constants.PICK_SOUND_REQUEST && resultCode == RESULT_OK) {
			Log.v(TAG, "A sound has selected ID[" + intent.getIntExtra(Constants.PICKED_AUDIO_FILE_POSITION, 999) + "] with Data[" + intent.getData() + "]");
			Log.d(TAG, "Intent URI[" + intent.toURI() + "]");

			ArrayList<File> files = FileSys.listFilesInDir(AUDIO_FILES_DIR);
			File file = files.get(intent.getIntExtra(Constants.PICKED_AUDIO_FILE_POSITION, 999));
			Log.v(TAG, "The selected file I is[" + file.getAbsolutePath() + "]");
			SoundPoolMgr.SELECTED_MOO_FILE = file.getAbsolutePath();
			initSoundPool();
		}

		if (requestCode == Constants.PICK_NEW_SOUND_REQUEST && resultCode == RESULT_OK) {
			Log.v(TAG, "A new sound has been recorded ID[" + intent.getIntExtra(Constants.PICKED_AUDIO_FILE_POSITION, 999) + "] with Data[" + intent.getData() + "]");
			Log.d(TAG, "Intent URI[" + intent.toURI() + "]");

			Cursor cursor = managedQuery(Uri.parse(intent.toURI()), null, null, null, null);
			startManagingCursor(cursor);
			cursor.moveToLast();

			if (copyToAudioDir(cursor.getString(Constants.COLUMN_RELATIVE_FILE_LOCATION))) {
				SoundPoolMgr.SELECTED_MOO_FILE = TMP_AUDIO_DIR + TMP_FILE + Constants.FILE_EXT;
				initSoundPool();
			}
		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Debug.stopMethodTracing();
	}

	private void initSoundPool() {
		mMgr = new SoundPoolMgr(this);
		mMgr.init();
	}

	/***
	 * Copy across the atachment and then ammend the Read/Write privs.
	 * 
	 * @param attachmentURI
	 * @param dst
	 */
	private void copyAttachmentToTmpDir(String attachmentURI, String dst) {
		Log.v(TAG, "Copying an attachment  to tmp dir Data[" + getIntent().getData().getPath() + "]");

		if (new File(TMP_AUDIO_DIR + EMAIL_TMP + Constants.FILE_EXT).exists()) {
			boolean delete = new File(TMP_AUDIO_DIR + TMP_FILE + Constants.FILE_EXT).delete();
			Log.i(TAG, "The delete has been done: " + delete);
		}

		try {
			String path = FileSys.copyInputStreamToFile(getContentResolver().openInputStream(Uri.parse(attachmentURI)), new File(dst));
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
		private Bitmap	bg;
		private Bitmap	cowHead;
		Paint mPaint;
		
		public CowHeadView(Context context) {
			super(context);

			bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
			cowHead = BitmapFactory.decodeResource(getResources(), R.drawable.cow);
			mPaint	= new Paint();
		}

		/***
		 * 	
		 * This function is called upon each rotation Event
		 * and so is a bottle neck.
		 * 
		 * The logic has been cut down to involve less references.
		 * 160 = BG width 320 / 2
		 * 228 = BG height 456 / 2 
		 * 115 = CowHead width 230 / 2
		 * 85 = CowHead 170 / 2
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(bg, 0, 0, mPaint);
			mPaint.setAntiAlias(true);
			
			canvas.translate(160, 228);
			canvas.rotate(-1 * mOrientation);
           	canvas.drawBitmap(cowHead, -115, -85, mPaint);
           	mPaint.setAntiAlias(false);
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

		/***
		 * Invalidation is constrained so only the head is redrawn
		 * and not the background.
		 * CowHead bounds: 30, 70, 300, 360
		 * 
		 */
		@Override
		public void onOrientationChanged(int orientation) {

			mOrientation = orientation;
			if (mView != null) {
				mView.invalidate(30, 70, 300, 360);
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