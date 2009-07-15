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
	
	protected static final String	TAG								= "[FileSys]:";
	
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
				testPath = dir + filename + i + extension;
			else
				testPath = dir + filename + extension;

			try {
				new RandomAccessFile(new File(testPath), "r");
			} catch (Exception e) {
				// Good, the file didn't exist
				path = testPath;
				Log.i(TAG, "Created new dir for saved files");
				break;
			}
		}

		return path;
	}
	
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
			if (i > 0)
				testPath = dir + filename + i;
			else
				testPath = dir + filename;
			
			try {
				new RandomAccessFile(new File(testPath), "r");
			} catch (Exception e) {
				// Good, the file didn't exist
				path = testPath;
				Log.i(TAG, "Created new dir for saved files");
				break;
			}
		}
		
		return path + ext;
	}
    
	//Used to retain header information during a copy
    public static void copyViaChannels(File src, File dst) throws IOException {

    	Log.i(TAG, "Copying "+ src.getAbsolutePath() + " to " + dst.getAbsolutePath());
    	Log.i(TAG, "Creating a new file: " + dst.createNewFile());
    	
	    try {
	        FileChannel srcChannel = new FileInputStream(src.getAbsolutePath()).getChannel();
	        FileChannel dstChannel = new FileOutputStream(dst.getAbsolutePath()).getChannel();
	    
	        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
	    
	        srcChannel.close();
	        dstChannel.close();
	    } catch (IOException e) {
	    	Log.e(TAG, "IO Exception in copying between channeks", e);
	    }
    }
    
	public static ArrayList<File> listFilesInDir_asFiles(String dir) {
		
		List<File> files = Arrays.asList(new File(dir).listFiles());
		ArrayList<File> allFiles = new ArrayList<File>();	
		for (File file: files){
			allFiles.add(file);
		}
		
		return allFiles;
	}
	
	public static ArrayList<String> listFilesInDir_asStrings(String dir) {
		
		List<File> files = Arrays.asList(new File(dir).listFiles());
		ArrayList<String> allFiles = new ArrayList<String>();
		for (File file: files){
			allFiles.add(file.getName());
		}
		
		return allFiles;
	}
	
	public static String[] fileNamesInDir(String dir) {
		
		List<File> files = Arrays.asList(new File(dir).listFiles());
		String[] fileNames = new String[files.size()];
		for (int i =0; i > 0; i++){
			
			Log.i("filesys", "Name: " + files.get(i).getName());
			Log.i("filesys", "Abs Name: " + files.get(i).getAbsolutePath());
			Log.i("filesys", "Path: " + files.get(i).getPath());
			
			fileNames[i] = files.get(i).getName();
		}
		
		return fileNames;
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
    }


    
}
