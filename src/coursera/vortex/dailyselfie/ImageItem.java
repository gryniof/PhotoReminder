package coursera.vortex.dailyselfie;

import java.io.Serializable;
import android.graphics.Bitmap;

public class ImageItem implements Serializable {
	
	// http://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	private static final long serialVersionUID = 4460694759015517444L;
	
	private String mFilePath;
	private Bitmap mThumbnail;

	public ImageItem(String filePath, Bitmap thumbnail) {
		mFilePath = filePath;
		mThumbnail = thumbnail;
	}
	
	public Bitmap getThumbnail() {
		
		return mThumbnail;
	}
	
	public void setThumbnail(Bitmap thumbnail) {
		mThumbnail = thumbnail;
	}
	
	public String getFileName() {
		
		return mFilePath;
	}
}
