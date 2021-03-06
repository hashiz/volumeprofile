package jp.meridiani.apps.volumeprofile.soundlevelalerthack;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.DisplayToast;
import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import jp.meridiani.apps.volumeprofile.profile.ProfileNotFoundException;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {
	private static final String ACTION_SOUND_LEVEL_ALERT = "com.sonyericsson.media.SOUND_LEVEL_ALERT";
	private static final String EXTRA_ALERT_SHOW         = "com.sonyericsson.media.SOUND_LEVEL_ALERT_SHOW";
	private static final String EXTRA_CHALLENGE          = "com.sonyericsson.media.SOUND_LEVEL_ALERT_CHALLENGE";
	private static final String ACTION_ACKNOWLEDGE       = "com.sonyericsson.media.SOUND_LEVEL_ALERT_ACKNOWLEDGE";
	private static final int RETRY_MAX = 5;
	private static final long RETRY_WAIT = 1000;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!Prefs.getInstance(context).isSoundLevelAlertHack()) {
			return;
		}
		if (!(ACTION_SOUND_LEVEL_ALERT.equals(intent.getAction()))) {
			return;
		}
		boolean show = intent.getBooleanExtra(EXTRA_ALERT_SHOW, false);
		if ( !show ) {
			return;
		}
		int challenge = intent.getIntExtra(EXTRA_CHALLENGE, -1);
		if (challenge < 0) {
			return;
		}
		sendAcknowledge(context, challenge);
		sendDialogClose(context);
		restoreVolume(context);
	}

	private void sendAcknowledge(Context context, int challenge) {
		Intent intent = new Intent(ACTION_ACKNOWLEDGE);
		intent.putExtra(EXTRA_CHALLENGE, challenge);
		context.sendBroadcast(intent);
	}

	private void sendDialogClose(Context context) {
		Intent intent = new Intent(ACTION_SOUND_LEVEL_ALERT);
		intent.putExtra(EXTRA_ALERT_SHOW, false);
		context.sendBroadcast(intent);
	}

	private void restoreVolume(Context context) {
		UUID profileId = ProfileStore.getInstance(context).getCurrentProfile();
		if (profileId == null) {
			return;
		}
		VolumeProfile profile;
		try {
			profile = ProfileStore.getInstance(context).loadProfile(profileId);
		}
		catch (ProfileNotFoundException e) {
			return;
		}
		AudioUtil audio = new AudioUtil(context);
		Prefs prefs = Prefs.getInstance(context);

		for (int trycount = 0; trycount < RETRY_MAX && profile.getMusicVolume() != audio.getVolume(StreamType.MUSIC); trycount++) {
			audio.applyProfile(profile);
			if (prefs.isDisplayToastOnProfileChange()) {
				DisplayToast.show(context, context.getString(R.string.msg_profile_applied, profile.getName()), Toast.LENGTH_LONG);
			}
			if (prefs.isVibrateOnProfileChange()) {
				Vibrator viblator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
				viblator.vibrate(100);
			}
			try {
				Thread.sleep(RETRY_WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
