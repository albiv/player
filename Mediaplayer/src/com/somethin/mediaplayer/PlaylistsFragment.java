package com.somethin.mediaplayer;

import com.somethin.mediaplayer.adapters.LibraryAlbumsAdapter;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class PlaylistsFragment extends Fragment implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
	SimpleCursorAdapter mAdapter;
	ListView listview;
	View v;
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(),
				android.R.layout.simple_list_item_1, null,
				new String[] { MediaStore.Audio.Playlists.NAME },
				new int[] { android.R.id.text1 }, 0);		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_librarylist, container, false);
		
		listview = (ListView) v.findViewById(R.id.list);
		listview.setAdapter(mAdapter);
		registerForContextMenu(v);
		return v;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(),
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, new String[] {
						MediaStore.Audio.Playlists._ID,
						MediaStore.Audio.Playlists.NAME }, null, null,
				MediaStore.Audio.Playlists.NAME + " ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		mAdapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
