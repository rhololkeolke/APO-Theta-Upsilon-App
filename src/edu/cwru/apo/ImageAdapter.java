package edu.cwru.apo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	
	public ImageAdapter(Context c) {
		mContext = c;
	}

	public int getCount() {
		return mThumbIds.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageView;
		if(convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(85,85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8,8,8,8);
		} 
		else {
			imageView = (ImageView) convertView;
		}
		
		imageView.setImageResource(mThumbIds[position]);
		return imageView;
	}
	
	// contains the images that will represent each button
	private Integer[] mThumbIds = {
			R.drawable.news,
			R.drawable.contract,
			R.drawable.profile,
			R.drawable.directory
	};

}
