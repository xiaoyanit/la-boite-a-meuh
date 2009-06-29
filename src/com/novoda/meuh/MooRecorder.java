package com.novoda.meuh;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.novoda.os.FileSys;

public class MooRecorder extends Activity {

	protected static final String	TAG								= "[MooRecord]:";
	private Button					startRecording;

	public static final int			COLUMN_AUDIO_URI_ID				= 0;
	public static final int			COLUMN_RELATIVE_FILE_LOCATION	= 1;
	public static final int			COLUMN_FILENAME					= 2;
	public static final int			COLUMN_INDEX4					= 2;
	public static final int			COLUMN_INDEX5					= 2;
	private static final String		audio_dir						= "/sdcard/la-boite-a-meuh/media/audio/meuhs";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiomgr);

		startRecording = (Button) findViewById(R.id.startrecording);

		startRecording.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					startActivityForResult(new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION), 1);
				} catch (Exception ee) {
					Log.e(TAG, "Caught io exception " + ee.getMessage());
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, data.toURI());
		Intent intent = getIntent();
		Log.i(TAG, intent.toString());

		Cursor cursor = managedQuery(Uri.parse(data.toURI()), null, null, null, null);
		startManagingCursor(cursor);

		cursor.moveToLast();
//		mRecordedAudio_uri = data.getData();
//		mRecordedAudio_uri_id = cursor.getString(COLUMN_AUDIO_URI_ID);
//		mRecordedAudio_absLoc = cursor.getString(COLUMN_RELATIVE_FILE_LOCATION);
//		mRecordedAudio_name = cursor.getString(COLUMN_FILENAME);
		
		try {
			String path = FileSys.createFilenameWithChecks(audio_dir, "kevins_new_moo", ".3gpp");
			FileSys.copyViaChannels(new File(cursor.getString(COLUMN_RELATIVE_FILE_LOCATION)), new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




}
