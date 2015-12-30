package jp.meridiani.apps.volumeprofile.profile;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.DisplayToast;
import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.VolumeChangedReceiver;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

public class Receiver extends VolumeChangedReceiver {

	ProfileStore mProfileStore = null;
	Context      mContext      = null;
	boolean     mDisplayToast = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		mProfileStore = ProfileStore.getInstance(context);
		if (!mProfileStore.isVolumeLocked()) {
			return;
		}
		mContext = context;
		mDisplayToast = Prefs.getInstance(context).isDisplayToastOnVolumeLock();
		String action = intent.getAction();
		if (VOLUME_CHANGED_ACTION.equals(action)) {
		    int type = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
		    if (!AudioUtil.isSupportedType(type)) {
		    	return;
		    }
		    int volume = intent.getIntExtra(EXTRA_VOLUME_STREAM_VALUE, -1);
		    if (volume < 0) {
		    	return;
		    }
		    int prevVolume = intent.getIntExtra(EXTRA_PREV_VOLUME_STREAM_VALUE, -1);
		    if (prevVolume < 0) {
		    	return;
		    }
		    if (volume == prevVolume) {
		    	return;
		    }
			restoreVolume(type, volume);
		}
		else if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(action)) {
		    int mode = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1);
		    if (!AudioUtil.isSupportedMode(mode)) {
		    	return;
		    }
		    restoreRingerMode(mode);
		}
	}

	private void restoreVolume(int type, int volume) {
		UUID profileId = mProfileStore.getCurrentProfile();
		if (profileId == null) {
			return;
		}
		VolumeProfile profile = null;
		try {
			profile = mProfileStore.loadProfile(profileId);
		}
		catch (ProfileNotFoundException e) {
			return;
		}
		AudioUtil.StreamType streamType = AudioUtil.getStreamType(type);
		if (profile.getVolumeLock(streamType)) {
			if ( volume == profile.getVolume(streamType)) {
				return;
			}
			new AudioUtil(mContext).setVolume(streamType, profile.getVolume(streamType), false);
			if (mDisplayToast) {
				DisplayToast.show(mContext, R.string.msg_volume_locked, Toast.LENGTH_SHORT);
			}
		}
	}

	private void restoreRingerMode(int mode) {
		UUID profileId = mProfileStore.getCurrentProfile();
		if (profileId == null) {
			return;
		}
		VolumeProfile profile = null;
		try {
			profile = mProfileStore.loadProfile(profileId);
		}
		catch (ProfileNotFoundException e) {
			return;
		}
		AudioUtil.RingerMode ringerMode = AudioUtil.getRingerMode(mode);
		if (profile.getRingerModeLock()) {
			if ( ringerMode.equals(profile.getRingerMode())) {
				return;
			}
			new AudioUtil(mContext).setRingerMode(profile.getRingerMode());
			if (mDisplayToast) {
				DisplayToast.show(mContext, R.string.msg_volume_locked, Toast.LENGTH_SHORT);
			}
		}
	}
}
