package coursera.vortex.dailyselfie;

import android.graphics.Bitmap;

public class ImageItem {
	
	// http://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	private String mFilePath;
	private Bitmap mThumbnail;

	public ImageItem(String filePath, Bitmap thumbnail) {
		mFilePath = filePath;
		mThumbnail = thumbnail;
	}
	
	public String getFilePath() {
		
		return mFilePath;
	}
	
	public Bitmap getThumbnail() {
		
		return mThumbnail;
	}
	
	public void setThumbnail(Bitmap thumbnail) {
		
		mThumbnail = thumbnail;
	}
	
	//TODO: add error checking
	public String getDispFileName() {
		
		String fileName = "default"; 
		
		if (mFilePath != null) {
			
			String[] levelSplit = mFilePath.split("/");
			String[] fileNameSplit = levelSplit[levelSplit.length-1].split("_");
			fileName = fileNameSplit[1] + "_" + fileNameSplit[2];
		}
		
		return fileName;
	}
}
