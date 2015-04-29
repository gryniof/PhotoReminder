package coursera.vortex.dailyselfie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.PendingIntent;
import android.os.SystemClock;

public class MainActivity extends ActionBarActivity {

	static final int REQUEST_TAKE_PHOTO = 1;
	private static final String FILE_NAME = "SeflieImagesFilePaths.txt";
	private static final String SAVED_PHOTO_PATH = "SavedPhotoPath";
	private static final String TAG = "MainActivity";
	
	private static String mAbsolutePhotoPath;
	private static String mSavedPhotoPath;
	
	private ImageView mImageView;
	private static ImageListAdapter mAdapter;
	
	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent; //, mLoggerReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent; //, mLoggerReceiverPendingIntent;
	private static final long INITIAL_ALARM_DELAY = 20 * 1000L;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			mSavedPhotoPath = savedInstanceState.getString(SAVED_PHOTO_PATH);
		}
		
		// TODO: figure out if getApplicationContext() is appropriate for this
//		mAdapter = new ImageListAdapter(getApplicationContext());
//		mImageView = (ImageView) findViewById(R.id.thumbnailView);
		
        // TODO: figure out diff between android.app.FM and android.support.v4.app.FM
        FragmentManager fm = getFragmentManager();  
        
        if (fm.findFragmentById(android.R.id.content) == null) {  
			ImageListFragment list = new ImageListFragment();  
			fm.beginTransaction().add(android.R.id.content, list).commit();  
        }
         
		// Get the AlarmManager Service
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		// Create an Intent to broadcast to the AlarmNotificationReceiver
		mNotificationReceiverIntent = new Intent(MainActivity.this, AlarmNotificationReceiver.class);
		
		// Create an PendingIntent that holds the NotificationReceiverIntent
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, mNotificationReceiverIntent, 0);
		
		// Set repeating alarm
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
				AlarmManager.INTERVAL_FIFTEEN_MINUTES,
				mNotificationReceiverPendingIntent);
        
    	// Cancel all alarms using mNotificationReceiverPendingIntent
    	// mAlarmManager.cancel(mNotificationReceiverPendingIntent);
    	// Cancel all alarms using mLoggerReceiverPendingIntent
    	// mAlarmManager.cancel(mLoggerReceiverPendingIntent);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   super.onSaveInstanceState(outState);
	   outState.putString(SAVED_PHOTO_PATH, mAbsolutePhotoPath);
	}
	
	@Override
	public void onRestoreInstanceState (Bundle inState) {
		super.onRestoreInstanceState(inState);
		mSavedPhotoPath = inState.getString(SAVED_PHOTO_PATH);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		
		if (id == R.id.action_camera) {
			dispatchTakePictureIntent();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
	// http://developer.android.com/training/camera/photobasics.html#TaskCaptureIntent
	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	
	    	// Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	        }
	        
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	        	
	        	Log.i(TAG, "Put extra param: " + Uri.fromFile(photoFile));
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	// http://developer.android.com/training/camera/photobasics.html#TaskPath
	@SuppressLint("SimpleDateFormat")
	protected File createImageFile() throws IOException {
		
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    
	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(imageFileName,  ".jpg", storageDir);

	    // Save a file: path for use with ACTION_VIEW intents
	    mAbsolutePhotoPath = image.getAbsolutePath();
	    
	    return image;
	}
	
	// http://developer.android.com/training/camera/photobasics.html#TaskPhotoView
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
	        
	    	// This is a bit of a hack forced by a configuration change after returning
	    	// from the camera app. TODO: Move this to onResume() somehow. loadItems called twice
	    	// is asking for trouble.
	    	// if (mAdapter.getCount() == 0 && !mSavedPhotoPath.equals("default")) {
	    	
//*****    	THIS WORKS:
//	    	if (mAbsolutePhotoPath == null || mAdapter.getCount() == 0) {
//	    		
//	    		if (mAbsolutePhotoPath == null) {
//	    			
//		    		Log.i(TAG, "Absolute path is NULL. Saved path: " + mSavedPhotoPath);
//		    		mAbsolutePhotoPath = mSavedPhotoPath;
//	    		}
//	    		loadItems();
//*****	    }

	    	if (mAbsolutePhotoPath == null) {
	    		Log.i(TAG, "Absolute path is NULL. Saved path: " + mSavedPhotoPath);
	    		mAbsolutePhotoPath = mSavedPhotoPath;
    		}
	    	
	    	if (mAdapter.getCount() == 0) {
	    		loadItems();
	    	}
	    	
	    	
	        Bitmap bitmap = setPic(mAbsolutePhotoPath); 
	        ImageItem newItem = new ImageItem(mAbsolutePhotoPath, bitmap);
	        
	        mAdapter.add(newItem);
	        
	    } else {
	    	Log.i(TAG, "Camera activity did NOT return correctly.");
	    }
	}
	
	// http://developer.android.com/training/camera/photobasics.html#TaskScalePhoto
	private Bitmap setPic(String absolutePhotoPath) {
	    // TODO: get the dimensions of the View
	    int targetW = 70; //mImageView.getWidth();
	    int targetH = 70; //mImageView.getHeight();
	    Bitmap bitmap = null;
	    
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(absolutePhotoPath, bmOptions);
	    
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    //bmOptions.inBitmap TODO: figure out this line and why BM.decodeFile is called twice in this fun.

	    if ((bitmap = BitmapFactory.decodeFile(absolutePhotoPath, bmOptions)) == null) {
	    	Log.i(TAG, "File could not be decoded.");
	    }
	    
	    return bitmap;
	}
	
	// Another way doing it:
	// http://stackoverflow.com/questions/5871482/serializing-and-de-serializing-android-graphics-bitmap-in-java
	// Bitmaps are not serializable so the bitmap needs to be wrapped in a serializable class that
	// converts the bitmap into a byte output stream.
	
   	// Load stored SelfieImages
	private void loadItems() {
		
		BufferedReader reader = null;
		String filePath = null;
		
		try {
			FileInputStream fis = openFileInput(FILE_NAME);
			reader = new BufferedReader(new InputStreamReader(fis));

			while (null != (filePath = reader.readLine())) {

				mAdapter.add(new ImageItem(filePath, setPic(filePath)));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// Save SelfieImages to file
	private void saveItems() {
		PrintWriter writer = null;
		try {
			FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));

			for (int idx = 0; idx < mAdapter.getCount(); idx++) {
				
				writer.println(((ImageItem) mAdapter.getItem(idx)).getFilePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.close();
			}
		}
	}
		
	//Based on: http://stackoverflow.com/questions/20524008/combining-listactivity-and-actionbaractivity
	public static class ImageListFragment extends ListFragment {
		
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            mAdapter = new ImageListAdapter(getActivity().getApplicationContext());
            setListAdapter(mAdapter);
        }
        
        @Override
    	public void onResume() {
    		super.onResume();

    		// Load saved ImageItems, if necessary
    		if (mAdapter.getCount() == 0) {
    			
    			Log.i(TAG, "onResume(). Load items in adapter: " + mAdapter.getCount());
    			((MainActivity) getActivity()).loadItems();
    		}
    	}

    	@Override
    	public void onPause() {
    		super.onPause();

    		Log.i(TAG, "onPause(). Save items in adapter: " + mAdapter.getCount());
    		((MainActivity) getActivity()).saveItems();
    	}
        
        public void onListItemClick(ListView lv, View v, int position, long id) {
    		
    		String filePath = ((ImageItem) getListView().getItemAtPosition(position)).getFilePath();
    		Log.i(TAG, "Image item selected: " + filePath);
    		
    		Intent intentOpenLargeImage = new Intent(getActivity(), LargeImageActivity.class);
    		intentOpenLargeImage.putExtra("savedFilePath", FILE_NAME);
    		intentOpenLargeImage.putExtra("imgPosition", position);
    		startActivity(intentOpenLargeImage);
    	}
    }	
}
