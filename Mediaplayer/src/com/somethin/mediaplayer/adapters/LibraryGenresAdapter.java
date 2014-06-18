package com.somethin.mediaplayer.adapters;

import com.somethin.mediaplayer.R;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LibraryGenresAdapter extends CursorAdapter {

	LayoutInflater inflater;

	public LibraryGenresAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		// long albumArt =
		// cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

		// holder.image.setImageBitmap((ArtUtils.getCachedAlbumArt(albumArt,
		// context)));
		holder.text1.setText(cursor.getString(cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)));
		// holder.text2.setText("# of tracks: " +
		// cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._COUNT)));
	}

	/*
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { ImageView imageView; String albumArt = cursor.getString(cursor
	 * .getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)); long aid =
	 * cursor.getLong(0);
	 * 
	 * if (convertView == null) { // if it's not recycled, initialize some
	 * attributes imageView = new ImageView(mContext);
	 * imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	 * imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	 * imageView.setPadding(8, 8, 8, 8); } else { imageView = (ImageView)
	 * convertView; }
	 * 
	 * imageView.setImageResource(mThumbIds[position]); return imageView; }
	 */

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
