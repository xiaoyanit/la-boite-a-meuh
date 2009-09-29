/**********************************************
 * 
 *  la-boite-a-meuh
 *  :SoundPoolMgr
 * 
 *  When called upon it plays back stored 
 *  files at a requested rate.
 * 
 * 
 *  http://www.novoda.com/blog
 * 
 */
package com.novoda.moo.media;

import java.util.HashMap;

import com.novoda.moo.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;


public class SoundPoolMgr {
	private static final String			TAG							= "[SoundPoolMgr]:";

	public static final int				MOO_SOUND_1					= 1;
	public static final int				MOO_SOUND_2					= 2;
	public static final int				CUSTOM_MOO_SOUND			= 3;

	private boolean						mEnabled					= true;
	private Context						mContext;
	private SoundPool					mSoundPool;
	private HashMap<Integer, Integer>	mSoundPoolMap;

	public static int					SELECTED_MOO_SOUND;
	public static String				SELECTED_MOO_FILE			= null;
	public static String				URI_currently_selected_file	= null;

	public SoundPoolMgr(Context context) {
		this.mContext = context;

		if (SELECTED_MOO_FILE == null) {
			SELECTED_MOO_SOUND = MOO_SOUND_1;
		}
	}

	public void init() {
		if (mEnabled) {
			Log.d(TAG, "Initializing new SoundPool");

			release();
			mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 100);
			mSoundPoolMap = new HashMap<Integer, Integer>();
			mSoundPoolMap.put(MOO_SOUND_1, mSoundPool.load(mContext, R.raw.defaultsound1, 1));
			mSoundPoolMap.put(MOO_SOUND_2, mSoundPool.load(mContext, R.raw.defaultsound2, 1));

			if (SELECTED_MOO_SOUND == CUSTOM_MOO_SOUND) {
				mSoundPoolMap.put(CUSTOM_MOO_SOUND, mSoundPool.load(SELECTED_MOO_FILE, 1));
			}

			Log.d(TAG, "SoundPool initialized");
		}
	}

	/***
	 * The Sound pool needs to be continually reinitialised after use.
	 */
	public void release() {
		if (mSoundPool != null) {
			Log.d(TAG, "Closing SoundPool");
			mSoundPool.release();
			mSoundPool = null;
			Log.d(TAG, "SoundPool closed");
			return;
		}
	}

	public void playSound(float speed) {
		if (mSoundPool != null) {
			Log.d(TAG, "Playing Sound [" + SELECTED_MOO_SOUND + "]");
			AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mSoundPoolMap.get(SELECTED_MOO_SOUND) != null) {
				mSoundPool.play(mSoundPoolMap.get(SELECTED_MOO_SOUND), streamVolume, streamVolume, 1, 0, speed);
			}
		}
	}

	public void setEnabled(boolean enabled) {
		this.mEnabled = enabled;
	}

}