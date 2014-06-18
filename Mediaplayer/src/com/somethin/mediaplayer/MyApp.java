package com.somethin.mediaplayer;

import java.util.ArrayList;

import android.app.Application;
import android.net.Uri;

public class MyApp extends Application {

	private ArrayList<Uri> lastPlaylist;
	private long currentTrackID;
	
	private boolean isPlaying = false;
	
	public void setPlayingState(boolean playingState){
		isPlaying = playingState;
	}
	
	public boolean isPlaying(){
		return isPlaying;
	}
	
	public void setPlaylist(ArrayList<Uri> list){
		lastPlaylist = list;
	}
	
}
