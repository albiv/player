package com.somethin.mediaplayer;

import com.somethin.mediaplayer.PlayerService.LocalBinder;

import miscellaneous.ArtUtils;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SmallPlayerFragment extends Fragment implements OnSeekBarChangeListener {
	private PlayerService	 mService;
	private boolean mIsBound = false;
	private ImageButton resume,next,previous ;
	private ImageView image;
	private TextView textview;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
   	 	View v = inflater.inflate(R.layout.fragment_small_player, container, false);
   	 	image = (ImageView) v.findViewById(R.id.albumArt);
   	 	textview = (TextView) v.findViewById(R.id.text1);
		resume = (ImageButton) v.findViewById(R.id.start);
		next = (ImageButton) v.findViewById(R.id.next);
		previous = (ImageButton) v.findViewById(R.id.previous);
		v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), PlayerActivity.class));
				getActivity().overridePendingTransition(R.anim.fadein, R.anim.zoomout);
				
			}
		});
		return v;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onStart() {
		super.onStart();
       // LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(msgReceiver, new IntentFilter("cazzo"));
        if (!mIsBound) doBindService();
        // set Runnable to update seekbar continuously
        

       
	
        
    }
	
	
	/*private Runnable run = new Runnable() {
    	
		@Override public void run() {  
					//seekBar.setProgress(mService.i);
					handler.postDelayed(this, 500);

			
		}
	};
	*/
	
	@Override
	public void onPause(){
		//LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(msgReceiver);
		doUnbindService();
		super.onPause();
		
	}
	
	
	

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
			if (fromUser) mService.setProgress(progress);//WHY THE FUCK DOES THIS UPDATE ITSELF WITHOUT USER INPUT?CUZ of the broadcast receiver
		//LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("seekTo").putExtra("progress", progress));
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
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mIsBound = true;

            resume.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mService.isPlaying())
						v.getContext().startService(new Intent(v.getContext(), PlayerService.class).setAction(PlayerService.ACTION_PAUSE));
					else 
						v.getContext().startService(new Intent(v.getContext(), PlayerService.class).setAction(PlayerService.ACTION_RESUME));
				}
			});
            next.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					v.getContext().startService(new Intent(v.getContext(), PlayerService.class).setAction(PlayerService.ACTION_NEXT));
				}
			});
            previous.setOnClickListener(new OnClickListener() {
	
            	@Override
            	public void onClick(View v) {
            		v.getContext().startService(new Intent(v.getContext(), PlayerService.class).setAction(PlayerService.ACTION_PREVIOUS));
            	}
            });
            
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBound = false;
        }
    };
    
    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
    	 Intent intent = new Intent(getActivity(), PlayerService.class);
    	getActivity().getApplicationContext().bindService(intent, mConnection, 0);
    	 mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
        	getActivity().getApplicationContext().unbindService(mConnection);
            mIsBound = false;
        }
    }

}
