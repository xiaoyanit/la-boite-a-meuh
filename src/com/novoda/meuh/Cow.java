package com.novoda.meuh;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class Cow extends Activity implements SensorListener {

	private static final String	TAG							= "[moo]:";

	private boolean							mooServiceIsBound;
	private ServiceConnection		mooServiceConnection;
	private SensorManager				mSensorManager;
	private Date								startTime;
	protected static final int	DORMANT_COW			= 45;
	protected static final int	BUILDING_GAS		= 53;
	protected static final int	MOO							= 88;
	private int									cowState				= DORMANT_COW;
	private static final int		MOO_THRESHHOLD	= -60;

	private long								totalMooPower;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    setContentView(new CowHeadView(this));

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER | SensorManager.SENSOR_MAGNETIC_FIELD | SensorManager.SENSOR_ORIENTATION,
				SensorManager.SENSOR_DELAY_FASTEST);

		this.startTime = new Date(000000000);

		mooServiceIsBound = true;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mooServiceIsBound) {
			unbindService(mooServiceConnection);
			mooServiceIsBound = false;
		}
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
					cowState = Cow.DORMANT_COW;
				}
			}

		}
	}
	
  private static class CowHeadView extends View {
    private CowHead mDrawable;

    public CowHeadView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        Drawable dr = context.getResources().getDrawable(R.drawable.me);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        
        Animation an = new TranslateAnimation(0, 100, 0, 200);
        an.setDuration(2000);
        an.setRepeatCount(-1);
        an.initialize(10, 10, 10, 10);
        
        mDrawable = new CowHead(dr, an);
        an.startNow();
    }
    
    @Override protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        mDrawable.draw(canvas);
        invalidate();
    }
}
	

}
