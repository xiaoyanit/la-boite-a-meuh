package com.novoda.meuh;


import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MooService extends Service implements SensorListener {
  private NotificationManager mNM;
  private final IBinder mBinder = new LocalBinder();
  private SensorManager				mSensorManager;
	private Date	startTime;
	private int	cowState = DORMANT_COW;
	private long	totalMooPower;
	private static final String	TAG												= "[mooServ]:";
	private static final int	DORMANT_COW	= 45;
	private static final int	BUILDING_GAS	= 53;
  
  @Override
  public void onCreate() {
      mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
  		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
  		mSensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER | SensorManager.SENSOR_MAGNETIC_FIELD | SensorManager.SENSOR_ORIENTATION,
  				SensorManager.SENSOR_DELAY_FASTEST);
  		
  		this.startTime = new Date(000000000);
  		
      showNotification();
  }

  @Override
  public void onDestroy() {
      mNM.cancel(R.string.moo_serv_stop);
      Toast.makeText(this, R.string.moo_serv_stop, Toast.LENGTH_SHORT).show();
  }

  @Override
  public IBinder onBind(Intent intent) {
      return mBinder;
  }

  public class LocalBinder extends Binder {
  	MooService getService() {
  		return MooService.this;
  	}
  }

  private void showNotification() {
      CharSequence text = getText(R.string.moo_serv_start);
      Notification notification = new Notification(R.drawable.stat_sample, text,  System.currentTimeMillis());
      PendingIntent contentIntent = PendingIntent.getActivity(this, 0,  new Intent(this, Cow.class), 0);
      notification.setLatestEventInfo(this, getText(R.string.moo_serv_lbl), text, contentIntent);
      mNM.notify(R.string.moo_serv_start, notification);
  }

	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		if (sensor == SensorManager.SENSOR_ORIENTATION) {
			//Log.d(TAG, "sensor: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);		
			
			if(values[1] > -60 && cowState == DORMANT_COW){
				startTime = new Date();
				cowState = BUILDING_GAS;
			}
			
			if(values[1] < -60 && cowState == BUILDING_GAS){
				totalMooPower = startTime.getTime() - System.currentTimeMillis();
				Log.d(TAG, "Moo Power: [" + totalMooPower + "]");
				cowState = DORMANT_COW;
			}
			
			
		}
	}
}