package miscellaneous;

import java.util.ArrayList;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;

public class ServicePlaylist {
	ArrayList<Uri> playlist;
	int currentPosition;
	int currentTrackIndex;
	
	
	public ServicePlaylist(ArrayList<Uri> playlist, int currentPosition, int currentTrackIndex) {
		this.playlist = playlist;
		this.currentPosition = currentPosition;
		this.currentTrackIndex = currentTrackIndex;
	}


//sto qua è da cambiare con il task creato in musicUtils
	public static class buildPlaylistTask extends
			AsyncTask<Cursor, Void, ArrayList<Uri>> {

		@Override
		protected ArrayList<Uri> doInBackground(Cursor... params) {
			Cursor cursor = (Cursor) params[0];
			ArrayList<Uri> list = new ArrayList<Uri>();
			cursor.moveToFirst();
			do {
				list.add(ContentUris.withAppendedId(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						cursor.getLong(0)));
			} while (cursor.moveToNext());
			cursor.close();
			return list;
		
		}

	}
}
