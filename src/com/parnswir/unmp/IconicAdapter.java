package com.parnswir.unmp;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.parnswir.unmp.R;

public class IconicAdapter extends ArrayAdapter<String> {
	
	ImageLoader loader;
	
	IconicAdapter(Activity activity, CoverList items) {
		super(activity, R.layout.image_row_layout, R.id.label, items.names);
		loader = new ImageLoader(activity, MainActivity.DB);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView text = (TextView)row.findViewById(R.id.label);
		ImageView icon = (ImageView)row.findViewById(R.id.icon);
		
		String name = (String) text.getText();
		loader.displayAlbumImage(name, icon);
		
		return(row);
	}
}
