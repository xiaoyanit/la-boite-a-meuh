package com.novoda.meuh;

import java.io.File;
import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

import com.novoda.os.FileSys;
import com.novoda.view.FileAdapter;

public class MooRecorder extends ListActivity {

	protected static final String	TAG	= "[MooRecord]:";
	private Button					startRecording;
	private FileAdapter				fileListAdapter;

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

		fileListAdapter = new FileAdapter(this, FileSys.listFilesInDir(Constants.AUDIO_FILES_DIR));
		this.setListAdapter(fileListAdapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				MooRecorder.this.finish();
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
		// mRecordedAudio_uri = data.getData();
		// mRecordedAudio_uri_id = cursor.getString(COLUMN_AUDIO_URI_ID);
		// mRecordedAudio_absLoc =
		// cursor.getString(COLUMN_RELATIVE_FILE_LOCATION);
		// mRecordedAudio_name = cursor.getString(COLUMN_FILENAME);

		try {
			String path = FileSys.createFilenameWithChecks(Constants.AUDIO_FILES_DIR, "user_meuh", ".3gpp");
			FileSys.copyViaChannels(new File(cursor.getString(Constants.COLUMN_RELATIVE_FILE_LOCATION)), new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
