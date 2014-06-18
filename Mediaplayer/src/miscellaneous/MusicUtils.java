package miscellaneous;

import java.util.ArrayList;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.somethin.mediaplayer.PlayerService;

public class MusicUtils {
	public static final int ALBUM_COLLECTION = 0;
	public static final int ARTIST_COLLECTION = 1;
	
	public static void buildServicePlaylist(String action, long collectionId, Context context, int collectionType) {
		new buildServicePlaylistTask(action, collectionId, context, collectionType).execute();
	}

	public static void buildRealPlaylist(String playlistName,
			long collectionId, Context context, int collectionType) {
		new buildRealPlaylistTask(playlistName, collectionId, context, collectionType)
				.execute();
	}

	public static void addTracksToRealPlaylist(long playlistID,
			long collectionId, Context context, int collectionType) {
		new addTracksToRealPlaylistTask(playlistID, collectionId, context, collectionType)
				.execute();
	}

	public static Cursor getTracksIdFromCollection(final long collectionId,
			final Context context, int collectionType) {
		if(collectionType == ALBUM_COLLECTION)
		return context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { "_ID" }, "ALBUM_ID =?",
				new String[] { Long.toString(collectionId) }, null);
		
		else return  context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { "_ID" }, "ARTIST_ID =?",
				new String[] { Long.toString(collectionId) }, null);
	}

	public static Cursor getPlaylists(Context context) {
		return context.getContentResolver().query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Playlists._ID,
						MediaStore.Audio.Playlists.NAME }, null, null,
				MediaStore.Audio.Playlists.NAME + " ASC");
	}

	public static class buildServicePlaylistTask extends
			AsyncTask<Void, Void, ArrayList<Uri>> {
		String action;
		long collectionId;
		Context context;
		int collectionType;
		
		public buildServicePlaylistTask(String action, long collectionId, Context context, int collectionType) {
			this.action = action;
			this.collectionId = collectionId;
			this.context = context;
			this.collectionType = collectionType;
		}

		@Override
		protected ArrayList<Uri> doInBackground(Void... params) {
			ArrayList<Uri> list = new ArrayList<Uri>();
			Cursor cursor = getTracksIdFromCollection(collectionId, context, collectionType);
			cursor.moveToFirst();
			Log.d("DO IN BACKGROUND STARTED", "kn");
			do {
				list.add(ContentUris.withAppendedId(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						cursor.getLong(0)));
			} while (cursor.moveToNext());
			cursor.close();
			return list;

		}

		@Override
		protected void onPostExecute(ArrayList<Uri> result) {
			Intent intent = new Intent(context, PlayerService.class);
			intent.putParcelableArrayListExtra("playlist", result);
			intent.setAction(action);
			Log.d("ON POST EXECUTTE", action);
			context.startService(intent);
		}
	}

	public static class buildRealPlaylistTask extends
			AsyncTask<Void, Void, Void> {
		String playlistName;
		long collectionId;
		Context context;
		int collectionType;

		public buildRealPlaylistTask(String playlistName, long collectionId,
				Context context, int collectionType) {
			this.playlistName = playlistName;
			this.collectionId = collectionId;
			this.context = context;
			this.collectionType = collectionType;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Cursor cursor = getTracksIdFromCollection(collectionId, context, collectionType);
			ContentResolver ctRes = context.getContentResolver();
			ContentValues playlistValues = new ContentValues();
			Log.d("IM HERE", "fuck");
			playlistValues.put(MediaStore.Audio.Playlists.NAME, playlistName);
			playlistValues.put(MediaStore.Audio.Playlists.DATE_ADDED,
					System.currentTimeMillis());
			playlistValues.put(MediaStore.Audio.Playlists.DATE_MODIFIED,
					System.currentTimeMillis());
			Uri playlistURI = ctRes.insert(
					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
					playlistValues);
			if (playlistURI != null) {
				Log.d("buildREALPlaylistTrask", "playlist created correctly");
				long playorder = 0;
				ContentValues trackValues = new ContentValues();
				cursor.moveToFirst();
				do {
					trackValues.put(
							MediaStore.Audio.Playlists.Members.AUDIO_ID,
							cursor.getLong(cursor.getColumnIndex("_ID")));
					trackValues.put(
							MediaStore.Audio.Playlists.Members.PLAY_ORDER,
							playorder++);
					ctRes.insert(playlistURI, trackValues);
				} while (cursor.moveToNext());
			}
			cursor.close();
			return null;

		}

	}

	public static class addTracksToRealPlaylistTask extends
			AsyncTask<Void, Void, Void> {

		long playlistID;
		long collectionId;
		Context context;
		int collectionType;
		public addTracksToRealPlaylistTask(long playlistID, long collectionId,
				Context context, int collectionType) {
			this.playlistID = playlistID;
			this.collectionId = collectionId;
			this.context = context;
			this.collectionType = collectionType;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Cursor cursor = getTracksIdFromCollection(collectionId, context, collectionType);
			ContentResolver ctRes = context.getContentResolver();
			ContentValues playlistValues = new ContentValues();
			// first update DATE_MODIFIED attribute
			/*
			 * ContentProviderClient cpc
			 * =ctRes.acquireContentProviderClient(MediaStore
			 * .Audio.Playlists.getContentUri("external"));
			 * playlistValues.put(MediaStore.Audio.Playlists.DATE_MODIFIED,
			 * System.currentTimeMillis());cpc.release();
			 */
			// now add the tracks
			Uri playlistURI = MediaStore.Audio.Playlists.Members.getContentUri(
					"external", playlistID);
			ContentProviderClient cpc = ctRes
					.acquireContentProviderClient(playlistURI);
			Cursor cursor2 = ctRes.query(playlistURI, new String[] {
					MediaStore.Audio.Playlists.Members._ID, "PLAY_ORDER" },
					null, null, "PLAY_ORDER DESC");
			cursor2.moveToFirst();
			long play_order = (cursor2.getLong(cursor2
					.getColumnIndex("PLAY_ORDER"))) + 1;
			cursor2.close();
			cursor.moveToFirst();
			do {
				playlistValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID,
						cursor.getLong(cursor.getColumnIndex("_ID")));
				playlistValues.put(
						MediaStore.Audio.Playlists.Members.PLAY_ORDER,
						play_order++);
				try {
					cpc.insert(playlistURI, playlistValues);
				} catch (RemoteException e) {
					Log.d("MODIFING PLAYLIST", "the insertion failed");
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
			cpc.release();
			cursor.close();
			return null;
		}
	}

	public static ArrayList<Uri> getTracksFromPlaylist(long playlistId,
			Context context) {
		ArrayList<Uri> servicePlaylist = new ArrayList<Uri>();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Playlists.Members.getContentUri("external",
						playlistId),
				new String[] { MediaStore.Audio.Playlists.Members.AUDIO_ID },
				null, null, null);

		cursor.moveToFirst();
		do {
			servicePlaylist.add(ContentUris.withAppendedId(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					cursor.getLong(0)));
		} while (cursor.moveToNext());
		return servicePlaylist;

	}
}
