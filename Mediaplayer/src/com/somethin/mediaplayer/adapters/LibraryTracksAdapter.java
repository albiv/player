package com.somethin.mediaplayer.adapters;

import com.somethin.mediaplayer.R;

import miscellaneous.ArtUtils;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class LibraryTracksAdapter extends CursorAdapter {

	LayoutInflater inflater;

	public LibraryTracksAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		long albumArt = cursor.getLong(cursor
				.getColumnIndexOrThrow(AudioColumns.ALBUM_ID));
		ArtUtils.BitmapLoaderTask.loadBitmap(albumArt, holder.image, context);
		AlphaAnimation aa = new AlphaAnimation(0f, 1f);
		aa.setDuration(500);
		holder.image.startAnimation(aa);
		// holder.image.setImageBitmap((ArtUtils.getCachedAlbumArt(albumArt,
		// context)));
		holder.text1.setText(cursor.getString(cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
		holder.text2.setText(cursor.getString(cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup container) {

		View view = new View(context);
		ViewHolder holder = new ViewHolder();
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.library_listitem, container, false);
		holder.image = (ImageView) view.findViewById(R.id.imageView1);
		holder.text1 = (TextView) view.findViewById(R.id.text1);
		holder.text2 = (TextView) view.findViewById(R.id.text2);

		view.setTag(holder);

		return view;
	}

	class ViewHolder {
		ImageView image;
		TextView text1;
		TextView text2;
	}

}
