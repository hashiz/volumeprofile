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
import jp.meridiani.apps.volumeprofile.pluginapi.BundleUtil;
import jp.meridiani.apps.volumeprofile.pluginapi.PluginEditActivity;
import jp.meridiani.apps.volumeprofile.pluginapi.PluginFireReceiver;
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
					state = Event.BTState.CONNECTED;
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					state = Event.BTState.DISCONNECTED;
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

			if (event.getBTState() != Event.BTState.ANY) {
				if (event.getBTState() != state) {
					continue;
				}
			}
			Intent fire = new Intent(context, PluginFireReceiver.class);
			BundleUtil bundle = new BundleUtil();
			bundle.setProfileId(event.getVProfileId());
			bundle.setClearAudioPlusState(event.getClearAudioPlus());
			fire.putExtras(bundle.getBundle());
			fire.setAction(com.twofortyfouram.locale.api.Intent.ACTION_FIRE_SETTING);
			context.startActivity(fire);
			break;
		}
    }
}
