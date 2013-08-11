package jp.meridiani.apps.volumeprofile.soundlevelalerthack;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.prefs.Prefs;
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
		VolumeProfile profile = ProfileStore.getInstance(context).loadProfile(profileId);
		if (profile == null) {
			return;
		}
		new AudioUtil(context).applyProfile(profile);
		Prefs prefs = Prefs.getInstance(context);
		if (prefs.isDisplayToastOnProfileChange()) {
			Toast.makeText(context, context.getString(R.string.msg_profile_applied, profile.getName()), Toast.LENGTH_LONG).show();
		}
		if (prefs.isVibrateOnProfileChange()) {
			Vibrator viblator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
			viblator.vibrate(100);
		}
	}
}
