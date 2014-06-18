package com.somethin.mediaplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ListIterator;

import miscellaneous.ServicePlaylist;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.content.Context;
import android.content.Intent;

public class PlayerService extends Service implements
		MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
		OnAudioFocusChangeListener {
	// private Runnable run;
	// private Handler handler = new Handler();

	/*
	 * BroadcastReceiver receiver = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) {
	 * mediaPlayer.seekTo(intent.getIntExtra("progress", 0)); } };
	 */
	// Timer timer = new Timer();

	private final IBinder mBinder = new LocalBinder();

	private static MediaPlayer mediaPlayer = null;
	public static final String ACTION_PLAY = "com.somethin.action.PLAY";
	public static final String ACTION_NEXT = "com.somethin.action.NEXT";
	public static final String ACTION_SHOW = "com.something.action.SHOW";
	public static final String ACTION_PREVIOUS = "com.somethin.action.PREVIOUS";
	public static final String ACTION_PAUSE = "com.somethin.action.PAUSE";
	public static final String ACTION_RESUME = "com.somethin.action.RESUME";
	public static final String ACTION_ADD_TO_QUEUE ="com.somethin.action.ADDQUEUE";
	private ArrayList<Uri> playlist = new ArrayList<Uri>();
	private int currentTrack;
	private Uri currentTrackUri;
	private MyApp myapp;


	
	
	@Override
	public void onCreate() {
		
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int result = audioManager.requestAudioFocus(this,
				AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			// could not get audio focus.
		}
		
		myapp = ((MyApp) getApplicationContext());
		super.onCreate();
	}

	public void onAudioFocusChange(int focusChange) {
		switch (focusChange) {

		case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
			if (mediaPlayer.isPlaying())
				mediaPlayer.setVolume(0.1f, 0.1f);
			break;
		case AudioManager.AUDIOFOCUS_GAIN:
			// resume playback
			if (mediaPlayer == null)
				initMediaPlayer();
			else if (!mediaPlayer.isPlaying())
				mediaPlayer.start();
			mediaPlayer.setVolume(1.0f, 1.0f);
			break;

		case AudioManager.AUDIOFOCUS_LOSS:
			// Lost focus for an unbounded amount of time: stop playback and
			// release media player
			saveCurrentPlaylistState(new ServicePlaylist(playlist,mediaPlayer.getCurrentPosition(),currentTrack));
			if (mediaPlayer.isPlaying())
				mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			break;

		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			// Lost focus for a short time, but we have to stop
			// playback. We don't release the media player because playback
			// is likely to resume
			if (mediaPlayer.isPlaying())
				mediaPlayer.pause();
			break;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		switch (intent.getAction()) {

		case ACTION_PLAY:
			// check if mediaplayer is already playing something. In that case,
			// reset it
			if (mediaPlayer == null) {
				initMediaPlayer();
			}
			mediaPlayer.reset();
			// mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			playlist = intent.getParcelableArrayListExtra("playlist");
			// per ora faccio così, dopo lo dovrei salvare direttamente sul db,
			// o forse meglio ancora, nelle shared preferences
			myapp.setPlaylist(playlist);
			currentTrack = 0;
			play(playlist.get(currentTrack));
			break;

		case ACTION_NEXT:
			next();
			break;

		case ACTION_PREVIOUS:
			previous();
			break;

		case ACTION_PAUSE:
			mediaPlayer.pause();
			break;

		case ACTION_RESUME:
			mediaPlayer.start();
			break;
			
		case ACTION_ADD_TO_QUEUE:
			ArrayList<Uri> tracksToAdd = intent.getParcelableArrayListExtra("playlist");
			addToQueue(tracksToAdd);	
		}

		return 0;

		/*
		 * if (intent.getAction().equals(ACTION_PLAY)) { if(mediaPlayer ==
		 * null){ mediaPlayer = new MediaPlayer();
		 * mediaPlayer.setWakeMode(getApplicationContext(),
		 * PowerManager.PARTIAL_WAKE_LOCK); } mediaPlayer.reset();
		 * mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); playlist =
		 * intent.getParcelableArrayListExtra("playlist");
		 * play(playlist.get(currentPlaylistTrack)); }
		 */
		/*
		 * try {
		 * 
		 * //mediaPlayer.setDataSource(getApplicationContext(),
		 * intent.getData()); mediaPlayer.setDataSource(getApplicationContext(),
		 * playlist.get(currentPlaylistTrack)); } catch
		 * (IllegalArgumentException | SecurityException | IllegalStateException
		 * | IOException e) {
		 * 
		 * e.printStackTrace(); } mediaPlayer.setOnPreparedListener(this);
		 * mediaPlayer.prepareAsync(); // prepare async to not block main thread
		 * // LocalBroadcastManager.getInstance(getApplicationContext()).
		 * registerReceiver(receiver, new IntentFilter("seekTo")); }
		 */

		/*
		 * if(intent.getAction().equals(ACTION_NEXT)){ currentPlaylistTrack++;
		 * play(playlist.get(currentPlaylistTrack)); }
		 * 
		 * if(intent.getAction().equals(ACTION_PREVIOUS)){ if
		 * (currentPlaylistTrack == 0) currentPlaylistTrack = playlist.size() -
		 * 1; currentPlaylistTrack--; play(playlist.get(currentPlaylistTrack));
		 * } return 0;
		 */
	}

	@Override
	public void onDestroy() {
		mediaPlayer.release();
		mediaPlayer = null;
		saveCurrentPlaylistState(new ServicePlaylist(playlist,mediaPlayer.getCurrentPosition(),currentTrack));
		Log.d("SERVICE-PLAYER", " I STOPPED AND SAVED THE STATE");
		super.onDestroy();
	}
	
	
	/** Called when MediaPlayer is ready */
	@Override
	@SuppressWarnings("deprecation")
	public void onPrepared(MediaPlayer player) {
		player.start();
		myapp.setPlayingState(true);

		// notification
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
				0, new Intent(getApplicationContext(), MainActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(getApplicationContext(),
				null, ": " + "Twofold", pi);
		startForeground(1, notification);

		/*
		 * TimerTask task = new TimerTask() {
		 * 
		 * @Override public void run() { Intent intent = new Intent("cazzo");
		 * intent.putExtra("currpos", mediaPlayer.getCurrentPosition());
		 * LocalBroadcastManager
		 * .getInstance(getApplicationContext()).sendBroadcast(intent); } }
		 * 
		 * timer.scheduleAtFixedRate(task, 0, 500);
		 */
	}

	private void play(Uri uri) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(getBaseContext(), uri);
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.prepareAsync();
		currentTrackUri = uri;// prepare async to not block main thread

	}

	private void next() {
		//if(LOOP)
		// if playing the last track of a playlist, restart from the first track
		if (currentTrack == playlist.size() - 1)
			currentTrack = 0;
		else {
			currentTrack++;
		}
		play(playlist.get(currentTrack));
	}

	private void previous() {
		// if playing the first track of a playlist, start from the end of the
		// playlist
		if (currentTrack == 0)
			currentTrack = playlist.size() - 1;
		else {
			currentTrack--;
		}
		play(playlist.get(currentTrack));
	}

	public class LocalBinder extends Binder {
		PlayerService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return PlayerService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public int getCurrentPosition() {
		return mediaPlayer.getCurrentPosition();

	}

	public int getDuration() {
		return mediaPlayer.getDuration();
	}

	public void setProgress(int progress) {
		mediaPlayer.seekTo(progress);
	}

	public void setPause() {
		mediaPlayer.pause();
	}

	public void setResume() {
		mediaPlayer.start();
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	@Override
	public void onCompletion(MediaPlayer mediaplayer) {
		next();
	}

	private void initMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setWakeMode(getApplicationContext(),
				PowerManager.PARTIAL_WAKE_LOCK);
	}

	
	public void addToQueue(ArrayList<Uri> uriList){
		playlist.addAll(uriList);
	}

	public void removeFromQueue(Uri uri) {
		int index = playlist.indexOf(uri);
		if (index != -1) {
			playlist.remove(index);
		}
		myapp.setPlaylist(playlist);
	}

	public Uri getCurrentTrackUri() {
		return currentTrackUri;
	}
	

	public void saveCurrentPlaylistState(ServicePlaylist currentPlaylist) {
		boolean keep = true;
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		File file = null;
		try {
			file = File.createTempFile("playlist", null,
					getApplicationContext().getCacheDir());
			fileOut = new FileOutputStream(file);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(currentPlaylist);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileOut != null)
					fileOut.close();
				if (out != null)
					out.close();
				if (keep == false)
					file.delete();
			} catch (Exception e) { /* do nothing */
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public ServicePlaylist retrievePlaylist(){
		ServicePlaylist playlistFromFile = null;
		try {
			FileInputStream fis = new FileInputStream(new File(getCacheDir(), "playlist"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			ServicePlaylist playlist  = (ServicePlaylist) ois.readObject();
			ois.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
				
		}
		return playlistFromFile;
	}
}
