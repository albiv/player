package com.somethin.mediaplayer;

import java.util.ArrayList;

import miscellaneous.MusicUtils;
import miscellaneous.Track;

import com.somethin.mediaplayer.adapters.LibraryArtistsAdapter;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class LibraryArtistsFragment extends Fragment implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	private String[] projection;
	private String orderBy;
	private GridView gridview;
	private Uri uri;
	private View v;
	private LibraryArtistsAdapter mAdapter;
	ArrayList<String> artistsList;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);

	}
	static LibraryArtistsFragment newInstance(int num) {
		LibraryArtistsFragment f = new LibraryArtistsFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		projection = new String[] { "_ID", "ARTIST" };
		uri = MediaStore.Audio.Artists.getContentUri("external");
		orderBy = new String(ArtistColumns.ARTIST + " ASC");
		mAdapter = new LibraryArtistsAdapter(getActivity(), null, 0);

	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.fragment_librarygrid, container, false);
		gridview = (GridView) v.findViewById(R.id.gridview);
		gridview.setAdapter(mAdapter);
		registerForContextMenu(gridview);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Intent activityIntent = new Intent(getActivity(),
						AlbumSelectionActivity.class);

				Cursor cursor = mAdapter.getCursor();
				cursor.moveToPosition(position);
				activityIntent.putExtra("artist_id", id);
				activityIntent.putExtra("artist_name", cursor.getString(1));
				startActivity(activityIntent);
			}
		});
		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.addArtistToQueue:
			MusicUtils.buildServicePlaylist(PlayerService.ACTION_ADD_TO_QUEUE,
					info.id, getActivity().getBaseContext(),
					MusicUtils.ARTIST_COLLECTION);
			return true;

		case R.id.playArtist:
			MusicUtils.buildServicePlaylist(PlayerService.ACTION_PLAY, info.id,
					getActivity().getBaseContext(),
					MusicUtils.ARTIST_COLLECTION);
			return true;

		case R.id.addToPlaylist:
			PlaylistDialogFragment dialog = PlaylistDialogFragment.newInstance(
					info.id, MusicUtils.ARTIST_COLLECTION);
			dialog.show(getFragmentManager(), null);

		default:
			return super.onContextItemSelected(item);
		}
	}

	// The callbacks through which we will interact with the LoaderManager.
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	// The adapter that binds our data to the ListView
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		if (loaderID == 0)
			return new CursorLoader(getActivity(), uri, projection, null, null,
					orderBy);
		if (loaderID == 1) {
			return new CursorLoader(
					getActivity(),
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] { "_ID" },
					"ARTIST_ID =?",
					new String[] { Long.toString(bundle.getLong("artist_id")) },
					null);
		}
		if (loaderID == 2) {
			return new CursorLoader(
					getActivity(),
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] { "_ID" },
					"ARTIST_ID =?",
					new String[] { Long.toString(bundle.getLong("artist_id")) },
					null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (loader.getId() == 0)
			mAdapter.swapCursor(cursor);
		if (loader.getId() == 1)
			new buildPlaylistTask(PlayerService.ACTION_ADD_TO_QUEUE)
					.execute(cursor);
		if (loader.getId() == 2)
			new buildPlaylistTask(PlayerService.ACTION_PLAY).execute(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);

	}

	private class buildPlaylistTask extends
			AsyncTask<Cursor, Void, ArrayList<Uri>> {
		String action;

		public buildPlaylistTask(String action) {
			this.action = action;
		}

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
			return list;

		}

		@Override
		protected void onPostExecute(ArrayList<Uri> result) {
			Intent intent = new Intent(getActivity(), PlayerService.class);
			intent.putParcelableArrayListExtra("playlist", result);
			intent.setAction(action);
			getActivity().startService(intent);
		}

	}
}
