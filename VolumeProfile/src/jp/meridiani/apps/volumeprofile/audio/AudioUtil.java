package jp.meridiani.apps.volumeprofile.audio;

import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.Context;
import android.media.AudioManager;

public class AudioUtil {
	private Context      mContext;
	private AudioManager mAmgr;

	public AudioUtil(Context context) {
		mContext = context;
		mAmgr    = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void applyProfile(VolumeProfile profile) {
		switch (profile.getRingerMode()) {
		case normal:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case vibrate:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case sirent:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}

		mAmgr.setStreamVolume(AudioManager.STREAM_ALARM, profile.getAlarmVolume(), 0);
		mAmgr.setStreamVolume(AudioManager.STREAM_DTMF,  profile.getDTMFVolume(), 0);
		mAmgr.setStreamVolume(AudioManager.STREAM_MUSIC, profile.getMusicVolume(), 0);
		mAmgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, profile.getNotificationVolume(), 0);
		mAmgr.setStreamVolume(AudioManager.STREAM_RING,  profile.getRingVolume(), 0);
		mAmgr.setStreamVolume(AudioManager.STREAM_SYSTEM, profile.getSystemVolume(), 0);
		mAmgr.setStreamVolume(AudioManager.STREAM_VOICE_CALL, profile.getVoiceCallVolume(), 0);
	}

	public int getMaxVolume(int streamType) {
		return mAmgr.getStreamMaxVolume(streamType);
	}

	public int getVolume(int streamType) {
		return mAmgr.getStreamVolume(streamType);
	}

	public VolumeProfile.RingerMode getRingerMode() {
		switch (mAmgr.getRingerMode()) {
		case AudioManager.RINGER_MODE_NORMAL:
			return VolumeProfile.RingerMode.normal;
		case AudioManager.RINGER_MODE_VIBRATE:
			return VolumeProfile.RingerMode.vibrate;
		case AudioManager.RINGER_MODE_SILENT:
			return VolumeProfile.RingerMode.sirent;
		}
		return VolumeProfile.RingerMode.normal;
	}

}
