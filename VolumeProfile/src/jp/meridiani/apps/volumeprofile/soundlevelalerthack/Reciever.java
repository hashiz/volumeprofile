package jp.meridiani.apps.volumeprofile.soundlevelalerthack;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import jp.meridiani.apps.volumeprofile.settings.Prefs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Reciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Prefs prefs = Prefs.getInstance(context);
		if (!prefs.isSoundLevelAlertHack()) {
			return;
		}
		if (!("com.sonyericsson.media.SOUND_LEVEL_ALERT".equals(intent.getAction()))) {
			return;
		}
		boolean show = intent.getBooleanExtra("com.sonyericsson.media.SOUND_LEVEL_ALERT_SHOW", false);
		if ( !show ) {
			return;
		}
		int challenge = intent.getIntExtra("com.sonyericsson.media.SOUND_LEVEL_ALERT_CHALLENGE", -1);
		if (challenge < 0) {
			return;
		}
		sendAcknowledge(context, challenge);
		sendDialogClose(context);
		restoreVolume(context);
	}

	private void sendAcknowledge(Context context, int challenge) {
		Intent intent = new Intent("com.sonyericsson.media.SOUND_LEVEL_ALERT_ACKNOWLEDGE");
		intent.putExtra("com.sonyericsson.media.SOUND_LEVEL_ALERT_CHALLENGE", challenge);
		context.sendBroadcast(intent);
	}

	private void sendDialogClose(Context context) {
		Intent intent = new Intent("com.sonyericsson.media.SOUND_LEVEL_ALERT");
		intent.putExtra("com.sonyericsson.media.SOUND_LEVEL_ALERT_SHOW", false);
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
	}
}
