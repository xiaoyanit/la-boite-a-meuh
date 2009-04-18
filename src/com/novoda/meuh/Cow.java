package com.novoda.meuh;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class Cow extends Activity implements SensorListener {

	private static final String	TAG							= "[moo]:";

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
		private Bitmap		cowHead;
		private int				cowHeadXOffset;
		private int				cowHeadYOffset;

		private Animation	anim;

    public CowHeadView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        
  			cowHead = BitmapFactory.decodeResource(getResources(), R.drawable.me);
  			cowHeadXOffset = cowHead.getWidth() / 2;
  			cowHeadYOffset = cowHead.getHeight() / 2;

    }
    
    @Override protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
  			super.onDraw(canvas);

  			// creates the animation the first time
  			if (anim == null) {
  				createAnim(canvas);
  			}

  			int centerX = canvas.getWidth() / 2;
  			int centerY = canvas.getHeight() / 2;

  			canvas.drawBitmap(cowHead, centerX - cowHeadXOffset, centerY - cowHeadYOffset, null);
//        mDrawable.draw(canvas);
        invalidate();
    }
    
		private void createAnim(Canvas canvas) {
			anim = new RotateAnimation(0, 360, canvas.getWidth() / 2, canvas.getHeight() / 2);
			anim.setRepeatMode(Animation.RESTART);
			anim.setRepeatCount(5);
			anim.setDuration(1800L);
			anim.setInterpolator(new LinearInterpolator());

			startAnimation(anim);
		}
}
	

}
