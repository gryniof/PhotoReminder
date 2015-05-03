package coursera.vortex.dailyselfie;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

//Using a FragmentStatePagerAdapter instead of a FragmentPagerAdapter because FSPA destroys 
//fragments that aren't visible (might cache some). FPA will store all visited fragments.
public class LargeImagePagerAdapter extends FragmentStatePagerAdapter {
	
	private static final String TAG = "LargeImagePagerAdapter";
	
	private List<ImageItem> mItems = new ArrayList<ImageItem>();
	private Context mContext;
	
	public LargeImagePagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		
		mContext = context;
		loadItems();
	}

	public void add(ImageItem item) {

		mItems.add(item);
		notifyDataSetChanged();
	}
		
	@Override
	public Fragment getItem(int i) {
		Log.i(TAG, "getItem() called with position: " + i);
		
		Fragment fragment = new LargeImageFragment(this);
	    Bundle args = new Bundle();
	    args.putInt(LargeImageFragment.ARG_OBJECT, i);
	    fragment.setArguments(args);
	    
	    return fragment;
	}

	@Override
	public int getCount() {
	    return mItems.size();
	}

	public List<ImageItem> getItems() {
		return mItems;
	}
	
    // Load stored SelfieImages
	private void loadItems() {
		
		BufferedReader reader = null;
		String filePath = null;
		
		try {
			FileInputStream fis = mContext.openFileInput(MainActivity.IMAGE_PATHS_FILE);
			reader = new BufferedReader(new InputStreamReader(fis));

			while (null != (filePath = reader.readLine())) {

				this.add(new ImageItem(filePath, null));
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
	
	//TODO: possible to use a ViewHolder patters like in the ImageListAdapter?
}
