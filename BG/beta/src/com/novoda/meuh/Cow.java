package com.novoda.meuh;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.novoda.meuh.media.MeuhSound;

public class Cow extends Activity {

	private static final String TAG = "[moo]:";

	private static final int CARL_ID = 0;
	private static final int KEVIN_ID = 1;
	private static final int YOU_ID = 2;

	private MeuhSound mCowSound;
	private int mOrientation = 0;
	private Moo m;

	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		view = new CowHeadView(this);
		setContentView(view);

		mCowSound = MeuhSound.create(this, R.raw.carlthecow);

		m = new Moo(this);
		if (!m.canDetectOrientation())
			Toast.makeText(this, "can't moo :(", 1000);
	}

	@Override
	protected void onResume() {
		super.onResume();
		m.enable();
	}

	@Override
	protected void onStop() {
		m.disable();
		super.onStop();
	}

	private boolean isMooChanging = false;

	private class Moo extends OrientationEventListener {

		private boolean isMooing;
		private int mooPower;

		public Moo(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {
			mOrientation = orientation;
			if (view != null)
				view.invalidate();

			if (orientation > 60 && orientation < 300) {
				isMooing = true;
				mooPower++;
			} else
				isMooing = false;

			if (!isMooing && mooPower > 0) {
				// play sound
				Log.i(TAG, "Power " + mooPower);
				double speed = 1;
				if (mooPower < 3)
					speed = 3;
				else if (mooPower < 5)
					speed = 2;
				else if (mooPower < 10)
					speed = 1;
				else if (mooPower < 13)
					speed = 0.7;
				else if (mooPower < 15)
					speed = 0.5;
				else
					speed = 0.4;
				moo(speed);
			}
		}

		public void moo(double speed) {
			if (!isMooChanging) {
				mCowSound.play(speed);
			}
			mooPower = 0;

		}
	}

	private class CowHeadView extends View {
		private Paint mPaint = new Paint();
		private Bitmap bg;

		private Bitmap cowHead;
		private int cowHeadXOffset;
		private int cowHeadYOffset;

		public CowHeadView(Context context) {
			super(context);

			bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);

			// Construct the cow head
			cowHead = BitmapFactory.decodeResource(getResources(),
					R.drawable.cow);
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
			int cx = w / 2;
			int cy = h / 2;

			// move it a bit more to the bottom so the cow head fits.
			cy += 40;
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
		menu.add(0, CARL_ID, 0, "Carl is a cow").setIcon(R.drawable.carl);
		menu.add(0, KEVIN_ID, 0, "Kevin is a cow").setIcon(R.drawable.kevin);
		
		// Will be enabled later.
		//menu.add(0, YOU_ID, 0, "You are a cow").setIcon(R.drawable.you);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CARL_ID:
			isMooChanging = true;
			mCowSound.dispose();
			mCowSound = MeuhSound.create(this, R.raw.carlthecow);
			isMooChanging = false;
			return true;
		case KEVIN_ID:
			mCowSound.dispose();
			mCowSound = MeuhSound.create(this, R.raw.kevinthecow);
			return true;
		case YOU_ID:
			// Not implemented yet
		}
		return false;
	}
}