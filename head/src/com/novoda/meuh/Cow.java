package com.novoda.meuh;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class Cow extends Activity implements SensorListener {

	private static final String	TAG				= "[moo]:";

	private SensorManager		mSensorManager;
	private Date				startTime;
	protected static final int	DORMANT_COW		= 45;
	protected static final int	BUILDING_GAS	= 53;
	protected static final int	MOO				= 88;
	private int					cowState		= DORMANT_COW;
	private static final int	MOO_THRESHHOLD	= -60;

	private long				totalMooPower;

	private CowHeadView			cowHeadView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		cowHeadView = (CowHeadView)findViewById(R.id.cowheadview);
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER | SensorManager.SENSOR_MAGNETIC_FIELD | SensorManager.SENSOR_ORIENTATION,
				SensorManager.SENSOR_DELAY_FASTEST);

		this.startTime = new Date(000000000);
		this.setRequestedOrientation(1);
	}

	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {

		synchronized (this) {
			if (sensor == SensorManager.SENSOR_ORIENTATION) {

				if (values[1] > Cow.MOO_THRESHHOLD && cowState == Cow.DORMANT_COW) {
					startTime = new Date();
					cowState = Cow.BUILDING_GAS;
				}

				if (values[1] < Cow.MOO_THRESHHOLD && cowState == Cow.BUILDING_GAS) {
					totalMooPower = startTime.getTime() - System.currentTimeMillis();
					Log.d(TAG, "Moo Power: [" + totalMooPower + "]");
					moo(totalMooPower);
					cowState = Cow.DORMANT_COW;
				}
			}

		}
	}

	private void moo(long totalMooPower) {
		cowHeadView.spinCowHead(totalMooPower);
		// makeMooSound();
	}

}
