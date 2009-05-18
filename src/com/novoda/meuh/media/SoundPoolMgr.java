package com.novoda.meuh.media;

import java.util.HashMap;

import com.novoda.meuh.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class SoundPoolMgr {
	private static final String			TAG				= "SoundPoolSoundManager";

	public static final int				MOO_SOUND_1		= 1;
	public static final int				MOO_SOUND_2		= 2;

	private boolean						enabled			= true;
	private Context						context;
	private SoundPool					soundPool;
	private HashMap<Integer, Integer>	soundPoolMap;

	public static int					SELECTED_MOO_SOUND;
	private static final int			DEFAULT_RATE	= 44100;

	public SoundPoolMgr(Context context) {
		this.context = context;
		SELECTED_MOO_SOUND = MOO_SOUND_1;
	}

	public void reInit() {
		init();
	}

	public void init() {
		if (enabled) {
			Log.d(TAG, "Initializing new SoundPool");
			// re-init sound pool to work around bugs
			release();
			soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
			soundPoolMap = new HashMap<Integer, Integer>();
			soundPoolMap.put(MOO_SOUND_1, soundPool.load(context, R.raw.carlthecow, 1));
			soundPoolMap.put(MOO_SOUND_2, soundPool.load(context, R.raw.kevinthecow, 1));
			Log.d(TAG, "SoundPool initialized");
		}
	}

	public void release() {
		if (soundPool != null) {
			Log.d(TAG, "Closing SoundPool");
			soundPool.release();
			soundPool = null;
			Log.d(TAG, "SoundPool closed");
			return;
		}
	}

	public void playSound(float speed) {

		if (soundPool != null) {
			Log.d(TAG, "Playing Sound " + 1);
			AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (soundPoolMap.get(SELECTED_MOO_SOUND) != null) {
				soundPool.play(soundPoolMap.get(SELECTED_MOO_SOUND), streamVolume, streamVolume, 1, 0, speed);
			}
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}