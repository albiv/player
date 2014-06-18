package miscellaneous;

import com.somethin.mediaplayer.PlayerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public final class MusicIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
			context.startService(new Intent(context, PlayerService.class)
					.setAction(PlayerService.ACTION_PAUSE));
		}
	}

}
