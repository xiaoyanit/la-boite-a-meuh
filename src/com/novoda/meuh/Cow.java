package com.novoda.meuh;

import android.app.Activity;
import android.hardware.SensorListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Cow extends Activity implements SensorListener {
	
	private static final String	TAG	= "[moo]:";
	private TextView	status;
	private TextView	coOrds;
	private TextView	accuracy_str;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		status = (TextView) findViewById(R.id.str_status);
		accuracy_str = (TextView) findViewById(R.id.str_accuracy);
		coOrds = (TextView) findViewById(R.id.str_coords);

	}

	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		
		Log.i(TAG, "Sensor: [" + Integer.toString(sensor) + "]");
		Log.i(TAG, "Accuracy:" + Integer.toString(accuracy) );
		
		status.setText("Sensor: [" + Integer.toString(sensor) + "]");
		accuracy_str.setText(Integer.toString(accuracy));
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		status.setText("Sensor: [" + Integer.toString(sensor) + "]");
		
		Log.i(TAG, "Sensor: [" + Integer.toString(sensor) + "]");
		
    int entry, allValues = values.length;
    StringBuffer builtString = new StringBuffer(allValues);

    for (entry = (allValues - 1); entry >= 0; entry--){
    	builtString.append(values[entry]);
    }
		
    Log.i(TAG, "CoOrds:" + builtString );
		coOrds.setText(builtString);
	}

}
