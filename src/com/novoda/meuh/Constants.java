package com.novoda.meuh;

import android.content.Intent;

public class Constants {
	public static final int		COLUMN_AUDIO_URI_ID				= 0;
	public static final int		COLUMN_RELATIVE_FILE_LOCATION	= 1;
	public static final int		COLUMN_FILENAME					= 2;
	public static final int		COLUMN_INDEX4					= 2;
	public static final int		COLUMN_INDEX5					= 2;
	public static final String	AUDIO_FILES_DIR					= "/sdcard/la-boite-a-meuh/media/audio/meuhs";
	public static final int		PICK_SOUND_REQUEST				= 33;
	public static final String	PICK_SOUND						= "com.novoda.meuh.action.PICK_SOUND";
	public static final String	PICKED_AUDIO_FILE_POSITION		= null;
	public static final Intent	CHOSEN_AUDIO_FILE				= null;
	public static final int		CHOSEN_AUDIO_FILE_ACTION		= 40;
}