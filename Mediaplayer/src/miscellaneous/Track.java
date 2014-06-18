package miscellaneous;

import java.net.URISyntaxException;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.database.Cursor;

public class Track {
	private String title;
	private String artistName;
	private String albumName;
	private long albumID;
	private Uri uri;
	private long trackID;

	public Track(Uri trackUri, Context context) {
		String trackID = Long.toString(ContentUris.parseId(trackUri));
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { "_ID", "TITLE", "ARTIST", "ALBUM", "ALBUM_ID" },
				"_ID =?", new String[] { trackID }, null);
		cursor.moveToFirst();
		this.trackID = cursor.getLong(0);
		this.title = cursor.getString(1);
		this.artistName = cursor.getString(2);
		this.albumName = cursor.getString(3);
		this.albumID = cursor.getLong(4);
		this.uri = trackUri;
	}

	public Track(long trackID, Context context) {
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { "_ID", "TITLE", "ARTIST", "ALBUM", "ALBUM_ID" },
				"_ID =?", new String[] { Long.toString(trackID) }, null);
		cursor.moveToFirst();
		this.trackID = trackID;
		this.title = cursor.getString(1);
		this.artistName = cursor.getString(2);
		this.albumName = cursor.getString(3);
		this.albumID = cursor.getLong(4);
		this.uri = ContentUris.withAppendedId(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, trackID);
	}

	public static Uri getUriFromId(long trackId){
		return ContentUris.withAppendedId(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, trackId);
	}
}