package com.novoda.meuh;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.novoda.meuh.media.MeuhSound;

public class Cow extends Activity {

	private static final String TAG = "[moo]:";

	private MeuhSound mCowSound;
	private int mOrientation = 0;
	private Moo m;
	private CowHeadView cowHeadView;

	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		view = new NCowHeadView(this);

		setContentView(view);

		// cowHeadView = (CowHeadView) findViewById(R.id.cowheadview);

		mCowSound = MeuhSound.create(this, R.raw.kevinthecow);

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

			if (cowHeadView != null) {

				cowHeadView.degrees = (float) orientation;
				cowHeadView.invalidate();
			}

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
			// cowHeadView.spinCowHead(800L);
			mCowSound.play(speed);
			mooPower = 0;
		}
	}

	private class NCowHeadView extends View {
		private Paint mPaint = new Paint();
		private Bitmap bg;

		private Bitmap cowHead;
		private int cowHeadXOffset;
		private int cowHeadYOffset;

		public NCowHeadView(Context context) {
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
}