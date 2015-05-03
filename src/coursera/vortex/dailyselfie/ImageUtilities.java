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
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ImageUtilities {
	
	private static final String TAG = "ImageUtilities";
	
	public static final String IMAGE_PATHS_FILE = "SeflieImagesFilePaths.txt";
	
	// http://developer.android.com/training/camera/photobasics.html#TaskPath
	@SuppressLint("SimpleDateFormat")
	public static File createImageFile() throws IOException {
		
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    
	    File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + 
	    							File.separator + "DailySelfie" + File.separator);
	    storageDir.mkdir();
	    	    
	    Log.i(TAG, "Path: " + storageDir.getAbsolutePath());
	    File image = File.createTempFile(imageFileName,  ".jpg", storageDir);

	    return image;
	}
	
	public static boolean removeImageFile(String filePath) {
		
		File file = new File(filePath);
		return file.delete();
	}

	// http://developer.android.com/training/camera/photobasics.html#TaskScalePhoto
	public static Bitmap setPic(String absolutePhotoPath) {
	    // TODO: get the dimensions of the View
	    int targetW = 80; //mImageView.getWidth();
	    int targetH = 80; //mImageView.getHeight();
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
	public static ImageListAdapter loadItems(ImageListAdapter adapter, Context context) {
		
		BufferedReader reader = null;
		String filePath = null;
		
		try {
			FileInputStream fis = context.openFileInput(IMAGE_PATHS_FILE);
			reader = new BufferedReader(new InputStreamReader(fis));

			while (null != (filePath = reader.readLine())) {

				adapter.add(new ImageItem(filePath, setPic(filePath)));
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
		
		return adapter;
	}
	
	// Save SelfieImages to file
	public static void saveItems(ImageListAdapter adapter, Context context) {
		PrintWriter writer = null;
		try {
			FileOutputStream fos = context.openFileOutput(IMAGE_PATHS_FILE, Context.MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));

			for (int idx = 0; idx < adapter.getCount(); idx++) {
				
				writer.println(((ImageItem) adapter.getItem(idx)).getFilePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.close();
			}
		}
	}
}
