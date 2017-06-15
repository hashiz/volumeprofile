package jp.meridiani.apps.volumeprofile.pluginapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.DisplayToast;
import jp.meridiani.apps.volumeprofile.MessageText;
import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockState;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import jp.meridiani.apps.volumeprofile.profile.CurrentProfile;
import jp.meridiani.apps.volumeprofile.profile.ProfileNotFoundException;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusState;

public class PluginFireReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
    	if (!com.twofortyfouram.locale.api.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
        	return;
        }
		BundleUtil bundle;
		try {
			bundle = new BundleUtil(intent.getBundleExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE));
		} catch (InvalidBundleException e) {
			return;
		}
		UUID profileId = bundle.getProfileId();
		String profileName = bundle.getProfileName();
		VolumeLockState changeLock = bundle.getVolumeLock();
		ClearAudioPlusState changeCaState = bundle.getClearAudioPlusState();
		Prefs prefs = Prefs.getInstance(context);
		ProfileStore store = ProfileStore.getInstance(context);
		boolean displayToast = prefs.isDisplayToastOnProfileChange();
		boolean vibrate = prefs.isVibrateOnProfileChange();
		boolean changed = false;
		MessageText toastString = new MessageText(",");
		if (profileId != null) {
			VolumeProfile profile = null;
			try {
				try {
					profile = store.loadProfile(profileId);
				}
				catch (ProfileNotFoundException e) {
					profile = store.loadProfile(profileName);
					profileId = profile.getUuid();
				}
				CurrentProfile.setCurrentProfile(context, profileId);
				toastString.addText(context.getString(R.string.msg_profile_applied, profile.getName()));
				changed = true;
			}
			catch (ProfileNotFoundException e) {
				toastString.addText(context.getString(R.string.msg_profile_notfound, profileName));
				changed = true;
			}
		}
		if (changeLock != null) {
			boolean isLocked = true;
			switch (changeLock) {
			case LOCK:
				isLocked = true;
				break;
			case UNLOCK:
				isLocked = false;
				break;
			case TOGGLE:
				isLocked = !store.isVolumeLocked();
				break;
			}
			store.setVolumeLocked(isLocked);
			int resid;
			if (isLocked) {
				resid = VolumeLockState.LOCK.getResource();
			}
			else {
				resid = VolumeLockState.UNLOCK.getResource();

			}
			toastString.addText(context.getString(resid));
			changed = true;
		}
		if (changeCaState != null) {
			AudioUtil au = new AudioUtil(context);
			if (au.isSupportedClearAudioPlus()) {
				boolean isOn = true;
				switch (changeCaState) {
				case ON:
					isOn = true;
					break;
				case OFF:
					isOn = false;
					break;
				case TOGGLE:
					isOn = !au.getClearAudioPlusState();
					break;
				}
				au.setClearAudioPlusState(isOn);
				int resid;
				if (isOn) {
					resid = ClearAudioPlusState.ON.getResource();
				}
				else {
					resid = ClearAudioPlusState.OFF.getResource();
	
				}
				toastString.addText(context.getString(resid));
				changed = true;
			}
		}
		if (changed) {
			if (displayToast) {
				DisplayToast.show(context, toastString.getText(), Toast.LENGTH_SHORT);
			}
			if (vibrate) {
				Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(100);
			}
		}
    }
}
