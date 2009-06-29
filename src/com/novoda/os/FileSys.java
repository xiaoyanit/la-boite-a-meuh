package com.novoda.os;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

public class FileSys {
	
	protected static final String	TAG								= "[NovodaFileSys]:";
	
	public static String createFilenameWithChecks(String dir, CharSequence title, String extension) {
		(new File(dir)).mkdirs();

		// Turn the title into a filename
		String filename = "";
		for (int i = 0; i < title.length(); i++) {
			if (Character.isLetterOrDigit(title.charAt(i))) {
				filename += title.charAt(i);
			}
		}

		// Try to make the filename unique
		String path = null;
		for (int i = 0; i < 100; i++) {
			String testPath;
			if (i > 0)
				testPath = dir + "/" + filename + i + extension;
			else
				testPath = dir + "/" + filename + extension;

			try {
				RandomAccessFile f = new RandomAccessFile(new File(testPath), "r");
			} catch (Exception e) {
				// Good, the file didn't exist
				path = testPath;
				Log.i(TAG, "Created new dir for saved files");
				break;
			}
		}

		return path;
	}
    
	//Used to retain header information during a copy
    public static void copyViaChannels(File src, File dst) throws IOException {

    	Log.i(TAG, "Copying "+ src.getAbsolutePath() + "to " + dst.getAbsolutePath());
    	
	    try {
	        FileChannel srcChannel = new FileInputStream(src.getAbsolutePath()).getChannel();
	        FileChannel dstChannel = new FileOutputStream(dst.getAbsolutePath()).getChannel();
	    
	        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
	    
	        srcChannel.close();
	        dstChannel.close();
	    } catch (IOException e) {
	    }
    }
    
	public static ArrayList<File> listFilesInDir(String dir) {
		
		List<File> files = Arrays.asList(new File(dir).listFiles());
		ArrayList<File> allFiles = new ArrayList<File>();
		for (File file: files){
			allFiles.add(file);
		}
		
		return allFiles;
	}

    
}
