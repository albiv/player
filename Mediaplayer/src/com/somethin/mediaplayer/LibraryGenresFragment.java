package com.somethin.mediaplayer;

import com.somethin.mediaplayer.adapters.LibraryGenresAdapter;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.GenresColumns;
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
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LibraryGenresFragment extends Fragment implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	private String[] projection;
	private String select;
	private String orderBy;
	private ListView listview;
	private Uri uri;
	private View v;
	private LibraryGenresAdapter mAdapter;
	private Bundle b;
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);

	}
	static LibraryGenresFragment newInstance(int num) {
		LibraryGenresFragment f = new LibraryGenresFragment();
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
		projection = new String[] { BaseColumns._ID, GenresColumns.NAME, };
		select = null;
		uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
		orderBy = new String(GenresColumns.NAME + " ASC");
		mAdapter = new LibraryGenresAdapter(getActivity(), null, 0);

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
				Intent activityIntent = new Intent(getActivity(),
						AlbumSelectionActivity.class);
				Cursor cursor = mAdapter.getCursor();
				cursor.moveToPosition(position);
				activityIntent.putExtra("genre_id", id);
				activityIntent.putExtra("genre_name", cursor.getString(1));
				getActivity().startService(activityIntent);

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

	// The callbacks through which we will interact with the LoaderManager.

	// The adapter that binds our data to the ListView
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		return new CursorLoader(getActivity(), uri, projection, select, null,
				orderBy);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// return cursor used to populate the grid
		if (loader.getId() == 0)
			mAdapter.swapCursor(cursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		if (arg0.getId() == 0)
			mAdapter.swapCursor(null);

	}

}