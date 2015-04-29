package coursera.vortex.dailyselfie;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class LargeImageFragment extends Fragment {

	private static final String TAG = "LargeImageFragment";
	// TODO: this class was initially static. Was this a good idea?
	// Instances of this class are fragments representing a single large image in the collection.
    public static final String ARG_OBJECT = "large_image";
    private int mImgPos = -1;

    private List<ImageItem> mItems;
    
    public LargeImageFragment(LargeImagePagerAdapter adapter) {
    	mItems = adapter.getItems();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Retain this fragment across configuration changes.
      setRetainInstance(true);
    }
	    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.fullscreen_image, container, false);
        Bundle args = getArguments();
        mImgPos = args.getInt(ARG_OBJECT);
        
        Log.i(TAG, "onCreateView() called on img num: " + mImgPos);
        
        Bitmap largeBitmap = BitmapFactory.decodeFile(mItems.get(mImgPos).getFilePath());
        ((ImageView) rootView.findViewById(R.id.largeImageView)).setImageBitmap(largeBitmap);
        
        return rootView;
    }
//	    
//    public void onDestroyView() {
//    	super.onDestroyView();
//    	setRetainInstance(false);
//    	
//    	Log.i(TAG, "onDestroyView() called on img num: " + mImgPos);
//    }
}
