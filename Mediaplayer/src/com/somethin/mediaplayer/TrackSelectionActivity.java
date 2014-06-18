package com.somethin.mediaplayer;

import java.util.ArrayList;

import com.somethin.mediaplayer.adapters.TrackSelectionAdapter;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.provider.MediaStore;

public class TrackSelectionActivity extends ActionBarActivity implements
android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{

	private ListView listview;
	private TrackSelectionAdapter mAdapter;
	private Long albumID;
	private Long artistID;
	private Long genreID;
	private Uri uri;
	private String[] projection;
	private String selection;
	private String[] selectionArgs;
	private String orderBy;
	private Button playAllButton;
	private Uri uri2;
	private String selection2;
	private String[] projection2;
	private String orderBy2;
	private String[] selectionArgs2;
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
	private ArrayList<Uri> playlist;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_selection);
		if (savedInstanceState == null && ((MyApp)getApplicationContext()).isPlaying()) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			//sft.setCustomAnimations(R.anim.translatein_from_below,0);
			ft.add(R.id.playerFrag, new SmallPlayerFragment());
			ft.commit();
		}
		
		setTitle(getIntent().getStringExtra("page_name"));
		listview = (ListView) findViewById(R.id.list);
		mAdapter = new TrackSelectionAdapter(getApplicationContext(), null, 0);
		listview.setAdapter(mAdapter);
		albumID = getIntent().getLongExtra("album_id", -1);
		
		if(albumID != -1){
			
			uri =MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			projection = new String[]{"_ID","TITLE", "DURATION"};
			selection = new String("ALBUM_ID =?");
			selectionArgs = new String[]{Long.toString(albumID)};
			orderBy = "TRACK ASC";
		}
		else{
			artistID = getIntent().getLongExtra("artist_id", -1);
			uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			projection = new String[]{"_ID","TITLE", "DURATION"};
			selection = "ARTIST_ID =" +artistID;
			selectionArgs = null;
			orderBy = "TRACK ASC";
		}
		getSupportLoaderManager().initLoader(0, null, this );

		listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Log.d("CAZZO", Long.toString(id));
            	playlist  = new ArrayList<Uri>();
        		Intent serviceIntent = new Intent(getBaseContext(), PlayerService.class);
        		Cursor cursor = mAdapter.getCursor();
        		//create a playlist in which the first track is the selected track itself, followed by the tracks next in the list.Add also
        		//the track preceding the selected track, in circular fashion 
        		cursor.moveToPosition(position);
        		do {
    				playlist.add(ContentUris.withAppendedId(uri, cursor.getLong(0)));
        		}
        		while (cursor.moveToNext());
        		cursor.moveToFirst();
        		while(cursor.getPosition()!= position){
    				playlist.add(ContentUris.withAppendedId(uri, cursor.getLong(0)));
    				cursor.moveToNext();
        		}
        		serviceIntent.putExtra("playlist", playlist);
        		serviceIntent.setAction(PlayerService.ACTION_PLAY);
        		startService(serviceIntent);
        		
        		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    			//non funziona
    			//ft.setCustomAnimations(R.anim.translatein_from_below,0);
        		ft.add(R.id.playerFrag, new SmallPlayerFragment());
    			ft.commit();
                }
            
            
            });
		
		receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				//non funziona
				ft.setCustomAnimations(R.anim.translatein_from_below,0);
				ft.add(R.id.playerFrag, new SmallPlayerFragment());
				ft.commit();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayerService.ACTION_SHOW);
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album, menu);
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
			return new CursorLoader(this,uri,projection,selection,selectionArgs,orderBy);
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}

}
