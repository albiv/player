package com.somethin.mediaplayer;

import java.util.ArrayList;

import com.somethin.mediaplayer.adapters.LibraryTracksAdapter;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LibraryTracksFragment extends Fragment implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	private String[] projection;
	private String select;
	private String orderBy;
	private ListView listview;
	private Uri uri;
	private View v;
	private LibraryTracksAdapter mAdapter;
	protected ArrayList<Uri> playlist;
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);

	}
	static LibraryTracksFragment newInstance(int num) {
		LibraryTracksFragment f = new LibraryTracksFragment();
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
		projection = new String[] { BaseColumns._ID, AudioColumns.ALBUM_ID,
				MediaColumns.TITLE, AudioColumns.ARTIST };
		select = null;
		uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		orderBy = new String(MediaColumns.TITLE + " ASC");
		mAdapter = new LibraryTracksAdapter(getActivity(), null, 0);

	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.fragment_librarylist, container, false);
		listview = (ListView) v.findViewById(R.id.list);
		listview.setAdapter(mAdapter);
		registerForContextMenu(listview);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				getActivity().getSupportFragmentManager().beginTransaction()
						.add(R.id.playerFrag, new SmallPlayerFragment())
						.commit();
				playlist = new ArrayList<Uri>();
				Intent serviceIntent = new Intent(getActivity(),
						PlayerService.class);
				Cursor cursor = mAdapter.getCursor();
				cursor.moveToPosition(position);
				do {
					playlist.add(ContentUris.withAppendedId(uri,
							cursor.getLong(0)));
				} while (cursor.moveToNext());
				cursor.moveToFirst();
				while (cursor.getPosition() != position) {
					playlist.add(ContentUris.withAppendedId(uri,
							cursor.getLong(0)));
					cursor.moveToNext();
				}
				serviceIntent.putExtra("playlist", playlist);
				serviceIntent.setAction(PlayerService.ACTION_PLAY);
				getActivity().startService(serviceIntent);
			}

			/*
			 * Intent serviceIntent = new Intent(getActivity(),
			 * PlayerService.class);
			 * serviceIntent.setAction("com.somethin.action.PLAY"); Uri
			 * contentUri = ContentUris.withAppendedId(
			 * android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			 * id); serviceIntent.setData(contentUri);
			 * getActivity().startService(serviceIntent); Intent activityIntent
			 * = new Intent(getActivity(), PlayerActivity.class); Bundle b = new
			 * Bundle(); Cursor c= mAdapter.getCursor();
			 * if(c.moveToPosition(position))
			 * b.putLong("track_id",c.getLong(c.getColumnIndexOrThrow
			 * (AudioColumns.ALBUM_ID))); activityIntent.putExtras(b);
			 * startActivity(activityIntent); Log.i("TracksFragment",
			 * "Item clicked: " + id); }
			 */

		});
		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	// The callbacks through which we will interact with the LoaderManager.
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	// The adapter that binds our data to the ListView
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), uri, projection, select, null,
				orderBy);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		mAdapter.swapCursor(arg1);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);

	}

}