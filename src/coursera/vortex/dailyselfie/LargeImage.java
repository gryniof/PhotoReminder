package coursera.vortex.dailyselfie;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class LargeImage extends Activity {
	
	private static final String TAG = "LargeImageActivity";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_large_image);
		
		String fileName;
		
		if ((fileName = getIntent().getStringExtra("file path")) != null) {
			Log.i(TAG, "File path passed in intent.");
			
			File imgFile = new File(fileName);
			
			if(imgFile.exists()) {
				Log.i(TAG, "Image exists.");
				
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

			    ImageView myImage = (ImageView) findViewById(R.id.largeImageView);

			    myImage.setImageBitmap(myBitmap);
			}
		}
	}
}
