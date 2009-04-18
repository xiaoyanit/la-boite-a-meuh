package com.novoda.meuh;

import java.util.Date;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.novoda.meuh.media.MeuhSound;

public class Cow extends Activity implements SensorEventListener {

	private static final String TAG = "[moo]:";

	private Date startTime;
	protected static final int DORMANT_COW = 45;
	protected static final int BUILDING_GAS = 53;
	protected static final int MOO = 88;
	private int cowState = DORMANT_COW;

	private static final int MOO_THRESHHOLD = 60;

	private boolean hasTilted = false;

	private long totalMooPower;

	private MeuhSound mCowSound;

	private SensorManager mSensorManager;

	private long enterTime;

	private long exitTime;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);
		mCowSound = MeuhSound.create(this, R.raw.kevinthecow);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(this);
		super.onStop();
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ORIENTATION)
			return;

		Log.i(TAG, "x " + event.values[0] + " ,y " + event.values[1] + " ,z "
				+ event.values[2]);

		synchronized (this) {
			// if (event.values[2] > Cow.MOO_THRESHHOLD
			// && cowState == Cow.DORMANT_COW) {
			// startTime = new Date();
			// cowState = Cow.BUILDING_GAS;
			// }
			//
			// if (event.values[2] < Cow.MOO_THRESHHOLD
			// && cowState == Cow.BUILDING_GAS) {
			// totalMooPower = startTime.getTime()
			// - System.currentTimeMillis();
			// Log.d(TAG, "Moo Power: [" + totalMooPower + "]");
			// cowState = Cow.DORMANT_COW;
			// }
			
			// First time it tilts
			if (Math.abs(event.values[2]) > Cow.MOO_THRESHHOLD && !hasTilted) {
				enterTime = System.currentTimeMillis();
				hasTilted = true;
			}

			if (Math.abs(event.values[2]) < Cow.MOO_THRESHHOLD && hasTilted) {
				exitTime = System.currentTimeMillis();
				// playSound(enterTime - exitTime);
				long lon = enterTime - exitTime;
				lon = (lon > 4000) ? 4000 : lon;
				Log.i(TAG, "enter " + lon / 1000);
				mCowSound.play(lon / 1000);
				hasTilted = false;
			}
		}
	}

	private void playSound(long lon) {
		lon = (lon > 4000) ? 4000 : lon;
		mCowSound.play(lon / 1000);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}