package com.somethin.mediaplayer.adapters;

import miscellaneous.echonestTest;

import com.echonest.api.v4.EchoNestException;
import com.somethin.mediaplayer.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class LibraryArtistsAdapter extends CursorAdapter {

	LayoutInflater inflater;
	echonestTest echo;
	public LibraryArtistsAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
			echo = new echonestTest();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		String artistName = cursor.getString(cursor.getColumnIndexOrThrow(AudioColumns.ARTIST));
		//doesn't work good with loading album convers too, because(I think) there s a limited number of parallel asynctasks(5 or so)
		/*echo.loadBitmap(artistName, holder.image);
		AlphaAnimation aa = new AlphaAnimation(0f, 1f);
		aa.setDuration(500);
		holder.image.startAnimation(aa);*/
		holder.text1.setText(artistName);
		// holder.text2.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup container) {
		View view = new View(context);
		ViewHolder holder = new ViewHolder();
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.library_griditem, container, false);
		holder.image = (ImageView) view.findViewById(R.id.imageView1);
		// image.setLayoutParams(new GridView.LayoutParams(100, 100));
		// image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		holder.text1 = (TextView) view.findViewById(R.id.text1);
		// holder.text2 = (TextView) view.findViewById(R.id.text2);

		view.setTag(holder);

		return view;
	}

	class ViewHolder {
		ImageView image;
		TextView text1;
		TextView text2;
	}

}
