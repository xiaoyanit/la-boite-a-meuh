/**********************************************
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.novoda.meuh.media.SoundPoolMgr;
import com.novoda.os.FileSys;

public class Cow extends Activity {

	private static final String	TAG				= "[Moo]:";

	private int					mOrientation	= 0;
	private MooOnRotationEvent	mooOnRotationEvent;

	private View				view;

	private SoundPoolMgr		mgr;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		view = new CowHeadView(this);
		setContentView(view);

		initSoundPool();
		mooOnRotationEvent = new MooOnRotationEvent(this);

		if (!mooOnRotationEvent.canDetectOrientation()) {
			Toast.makeText(this, "Can't moo :(", 1000);
		}
	}

	private void initSoundPool() {
		mgr = new SoundPoolMgr(this);
		mgr.init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mooOnRotationEvent.enable();
	}

	@Override
	protected void onStop() {
		mooOnRotationEvent.disable();
		super.onStop();
	}

	private boolean	isMooChanging	= false;

	private class MooOnRotationEvent extends OrientationEventListener {

		private boolean	isMooing;
		private int		mooPower;

		public MooOnRotationEvent(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {

			mOrientation = orientation;
			if (view != null) {
				view.invalidate();
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
			if (!isMooChanging) {
				mgr.playSound(speed);
			}
			mooPower = 0;

		}
	}

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
			int cy = (h / 2) + 40;

			canvas.translate(cx, cy);
			canvas.rotate(-1 * mOrientation);
			canvas.drawBitmap(cowHead, -cowHeadXOffset, -cowHeadYOffset, paint);
		}

		@Override
		protected void onAttachedToWindow() {
			super.onAttachedToWindow();
		}

		@Override
		protected void onDetachedFromWindow() {
			super.onDetachedFromWindow();
		}
	}

	/*********************** Menu creation ***********************/
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Constants.MENU_RECORD_SOUND, 0, "Record").setIcon(android.R.drawable.btn_dropdown);
		menu.add(0, Constants.MENU_CHOOSE_AUDIO_FROM_LIST, 0, "Select").setIcon(android.R.drawable.ic_media_play);
		menu.add(0, Constants.MENU_SAVE_NEW_SOUND, 0, "Save current sound").setIcon(android.R.drawable.btn_dropdown);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
			case Constants.MENU_RECORD_SOUND:
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.MOO_SOUND_3;
				intent.setClassName(getBaseContext(), "com.novoda.meuh.Cow");
				startActivityForResult(new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION), Constants.PICK_NEW_SOUND_REQUEST);
				return true;
			case Constants.MENU_CHOOSE_AUDIO_FROM_LIST:
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.MOO_SOUND_3;				
				intent.setClassName(getBaseContext(), "com.novoda.meuh.MooFileMgr");
				startActivityForResult(intent, Constants.PICK_SOUND_REQUEST);
				return true;
			case Constants.MENU_SAVE_NEW_SOUND:
				//popup dialog with save
				return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "Got the result" + data.getData());
		
		
		Log.i(TAG, "request code from picking a sound =" + requestCode);
		Log.i(TAG, "result code from picking a sound =" + resultCode);

		
		if (requestCode == Constants.PICK_SOUND_REQUEST) {
			if (resultCode == RESULT_OK) {

				Log.i(TAG, "This is what is in data " + data.getData());
				Log.i(TAG, "This is the action " + data.getAction());
				Log.i(TAG, "This is the ID " + data.getIntExtra(Constants.PICKED_AUDIO_FILE_POSITION, 999));

				ArrayList<File> files = FileSys.listFilesInDir_asFiles(Constants.AUDIO_FILES_DIR);
				File file = files.get(data.getIntExtra(Constants.PICKED_AUDIO_FILE_POSITION, 999));
				Log.i(TAG, "file I want is " + file.getAbsolutePath());
				SoundPoolMgr.SELECTED_MOO_FILE = file.getAbsolutePath();

				initSoundPool();
			}
		}
		
		
		if (requestCode == Constants.PICK_NEW_SOUND_REQUEST) {
			if (resultCode == RESULT_OK) {
				
				Log.i(TAG, data.toURI());
				Log.i(TAG, "request code from recording =" + requestCode);
				Log.i(TAG, "result code from recording =" + resultCode);

				Cursor cursor = managedQuery(Uri.parse(data.toURI()), null, null, null, null);
				startManagingCursor(cursor);
				cursor.moveToLast();

				try {
					String path = FileSys.createFilenameWithChecks(Constants.AUDIO_FILES_DIR, "user_meuh", ".3gpp");
					FileSys.copyViaChannels(new File(cursor.getString(Constants.COLUMN_RELATIVE_FILE_LOCATION)), new File(path));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
			case Constants.MENU_CHOOSE_AUDIO_FROM_LIST:

				String[] listOfFiles = new File(Constants.AUDIO_FILES_DIR).list();
				final ArrayList<File> files = FileSys.listFilesInDir_asFiles(Constants.AUDIO_FILES_DIR);

				return new AlertDialog.Builder(Cow.this)
					.setIcon(R.drawable.alert_dialog_icon)
					.setTitle(R.string.title_choose_sound)
					.setSingleChoiceItems(listOfFiles, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								SoundPoolMgr.SELECTED_MOO_FILE = files.get(whichButton).getAbsolutePath();
								initSoundPool();
							}
						}
					)
					.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					})
					.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					}).create();
		}

		return null;
	}

}