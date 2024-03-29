
package com.novoda.moo.view;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.moo.R;
import com.novoda.moo.os.FileSys;


public class AudioFileListAdapter extends BaseAdapter {

    public ArrayList<File> files;

    private final Activity activity;

    public AudioFileListAdapter(Activity activity, ArrayList<File> files) {
        this.activity = activity;
        this.files = files;
    }

    public int getCount() {
        return this.files.size();
    }

    public File getItem(int position) {
        return this.files.get(position);
    }

    public long getItemId(int position) {
        return this.files.hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AudioFileListAdapterView todayView = new AudioFileListAdapterView(activity, files
                .get(position));
        return todayView;
    }

    /***
     * This calc will not be needed when the URI based interaction is timeCalc
     * is approx (frameSize-1024 * mDefaultRate-22050 ) TODO: Once all file
     * access is done via URI the duration can be obtained form the MEDIA_DB
     */
    public class AudioFileListAdapterView extends LinearLayout {

        public AudioFileListAdapterView(Activity activity, File fileItem) {
            super(activity);
            addView(createListItem(activity, fileItem));
        }
    }

    private View createListItem(Activity activity, File fileItem) {

        final TextView mFileName;
        final TextView mFileLength;
        final double timeCalc = 1240.0; // bitrate +header

        LayoutInflater factory = LayoutInflater.from(activity);
        final View itemInListView = factory.inflate(R.layout.row_item_file, null);

        mFileName = (TextView)itemInListView.findViewById(R.id.row_title);
        mFileName.setText(FileSys.getFilenameWithoutExtension(fileItem.getName()));
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);

        mFileLength = (TextView)itemInListView.findViewById(R.id.row_item_length);
        
        if(fileItem.getName().equals("moo1.wav")){
        	mFileLength.setText("3secs");	
        }
        
        if(fileItem.getName().equals("moo2.wav")){
        	mFileLength.setText("5secs");
        }
        
        if(!fileItem.getName().equals("moo1.wav") && !fileItem.getName().equals("moo2.wav")){
        	mFileLength.setText(nf.format(new Double(fileItem.length() / timeCalc)) + "secs");
        }
        
        
        return itemInListView;
    }

}
