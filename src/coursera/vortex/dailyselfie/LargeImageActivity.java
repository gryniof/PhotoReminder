package coursera.vortex.dailyselfie;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class LargeImageActivity extends FragmentActivity {

	private static final String TAG = "LargeImageActivity";
	
    LargeImagePagerAdapter mLargeImagePagerAdapter;
    ViewPager mViewPager;
    int mImgPosition;
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_fullscreen_swipe_view);
    	
    	if ((mImgPosition = getIntent().getIntExtra("imgPosition", -1)) != -1) {
    		
    		Log.i(TAG, "imgPosition: " + mImgPosition);
			
			// ViewPager and its adapters use support library
	    	// fragments, so use getSupportFragmentManager.
    		mLargeImagePagerAdapter = new LargeImagePagerAdapter(getSupportFragmentManager(), this);

	    	mViewPager = (ViewPager) findViewById(R.id.pager);
	    	mViewPager.setAdapter(mLargeImagePagerAdapter);
	    	
	    	// Set current item to the saved in the Bundle OR
	    	// one user selected (passed in the intent)
	    	if (savedInstanceState != null) {
	    		mViewPager.setCurrentItem(savedInstanceState.getInt("currentItem"), true);
	    	} else {
	    		mViewPager.setCurrentItem(mImgPosition, true);
	    	}
	    	
		} else {
			
			//TODO: Consider displaying an error screen here.
			Log.e(TAG, "Unable to show large images.");
		}
    }
    
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putInt("currentItem", mViewPager.getCurrentItem());
    }
}