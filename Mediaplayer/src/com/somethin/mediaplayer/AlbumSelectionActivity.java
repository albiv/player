package com.somethin.mediaplayer;

import com.somethin.mediaplayer.adapters.AlbumSelectionAdapter;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.widget.AdapterView.OnItemClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;

public class AlbumSelectionActivity extends ActionBarActivity implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	private ListView listview;
	private AlbumSelectionAdapter mAdapter;
	private Long artistID;
	private Long genreID;
	private Uri uri;
	private String[] projection;
	private String orderBy;
	private Button playAllButton;
	/*
	 * private Uri uri2; private String selection2; private String[]
	 * projection2; private String orderBy2; private String[] selectionArgs2;
	 */
	private String pageName;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null
				&& ((MyApp) getApplicationContext()).isPlaying()) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			// non funziona
			// ft.setCustomAnimations(R.anim.translatein_from_below,0);
			ft.add(R.id.playerFrag, new SmallPlayerFragment());
			ft.commit();
		}
		setContentView(R.layout.activity_album_selection);
		artistID = getIntent().getLongExtra("artist_id", -1);
		if (artistID != -1) {
			pageName = getIntent().getStringExtra("artist_name");
			setTitle(pageName);
			uri = MediaStore.Audio.Artists.Albums.getContentUri("external",
					artistID);
			projection = new String[] { "_id", AlbumColumns.ALBUM,
					AlbumColumns.NUMBER_OF_SONGS };
			orderBy = AlbumColumns.ALBUM + " ASC";

			/*
			 * uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; projection2 =
			 * new String[] { "_ID" }; selection2 = new String("ARTIST_ID =?");
			 * selectionArgs2 = new String[] { Long.toString(artistID) };
			 * orderBy2 = MediaStore.Audio.Media.ALBUM + " ASC";
			 */
		} else {
			genreID = getIntent().getLongExtra("genre_id", -1);
			pageName = getIntent().getStringExtra("genre_name");
			setTitle(pageName);
			uri = MediaStore.Audio.Genres.Members.getContentUri("external",
					genreID);
			projection = new String[] { "_id"
			// AlbumColumns.ALBUM_ID,
			// AlbumColumns.ALBUM,
			// AlbumColumns.NUMBER_OF_SONGS
			};
			orderBy = null;
		}
		listview = (ListView) findViewById(R.id.list);
		playAllButton = (Button) findViewById(R.id.playAll);
		mAdapter = new AlbumSelectionAdapter(getApplicationContext(), null, 0);
		listview.setAdapter(mAdapter);
		getSupportLoaderManager().initLoader(0, null, this);
		playAllButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent activityIntent = new Intent(getBaseContext(),
						TrackSelectionActivity.class);
				activityIntent.putExtra("artist_id", artistID);
				startActivity(activityIntent);
				// playlist = new ArrayList<Uri>();
				// getSupportLoaderManager().initLoader(1, null, mCallbacks);
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Intent activityIntent = new Intent(getBaseContext(),
						TrackSelectionActivity.class);
				activityIntent.putExtra("album_id", id);
				activityIntent.putExtra("artist_id", artistID);
				activityIntent.putExtra("page_name", pageName);
				startActivity(activityIntent);
			}
		});

		/*
		 * receiver = new BroadcastReceiver() {
		 * 
		 * @Override public void onReceive(Context context, Intent intent) {
		 * FragmentTransaction ft = getSupportFragmentManager()
		 * .beginTransaction(); // non funziona //
		 * ft.setCustomAnimations(R.anim.translatein_from_below,0);
		 * ft.add(R.id.playerFrag, new SmallPlayerFragment()); ft.commit(); } };
		 * IntentFilter filter = new IntentFilter();
		 * filter.addAction(PlayerService.ACTION_SHOW);
		 * LocalBroadcastManager.getInstance(getApplicationContext())
		 * .registerReceiver(receiver, filter);
		 */

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		return new CursorLoader(this, uri, projection, null, null, orderBy);
		/*
		 * else return new CursorLoader(this, uri2, projection2, selection2,
		 * selectionArgs2, orderBy2);
		 */
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		/*
		 * cursor.moveToFirst(); do { playlist.add(ContentUris .withAppendedId(
		 * android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		 * cursor.getLong(0))); } while (cursor.moveToNext()); }
		 */
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (((MyApp) getApplicationContext()).isPlaying()
				&& getSupportFragmentManager()
						.findFragmentById(R.id.playerFrag) == null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.setCustomAnimations(0, 0);
			ft.add(R.id.playerFrag, new SmallPlayerFragment());
			ft.commit();
		}
	}

	

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		
	}

}
