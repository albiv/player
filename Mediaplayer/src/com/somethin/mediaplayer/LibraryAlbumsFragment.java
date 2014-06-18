package com.somethin.mediaplayer;

import java.util.ArrayList;

import miscellaneous.MusicUtils;

import com.somethin.mediaplayer.adapters.LibraryAlbumsAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class LibraryAlbumsFragment extends Fragment implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	private String[] projection;
	private String select;
	private String orderBy;
	private GridView gridview;
	private Uri uri;
	private View v;
	private LibraryAlbumsAdapter mAdapter;
	final int GET_ALBUMS = 0;
	final int GET_ALBUM_TRACKS = 1;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(GET_ALBUMS, null, this);

	}

	static LibraryAlbumsFragment newInstance(int num) {
		LibraryAlbumsFragment f = new LibraryAlbumsFragment();
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
		projection = new String[] { BaseColumns._ID, AudioColumns.ARTIST,
				AudioColumns.ALBUM, };
		uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
		select = null;
		orderBy = new String(AudioColumns.ALBUM + " ASC");
		mAdapter = new LibraryAlbumsAdapter(getActivity(), null, 0);
		

	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		v = inflater.inflate(R.layout.fragment_librarygrid, container, false);
		gridview = (GridView) v.findViewById(R.id.gridview);
		// if landscape, diminish the column width to 140 dp, after doing a
		// convertion from dp to px
		if (getResources().getConfiguration().orientation == 2)// landscape=2,
																// portrait = 1
			gridview.setColumnWidth((int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 140, getResources()
							.getDisplayMetrics()));
		gridview.setAdapter(mAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Cursor cursor = mAdapter.getCursor();
				cursor.moveToPosition(position);

				Intent activityIntent = new Intent(getActivity(),
						TrackSelectionActivity.class);
				activityIntent.putExtra("album_id", id);
				activityIntent.putExtra("page_name",
						cursor.getString(cursor.getColumnIndexOrThrow("ALBUM")));
				startActivity(activityIntent);
			}

		});
		registerForContextMenu(gridview);
		return v;
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu_album, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {

		case R.id.addAlbumToQueue:
			MusicUtils.buildServicePlaylist(PlayerService.ACTION_ADD_TO_QUEUE,
					info.id, getActivity().getBaseContext(), MusicUtils.ALBUM_COLLECTION);

			return true;
		case R.id.playAlbum:
			MusicUtils.buildServicePlaylist(PlayerService.ACTION_PLAY, info.id,
					getActivity().getBaseContext(), MusicUtils.ALBUM_COLLECTION);
			return true;

		case R.id.addToPlaylist:
			PlaylistDialogFragment dialog = PlaylistDialogFragment.newInstance(info.id, MusicUtils.ALBUM_COLLECTION);
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
		if (loaderID == GET_ALBUMS)
			return new CursorLoader(getActivity(), uri, projection, select,
					null, orderBy);
		if (loaderID == GET_ALBUM_TRACKS) {
			return new CursorLoader(getActivity(),
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] { "_ID" }, "ALBUM_ID =?",
					new String[] { Long.toString(bundle.getLong("album_id")) },
					null);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
		if (loader.getId() == GET_ALBUMS)
			mAdapter.swapCursor(cursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		if (arg0.getId() == GET_ALBUMS)
			mAdapter.swapCursor(null);

	}

}
