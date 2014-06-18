package com.somethin.mediaplayer.adapters;

import java.util.concurrent.TimeUnit;

import com.somethin.mediaplayer.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrackSelectionAdapter extends CursorAdapter {

	private LayoutInflater inflater;

	public TrackSelectionAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		holder.text1.setText(cursor.getString(cursor
				.getColumnIndexOrThrow("TITLE")));
		holder.text2.setText(convertMill(cursor.getLong((cursor
				.getColumnIndexOrThrow("DURATION")))));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup container) {
		View view = new View(context);
		ViewHolder holder = new ViewHolder();
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.library_listitem, container, false);

		holder.text1 = (TextView) view.findViewById(R.id.text1);
		holder.text2 = (TextView) view.findViewById(R.id.text2);

		view.setTag(holder);

		return view;
	}

	private String convertMill(long millis){
		return String.format("%02d:%02d:%02d", 
			    TimeUnit.MILLISECONDS.toHours(millis),
			    TimeUnit.MILLISECONDS.toMinutes(millis) - 
			    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
			    TimeUnit.MILLISECONDS.toSeconds(millis) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
	class ViewHolder {
		TextView text1;
		TextView text2;
	}

}