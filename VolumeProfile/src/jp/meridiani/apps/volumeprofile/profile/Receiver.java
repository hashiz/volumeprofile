package jp.meridiani.apps.volumeprofile.profile;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.VolumeChangedReceiver;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class Receiver extends VolumeChangedReceiver {

	ProfileStore mProfileStore = null;
	Context      mContext      = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mProfileStore = ProfileStore.getInstance(context);
		if (!mProfileStore.isVolumeLocked()) {
			return;
		}
		mContext = context;
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
		VolumeProfile profile = mProfileStore.loadProfile(profileId);
		if (profile == null) {
			return;
		}
		AudioUtil.StreamType streamType = AudioUtil.getStreamType(type);
		if ( volume == profile.getVolume(streamType)) {
			return;
		}
		if (profile.getVolumeLock(streamType)) {
			new AudioUtil(mContext).setVolume(streamType, profile.getVolume(streamType), false);
		}
	}

	private void restoreRingerMode(int mode) {
		UUID profileId = mProfileStore.getCurrentProfile();
		if (profileId == null) {
			return;
		}
		VolumeProfile profile = mProfileStore.loadProfile(profileId);
		if (profile == null) {
			return;
		}
		AudioUtil.RingerMode ringerMode = AudioUtil.getRingerMode(mode);
		if ( ringerMode.equals(profile.getRingerMode())) {
			return;
		}
		if (profile.getRingerModeLock()) {
			new AudioUtil(mContext).setRingerMode(profile.getRingerMode());
		}
	}
}
