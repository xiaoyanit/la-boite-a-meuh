package com.novoda.meuh.media;

import java.util.HashMap;

import com.novoda.meuh.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class SoundPoolMgr {
    private static final String TAG = "SoundPoolSoundManager";

    public static final int SOUND_1 = 1;

    private boolean enabled = true;
    private Context context;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
	private static final int	DEFAULT_RATE	= 44100;

    public SoundPoolMgr(Context context) {
            this.context = context;
    }

    public void reInit() {
            init();
    }

    public void init() {
            if (enabled) {
                    Log.d(TAG, "Initializing new SoundPool");
                    //re-init sound pool to work around bugs
                    release();
                    soundPool = new SoundPool(2,AudioManager.STREAM_MUSIC, 100);
                    soundPoolMap = new HashMap<Integer, Integer>();
                    soundPoolMap.put(SOUND_1, soundPool.load(context, R.raw.carlthecow, 1));
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

    public void playSound(double speed) { 	
    	
            if (soundPool != null) {
                    Log.d(TAG, "Playing Sound " + 1);
                    AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                    int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                    Integer soundId = soundPoolMap.get(1);
                    if (soundId != null) {
                            soundPool.play(soundPoolMap.get(1), streamVolume,streamVolume, 1, 0, 1.4f);
                    }
            }
    }

    public void setEnabled(boolean enabled) {
            this.enabled = enabled;
    }

} 