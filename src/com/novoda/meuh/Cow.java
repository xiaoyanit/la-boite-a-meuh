package com.novoda.meuh;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

public class Cow extends Activity {

	private static final String	TAG												= "[moo]:";
	private TextView						status;
	private TextView						coOrds;
	private TextView						accuracy_str;
	private float[]							currentOrientationValues	= new float[3];
	private TextView						currentX;
	private TextView						currentY;

  private boolean mIsBound;
  private MooService mBoundService;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    
		status = (TextView) findViewById(R.id.txt_status);
		accuracy_str = (TextView) findViewById(R.id.txt_accuracy);
		currentX = (TextView) findViewById(R.id.txt_X);
		currentY = (TextView) findViewById(R.id.txt_Y);

		setContentView(R.layout.main);
    bindService(new Intent(Cow.this, MooService.class), mConnection, Context.BIND_AUTO_CREATE);
    mIsBound = true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
    if (mIsBound) {
      unbindService(mConnection);
      mIsBound = false;
    }
	}
	
  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
        mBoundService = ((MooService.LocalBinder)service).getService();
        
        Toast.makeText(Cow.this, R.string.moo_serv_start, Toast.LENGTH_SHORT).show();
    }

    public void onServiceDisconnected(ComponentName className) {
        mBoundService = null;
        Toast.makeText(Cow.this, R.string.moo_serv_stop,  Toast.LENGTH_SHORT).show();
    }
};

	public void onAccuracyChanged(int sensor, int accuracy) {

		// Log.i(TAG, "Sensor: [" + Integer.toString(sensor) + "]");
		// Log.i(TAG, "Accuracy:" + Integer.toString(accuracy) );
		//		
		// status.setText("Sensor: [" + Integer.toString(sensor) + "]");
		// accuracy_str.setText(Integer.toString(accuracy));
	}

	public void onSensorChanged(int sensor, float[] values) {
//
//		synchronized (this) {
//			if (sensor == SensorManager.SENSOR_ORIENTATION) {
//				currentX.setText(Float.toString(values[0]));
//				currentY.setText(Float.toString(values[1]));
//			}
//		}
		
		
	}

	//		
	// int entry, allValues = values.length;
	// StringBuffer builtString = new StringBuffer(allValues);
	//			
	// status.setText("Sensor: [" + Integer.toString(sensor) + "]");
	// Log.i(TAG, "Sensor: [" + Integer.toString(sensor) + "]");
	//			
	// for (entry = (allValues - 1); entry >= 0; entry--){
	// builtString.append(values[entry]);
	// }
	//			
	// Log.i(TAG, "CoOrds:" + builtString );
	// coOrds.setText(builtString);
	//		
	// }

}
