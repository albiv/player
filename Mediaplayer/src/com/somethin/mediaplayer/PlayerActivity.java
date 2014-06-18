package com.somethin.mediaplayer;

import miscellaneous.ArtUtils;

import com.somethin.mediaplayer.PlayerService.LocalBinder;

import android.sax.StartElementListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PlayerActivity extends ActionBarActivity implements
		OnSeekBarChangeListener {
	private SeekBar seekBar;
	private Runnable run;
	private PlayerService mService;
	private boolean mIsBound = false;
	private Handler handler = new Handler();
	private ImageButton resume, next, previous;
	private ImageView imageView;

	/*
	 * BroadcastReceiver msgReceiver = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) {
	 * seekBar.setProgress(intent.getIntExtra("currpos", 0)); } };
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);

		imageView = (ImageView) findViewById(R.id.albumArt);
		// image.setImageBitmap(ArtUtils.getBitmapAlbumArt(getIntent().getExtras().getLong("album_id"),
		// getApplicationContext(), 400, 600));
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(this);
		resume = (ImageButton) findViewById(R.id.start);
		next = (ImageButton) findViewById(R.id.next);
		previous = (ImageButton) findViewById(R.id.previous);
		getSupportActionBar().hide();
		doBindService();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(msgReceiver,
		// new IntentFilter("cazzo"));
		if (!mIsBound)
			doBindService();
		// set Runnable to update seekbar continuously
		run = new Runnable() {
			@Override
			public void run() {
				seekBar.setProgress(mService.getCurrentPosition());
				handler.postDelayed(this, 500);
			}
		};

	}

	/*
	 * private Runnable run = new Runnable() {
	 * 
	 * @Override public void run() { //seekBar.setProgress(mService.i);
	 * handler.postDelayed(this, 500);
	 * 
	 * 
	 * } };
	 */

	@Override
	protected void onPause() {
		// LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(msgReceiver);
		doUnbindService();
		super.onPause();
		overridePendingTransition(R.anim.zoomin, R.anim.translateout);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.player, menu);
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
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser)
			mService.setProgress(progress);// WHY THE FUCK DOES THIS UPDATE
											// ITSELF WITHOUT USER INPUT?CUZ of
											// the broadcast receiver
		// LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new
		// Intent("seekTo").putExtra("progress", progress));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mService.setPause();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mService.setResume();
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mIsBound = true;
			drawAlbumCover(imageView);
			seekBar.setMax(mService.getDuration());
			handler.post(run);
			resume.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mService.isPlaying())
						startService(new Intent(getApplicationContext(),
								PlayerService.class)
								.setAction(PlayerService.ACTION_PAUSE));
					startService(new Intent(getApplicationContext(),
							PlayerService.class)
							.setAction(PlayerService.ACTION_PLAY));
				}
			});
			next.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startService(new Intent(getApplicationContext(),
							PlayerService.class)
							.setAction("com.somethin.action.NEXT"));
				}
			});
			previous.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startService(new Intent(getApplicationContext(),
							PlayerService.class)
							.setAction("com.somethin.action.PREVIOUS"));
				}
			});

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mIsBound = false;
		}
	};

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because there is no reason to be able to let other
		// applications replace our component.
		Intent intent = new Intent(this, PlayerService.class);
		bindService(intent, mConnection, BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	private void drawAlbumCover(ImageView imageView) {
		Uri uri = mService.getCurrentTrackUri();
		String id = Long.toString(ContentUris.parseId(uri));
		Log.d("cazzo", id);
		Cursor cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { "_ID", "ALBUM_ID" }, "_ID =?",
				new String[] { id }, null);
		cursor.moveToFirst();
		long albumID = cursor.getLong(1);
		imageView.setImageBitmap(ArtUtils.getBitmapAlbumArt(albumID,
				getApplicationContext(), 800, 800));
		cursor.close();

	}

}
