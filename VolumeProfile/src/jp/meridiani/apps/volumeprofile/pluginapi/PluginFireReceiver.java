package jp.meridiani.apps.volumeprofile.pluginapi;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.DisplayToast;
import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.pluginapi.PluginEditActivity.ClearAudioPlusStateValue;
import jp.meridiani.apps.volumeprofile.pluginapi.PluginEditActivity.VolumeLockValue;
import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import jp.meridiani.apps.volumeprofile.profile.CurrentProfile;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class PluginFireReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
    	if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
        	return;
        }
		BundleUtil bundle;
		try {
			bundle = new BundleUtil(intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE));
		} catch (InvalidBundleException e) {
			return;
		}
		UUID profileId = bundle.getProfileId();
		VolumeLockValue changeLock = bundle.getVolumeLock();
		ClearAudioPlusStateValue changeCaState = bundle.getClearAudioPlusState();
		Prefs prefs = Prefs.getInstance(context);
		ProfileStore store = ProfileStore.getInstance(context);
		boolean displayToast = prefs.isDisplayToastOnProfileChange();
		boolean viblate = prefs.isVibrateOnProfileChange();
		boolean changed = false;
		StringBuffer toastString = new StringBuffer();
		String sep = "";
		if (profileId != null) {
			VolumeProfile profile = store.loadProfile(profileId);
			if ( profile == null ) {
				if (displayToast) {
					String profileName = bundle.getProfileName();
		        	DisplayToast.show(context, context.getString(R.string.msg_profile_notfound, profileName), Toast.LENGTH_SHORT);
				}
			}
			else {
				CurrentProfile.setCurrentProfile(context, profileId);
				toastString.append(context.getString(R.string.msg_profile_applied, profile.getName()));
				sep = ",";
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
				resid = VolumeLockValue.LOCK.getResource();
			}
			else {
				resid = VolumeLockValue.UNLOCK.getResource();

			}
			toastString.append(sep);
			toastString.append(context.getString(resid));
			sep = ",";
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
					resid = ClearAudioPlusStateValue.ON.getResource();
				}
				else {
					resid = ClearAudioPlusStateValue.OFF.getResource();
	
				}
				toastString.append(sep);
				toastString.append(context.getString(resid));
				changed = true;
			}
		}
		if (changed) {
			if (displayToast) {
				DisplayToast.show(context, toastString, Toast.LENGTH_SHORT);
			}
			if (viblate) {
				Vibrator viblator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
				viblator.vibrate(100);
			}
		}
    }
}
