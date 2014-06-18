package com.somethin.mediaplayer.adapters;

import com.somethin.mediaplayer.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumSelectionAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	public AlbumSelectionAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		ViewHolder holder = (ViewHolder) view.getTag();
		// long albumArt =
		// cursor.getLong(cursor.getColumnIndexOrThrow("ALBUM"));
		// ArtUtils.BitmapLoaderTask.loadBitmap(albumArt, holder.image,
		// context);

		// holder.image.setImageBitmap((ArtUtils.getCachedAlbumArt(albumArt,
		// context)));
		holder.text1.setText(cursor.getString(cursor
				.getColumnIndexOrThrow(AlbumColumns.ALBUM)));
		holder.text2
				.setText(cursor.getString(cursor
						.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS)));
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
