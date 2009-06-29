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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.novoda.meuh.media.MeuhSound;
import com.novoda.meuh.media.SoundPoolMgr;

public class Cow extends Activity {

	private static final String	TAG						= "[Moo]:";

	private static final int	CARL_ID					= 0;
	private static final int	KEVIN_ID				= 1;
	private static final int	SWITCH_SOUND_MANAGER	= 2;

	private boolean				soundManager			= true;

	private int					mOrientation			= 0;
	private MooOnRotationEvent	mooOnRotationEvent;

	private View				view;

	private SoundPoolMgr		mgr;
	private MeuhSound			mCowSound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		view = new CowHeadView(this);
		setContentView(view);

		mCowSound = MeuhSound.create(this, R.raw.carlthecow);

		mgr = new SoundPoolMgr(this);
		mgr.init();

		mooOnRotationEvent = new MooOnRotationEvent(this);

		if (!mooOnRotationEvent.canDetectOrientation()) {
			Toast.makeText(this, "Can't moo :(", 1000);
		}
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
				if (soundManager)
					mgr.playSound(speed);
				else
					mCowSound.play(speed);
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
		menu.add(0, CARL_ID, 0, "Carl's French meuh").setIcon(R.drawable.carl);
		menu.add(0, KEVIN_ID, 0, "Kevin's Scottish moo").setIcon(R.drawable.kevin);
		menu.add(0, SWITCH_SOUND_MANAGER, 0, "change sound manager").setIcon(android.R.drawable.ic_media_play);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case CARL_ID:
				isMooChanging = true;
				mCowSound.dispose();
				mCowSound = MeuhSound.create(this, R.raw.carlthecow);
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.MOO_SOUND_1;
				isMooChanging = false;
				return true;
			case KEVIN_ID:
				isMooChanging = true;
				mCowSound.dispose();
				mCowSound = MeuhSound.create(this, R.raw.kevinthecow);
				SoundPoolMgr.SELECTED_MOO_SOUND = SoundPoolMgr.MOO_SOUND_2;
				isMooChanging = false;
				return true;
			case SWITCH_SOUND_MANAGER:
				soundManager = !soundManager;
				Intent intent = new Intent();
				intent.setClassName(getBaseContext(), "com.novoda.meuh.MooRecorder");
				startActivity(intent);
				return true;
		}
		return false;
	}
}