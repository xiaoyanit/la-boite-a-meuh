package com.novoda.meuh;


import java.util.Date;

import android.app.Activity;
import android.content.ServiceConnection;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class Cow extends Activity implements SensorListener{

	private static final String	TAG												= "[moo]:";
	private TextView						status;
	private TextView						coOrds;
	private TextView						accuracy_str;
	private TextView						currentX;
	private TextView						currentY;

  private boolean mooServiceIsBound;
	private ServiceConnection	mooServiceConnection;
	private Handler	mooHandler;	
  private SensorManager				mSensorManager;
	private Date	startTime;
	protected static final int	DORMANT_COW	= 45;
	protected static final int	BUILDING_GAS	= 53;
	protected static final int	MOO	= 88;
	private int	cowState = DORMANT_COW;
	private static final int	MOO_THRESHHOLD	= -60;
	
	private long	totalMooPower;
  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
    
		status = (TextView) findViewById(R.id.txt_status);
		accuracy_str = (TextView) findViewById(R.id.txt_accuracy);
		currentX = (TextView) findViewById(R.id.txt_X);
		currentY = (TextView) findViewById(R.id.txt_Y);
		
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
					
					if(values[1] > Cow.MOO_THRESHHOLD && cowState == Cow.DORMANT_COW){
						startTime = new Date();
						cowState = Cow.BUILDING_GAS;
					}
					
					if(values[1] < Cow.MOO_THRESHHOLD && cowState == Cow.BUILDING_GAS){
						totalMooPower = startTime.getTime() - System.currentTimeMillis();
						Log.d(TAG, "Moo Power: [" + totalMooPower + "]");
						cowState = Cow.DORMANT_COW;
					}
				}

			}
	}
  
  

}
