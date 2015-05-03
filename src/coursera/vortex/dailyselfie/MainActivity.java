package coursera.vortex.dailyselfie;

import java.io.File;
import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;

public class MainActivity extends ActionBarActivity {

	private static final String TAG = "MainActivity";
	
	static final int REQUEST_TAKE_PHOTO = 1;
	static final int REQUEST_CHANGE_SETTINGS = 2;
	
	public static final String IMAGE_POSITION = "imgPosition";
	
	private static final String SAVED_PHOTO_PATH = "SavedPhotoPath";
	
	private static String mAbsolutePhotoPath;
	private static String mSavedPhotoPath;
	
	//private ImageView mImageView;
	private static ImageListAdapter mAdapter;
	
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
		} else if (id == R.id.settings) {
			openSettings();
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
	            photoFile = ImageUtilities.createImageFile();
	    	    mAbsolutePhotoPath = photoFile.getAbsolutePath();
	        } catch (IOException ex) {
	            Log.e(TAG, "dispatchTakePictureIntent(): Error occured while creating the file.");
	        }
	        
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	        	
	        	Log.i(TAG, "Put extra param: " + Uri.fromFile(photoFile));
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	private void openSettings() {
		Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
		startActivity(settingsIntent);
	}
	
	// http://developer.android.com/training/camera/photobasics.html#TaskPhotoView
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
	        
	    	// This is a bit of a hack forced by a configuration change after returning
	    	// from the camera app. TODO: loadItems called twice seems like it's asking for trouble.
	    	// Possible to move this to onResume() somehow? Prolly not cause this is called before onResume(). 
   	
	    	if (mAbsolutePhotoPath == null) {
	    		Log.i(TAG, "Absolute path is NULL. Saved path: " + mSavedPhotoPath);
	    		mAbsolutePhotoPath = mSavedPhotoPath;
    		}
	    	
	    	if (mAdapter.getCount() == 0) {
	    		mAdapter = ImageUtilities.loadItems(mAdapter, this);
	    	}
	    	
	        Bitmap bitmap = ImageUtilities.setPic(mAbsolutePhotoPath); 
	        ImageItem newItem = new ImageItem(mAbsolutePhotoPath, bitmap);
	        
	        mAdapter.add(newItem);
	        
	    } else {
	    	Log.i(TAG, "onActivityResult(), resultCode not RESULT_OK.");
	    }
	}
	
			
	//Based on: http://stackoverflow.com/questions/20524008/combining-listactivity-and-actionbaractivity
	public static class ImageListFragment extends ListFragment {
		
		static final int DISMISS_VALUE = 1;
		Fragment f = this;
		
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            mAdapter = new ImageListAdapter(getActivity().getApplicationContext());
            setListAdapter(mAdapter);
            
            getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            	//getParentFragment() requires API 17
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                	
                	DialogFragment dialog = DeleteImageItemDialogFragment.newInstance(arg2);                	
        			dialog.show(getFragmentManager(), "DeleteDialog");
        			dialog.setTargetFragment(f, DISMISS_VALUE);
        			
                    return true;
                }
            });
        }
        
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        	super.onActivityResult(requestCode, resultCode, data);
        	
        	int itemToRemove = data.getIntExtra("numToDelete", -1);
        	
        	Log.i(TAG, "Removing item from list: " + itemToRemove);
        	
        	//Delete the image file
        	ImageUtilities.removeImageFile(((ImageItem) mAdapter.getItem(itemToRemove)).getFilePath());
        	//Remove the file from the adapter
        	mAdapter.removeItem(itemToRemove);
        }
        
        @Override
    	public void onResume() {
    		super.onResume();

    		// Load saved ImageItems, if necessary
    		if (mAdapter.getCount() == 0) {
    			
    			Log.i(TAG, "onResume(). Load items in adapter: " + mAdapter.getCount());
    			mAdapter = ImageUtilities.loadItems(mAdapter, (MainActivity) getActivity());
    		}
    	}

    	@Override
    	public void onPause() {
    		super.onPause();

    		Log.i(TAG, "onPause(). Save items in adapter: " + mAdapter.getCount());
    		ImageUtilities.saveItems(mAdapter, (MainActivity) getActivity());
    	}
    	
    	@Override
        public void onListItemClick(ListView lv, View v, int position, long id) {
    		
    		String filePath = ((ImageItem) getListView().getItemAtPosition(position)).getFilePath();
    		Log.i(TAG, "Image item selected: " + filePath);
    		
    		Intent intentOpenLargeImage = new Intent(getActivity(), LargeImageActivity.class);
    		intentOpenLargeImage.putExtra(IMAGE_POSITION, position);
    		startActivity(intentOpenLargeImage);
    	}
    }	
}
