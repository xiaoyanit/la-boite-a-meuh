package com.novoda.os;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

public class FileSys {
	
	private static final String	TAG								= "[FileSys]:";
	/**
	 * Helper for dealing with exceptions in creating files any
	 * conventions/logic which may pertain to the files.
	 * Makes sure that a filename is unique and also make sure 
	 * that it has appropriate letters/digits in the title.
	 * 
	 * @param dir
	 * @param title
	 * @return
	 */
	public static String createFilenameWithChecks(String dir, CharSequence title) {
		(new File(dir)).mkdirs();

		String ext = getExtensionFromFilename(title.toString());
		String justFileName = title.toString().substring(0, title.toString().indexOf(ext));
		
		
		// Turn the title into a filename
		String filename = "";
		for (int i = 0; i < justFileName.length(); i++) {
			if (Character.isLetterOrDigit(justFileName.charAt(i))) {
				filename += justFileName.charAt(i);
			}
		}
		
		// Try to make the filename unique
		String path = null;
		for (int i = 0; i < 100; i++) {
			String testPath;
			if (i > 0){
				testPath = dir + filename + i;
			}else{
				testPath = dir + filename;
			}
			
			try {
				new RandomAccessFile(new File(testPath), "r");
			} catch (IOException e) {
				Log.i(TAG, "A checked filename testpath does not exist and can be used");
				path = testPath;
				break;
			}
		}
		
		return (path + ext);
	}
    
	/***
	 * Retains header information during a copy
	 * @param src
	 * @param dst
	 */
    public static void copyViaChannels(File src, File dst) {

    	Log.i(TAG, "Copying "+ src.getAbsolutePath() + " to " + dst.getAbsolutePath());
    	
    	try {
			Log.i(TAG, "Creating a new file: " + dst.createNewFile());
		} catch (IOException e) {
			Log.e(TAG, "Could not create destination file [" + dst.getAbsolutePath() + "]", e);
		}
    	
	    try {
	        FileChannel srcChannel = new FileInputStream(src.getAbsolutePath()).getChannel();
	        FileChannel dstChannel = new FileOutputStream(dst.getAbsolutePath()).getChannel();
	    
	        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
	    
	        srcChannel.close();
	        dstChannel.close();
	    } catch (IOException e) {
	    	Log.e(TAG, "Problem in copying files["+src.getAbsolutePath()+"] to ["+dst.getAbsolutePath()+"]", e);
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
	
	public static ArrayList<String> listFileNamesInDir(String dir) {
		
		List<File> files = Arrays.asList(new File(dir).listFiles());
		ArrayList<String> allFiles = new ArrayList<String>();
		for (File file: files){
			allFiles.add(file.getName());
		}
		
		return allFiles;
	}
	
    public static String getExtensionFromFilename(String filename) {
        return filename.substring(filename.lastIndexOf('.'),filename.length());
    }
    
    public static String getFilenameWithoutExtension(String filename) {
        return filename.substring(0 ,filename.lastIndexOf('.'));
    }
    

    public static String copyInputStreamToFile(InputStream in, File dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);
    
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        
        return dst.getAbsolutePath();
    }}
