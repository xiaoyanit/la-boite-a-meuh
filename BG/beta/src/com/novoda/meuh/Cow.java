package com.novoda.meuh;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
	private int mOrientation = -1;
	private Moo m;
	private CowHeadView	cowHeadView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		setContentView(R.layout.main);
		cowHeadView = (CowHeadView) findViewById(R.id.cowheadview);
		
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
		//	cowHeadView.spinCowHead(800L);
			mCowSound.play(speed);
			mooPower = 0;
		}

	}

}