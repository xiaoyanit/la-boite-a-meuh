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
import java.util.logging.Logger;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.novoda.meuh.media.SoundPoolMgr;
import com.novoda.os.FileSys;
import com.novoda.view.FileListingAdapter;

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
		
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent = new Intent();
		switch (item.getItemId()) {
            case R.id.record:
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.MOO_SOUND_3;
				intent.setClassName(getBaseContext(), "com.novoda.meuh.Cow");
				startActivityForResult(new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION), Constants.PICK_NEW_SOUND_REQUEST);
				return true;
			case R.id.select:
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.MOO_SOUND_3;				
				intent.setClassName(getBaseContext(), "com.novoda.meuh.MooFileMgr");
				startActivityForResult(intent, Constants.PICK_SOUND_REQUEST);
				return true;
			case R.id.save:
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

				ArrayList<File> files = FileSys.listFilesInDir_asFiles(Constants.AUDIO_FILES_DIR);
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
				String path =null;
				
				try {
					path = FileSys.createFilenameWithChecks(Constants.AUDIO_FILES_DIR, "user_meuh", ".3gpp");
					FileSys.copyViaChannels(new File(cursor.getString(Constants.COLUMN_RELATIVE_FILE_LOCATION)), new File(path));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				if(path != null){
					FileListingAdapter fileListAdapter = new FileListingAdapter(this, FileSys.listFilesInDir_asFiles(Constants.AUDIO_FILES_DIR));
					Log.i(TAG, "The path we're looking for is:" + path);
					int lastindexIs = fileListAdapter.files.indexOf(new File(path));
					Log.i(TAG, "The index for this path is: " + lastindexIs);
					
					
					File file = fileListAdapter.files.get(lastindexIs);
					Log.i(TAG, "file I want is " + file.getAbsolutePath());
					SoundPoolMgr.SELECTED_MOO_FILE = file.getAbsolutePath();
					
					initSoundPool();
				}
				
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
			case R.id.select:

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