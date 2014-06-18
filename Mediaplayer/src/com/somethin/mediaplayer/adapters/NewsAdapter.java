package com.somethin.mediaplayer.adapters;

import java.util.List;

import com.somethin.mediaplayer.R;

import miscellaneous.RSSFeedParser.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends ArrayAdapter<Item> {
	private int count;
	View view;
	ViewHolder holder = null;
	Context context;
	Item item;
	public NewsAdapter(Context context, int resource, List<Item> objects) {
		super(context, resource, objects);
		this.context = context;		
		// TODO Auto-generated constructor stub
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		item = getItem(position);
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.news_listitem, parent, false);
			holder = new ViewHolder();
			//holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//holder.imageView.setImageResource(1);
		holder.title.setText(item.title);

		return convertView;
	}

	private class ViewHolder {
		ImageView imageView;
		TextView title;
	}
}
