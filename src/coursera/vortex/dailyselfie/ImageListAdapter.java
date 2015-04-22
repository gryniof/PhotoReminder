package coursera.vortex.dailyselfie;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageListAdapter extends BaseAdapter {

	private List<ImageItem> mItems = new ArrayList<ImageItem>();
	private Context mContext;
	
	//private static final String TAG = "ImageListAdapter";
		
	public ImageListAdapter(Context context) {

		mContext = context;
	}

	public void add(ImageItem item) {

		mItems.add(item);
		notifyDataSetChanged();
	}

	public void clear() {

		mItems.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return mItems.size();
	}

	@Override
	public Object getItem(int pos) {

		return mItems.get(pos);
	}

	// Get the ID for the ImageItem
	// In this case it's just the position
	@Override
	public long getItemId(int pos) {

		return pos;
	}

    // ViewHolder pattern to make scrolling more efficient
    // http://developer.android.com/training/improving-layouts/smooth-scrolling.html
	static class ViewHolder {
		ImageView thumbnail;
		TextView fileName;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		if (convertView == null) {
			// Inflate the View for this ImageItem from image_item.xml.
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.image_item, parent, false);
	
			holder = new ViewHolder();
			holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnailView);
			holder.fileName = (TextView) convertView.findViewById(R.id.fileNameView);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final ImageItem imageItem = mItems.get(position);
		
		holder.thumbnail.setImageBitmap(imageItem.getThumbnail());
		holder.fileName.setText(imageItem.getDispFileName());
       
		return convertView;
	}
}
