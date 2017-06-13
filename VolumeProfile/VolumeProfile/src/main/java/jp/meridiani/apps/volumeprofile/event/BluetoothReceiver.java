package jp.meridiani.apps.volumeprofile.event;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import jp.meridiani.apps.volumeprofile.DisplayToast;
import jp.meridiani.apps.volumeprofile.MessageText;
import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import jp.meridiani.apps.volumeprofile.profile.CurrentProfile;
import jp.meridiani.apps.volumeprofile.profile.ProfileNotFoundException;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;

public class BluetoothReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Event.BTState state;
		BluetoothDevice stateChangedDevice = null;
		Event.BTProfile stateChangedProfile = null;
    	if (BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
			stateChangedProfile = Event.BTProfile.A2DP;
		}
		else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
			stateChangedProfile = Event.BTProfile.HEADSET;
		}
		else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
			stateChangedProfile = Event.BTProfile.ANY;
		}
		else {
			return;
		}
		if (intent.hasExtra(BluetoothProfile.EXTRA_STATE)) {
			switch (intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_CONNECTED)) {
				case BluetoothProfile.STATE_CONNECTED:
					state = Event.BTState.CONNECTED;
					break;
				case BluetoothProfile.STATE_DISCONNECTED:
					state = Event.BTState.DISCONNECTED;
					break;
				default:
					return;
			}
		}
		else if (intent.hasExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE)) {
			switch (intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.STATE_CONNECTED)) {
				case BluetoothAdapter.STATE_CONNECTED:
					state = ConnState.CONNECTED;
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					state = ConnState.DISCONNECTED;
					break;
				default:
					return;
			}
		}
		else {
			return;
		}
		if (!intent.hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
			return;
		}
		stateChangedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		Prefs prefs = Prefs.getInstance(context);
		EventStore store = EventStore.getInstance(context);
		boolean displayToast = prefs.isDisplayToastOnProfileChange();
		boolean vibrate = prefs.isVibrateOnProfileChange();
		boolean changed = false;
		MessageText toastString = new MessageText(",");

		for (Event event : store.listEvents()) {
			if (!event.getDeviceAddress().equals(stateChangedDevice.getAddress())) {
				continue;
			}
			if (event.getBTProfile() != Event.BTProfile.ANY) {
				if (event.getBTProfile() != stateChangedProfile) {
					continue;
				}
			}
			if (event.getBTState() != state)
			switch (state) {
				case CONNECTED:
				case CONNECTING:
					break;
				default:
					continue;
			}

			event.getBTProfile();
			if (!(event.getBTProfile() == Event.BTProfile.ANY) && event.)

		}

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
				resid = VolumeLockValue.LOCK.getResource();
			}
			else {
				resid = VolumeLockValue.UNLOCK.getResource();

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
					resid = ClearAudioPlusStateValue.ON.getResource();
				}
				else {
					resid = ClearAudioPlusStateValue.OFF.getResource();
	
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
