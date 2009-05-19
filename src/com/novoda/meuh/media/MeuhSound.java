/**********************************************
 * 
 *  la-boite-a-meuh
 *  :MeuhSound
 *  
 *  Alter the frequency of the replayed sound. 
 *  This only works with 44.1k Hz 16bit stereo PCM sound files.
 *    
 *    
 *  http://www.novoda.com/blog
 * 
 */
package com.novoda.meuh.media;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class MeuhSound {

	private static final int	DEFAULT_RATE	= 22050;

	private static final String	TAG				= "meuh:";

	// Have to be cast to int as we won't play huge files
	private int					fileLength;
	private InputStream			audioin;

	private byte[]				buffer;

	private AudioTrack			at;

	private boolean				first			= true;

	public MeuhSound(InputStream in, int size) throws FileNotFoundException {
		fileLength = size;
		audioin = in;
	}

	public static MeuhSound create(Context context, int resid) {
		try {
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(resid);
			if (afd == null)
				return null;
			Log.i(TAG, "size: " + afd.getLength());
			MeuhSound meuh = new MeuhSound(context.getResources().openRawResource(resid), (int) afd.getLength());
			afd.close();
			meuh.prepare();
			return meuh;
		} catch (IOException ex) {
			Log.d(TAG, "create failed:", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "create failed:", ex);
		} catch (SecurityException ex) {
			Log.d(TAG, "create failed:", ex);
		}
		return null;
	}

	private void prepare() {
		buffer = new byte[fileLength];
		Log.i(TAG, "file l: " + fileLength);
		try {
			audioin.read(buffer);
			audioin.close();
		} catch (IOException ex) {
			Log.e(TAG, "prepare failed: " + ex.getMessage());
		}
		at = new AudioTrack(AudioManager.STREAM_MUSIC, DEFAULT_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_DEFAULT, fileLength, AudioTrack.MODE_STATIC);
		at.write(buffer, 0, fileLength);
		at.flush();
	}

	public void play(double speed) {
		// Otherwise it plays the sound twice the first time
		if (!first) {
			at.stop();
			at.reloadStaticData();
		}
		at.setPlaybackRate((int) (DEFAULT_RATE * speed));
		first = false;
		at.play();
	}

	public void dispose() {
		at.release();
		buffer = null;
	}
}
