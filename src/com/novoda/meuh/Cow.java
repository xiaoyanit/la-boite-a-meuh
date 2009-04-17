package com.novoda.meuh;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Cow extends Activity {
	
	private static final String	TAG	= "[moo]:";
	private TextView	status;
	private TextView	coOrds;
	private TextView	accuracy_str;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		status = (TextView) findViewById(R.id.txt_status);
		accuracy_str = (TextView) findViewById(R.id.txt_accuracy);
		coOrds = (TextView) findViewById(R.id.txt_coords);
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
		
		Log.i(TAG, "Sensor: [" + Integer.toString(sensor) + "]");
		Log.i(TAG, "Accuracy:" + Integer.toString(accuracy) );
		
		status.setText("Sensor: [" + Integer.toString(sensor) + "]");
		accuracy_str.setText(Integer.toString(accuracy));
	}

	public void onSensorChanged(int sensor, float[] values) {
		int entry, allValues = values.length;
		StringBuffer builtString = new StringBuffer(allValues);
		
		status.setText("Sensor: [" + Integer.toString(sensor) + "]");
		Log.i(TAG, "Sensor: [" + Integer.toString(sensor) + "]");
		
    for (entry = (allValues - 1); entry >= 0; entry--){
    	builtString.append(values[entry]);
    }
		
    Log.i(TAG, "CoOrds:" + builtString );
		coOrds.setText(builtString);
	}

}
