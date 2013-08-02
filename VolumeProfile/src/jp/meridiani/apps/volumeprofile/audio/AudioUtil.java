package jp.meridiani.apps.volumeprofile.audio;

import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.Context;
import android.media.AudioManager;

public class AudioUtil {
	private Context      mContext;
	private AudioManager mAmgr;

	public static enum RingerMode {
		NORMAL,
		VIBRATE,
		SIRENT
	}

	public static enum StreamType {
		ALARM,
		DTMF,
		MUSIC,
		NOTIFICATION,
		RING,
		SYSTEM,
		VOICE_CALL,
	}

	public AudioUtil(Context context) {
		mContext = context;
		mAmgr    = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void applyProfile(VolumeProfile profile) {
		switch (profile.getRingerMode()) {
		case NORMAL:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case VIBRATE:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case SIRENT:
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

	private int getStreamType(StreamType type) {
		switch (type) {
		case ALARM:
			return AudioManager.STREAM_ALARM;
		case DTMF:
			return AudioManager.STREAM_DTMF;
		case MUSIC:
			return AudioManager.STREAM_MUSIC;
		case NOTIFICATION:
			return AudioManager.STREAM_NOTIFICATION;
		case RING:
			return AudioManager.STREAM_RING;
		case SYSTEM:
			return AudioManager.STREAM_SYSTEM;
		case VOICE_CALL:
			return AudioManager.STREAM_VOICE_CALL;
		}
		return AudioManager.STREAM_RING;
	}

	private StreamType getStreamType(int streamType) {
		switch (streamType) {
		case AudioManager.STREAM_ALARM:
			return StreamType.ALARM;
		case AudioManager.STREAM_DTMF:
			return StreamType.DTMF;
		case AudioManager.STREAM_MUSIC:
			return StreamType.MUSIC;
		case AudioManager.STREAM_NOTIFICATION:
			return StreamType.NOTIFICATION;
		case AudioManager.STREAM_RING:
			return StreamType.RING;
		case AudioManager.STREAM_SYSTEM:
			return StreamType.SYSTEM;
		case AudioManager.STREAM_VOICE_CALL:
			return StreamType.VOICE_CALL;
		}
		return StreamType.RING;
	}

	public int getMaxVolume(StreamType type) {
		return mAmgr.getStreamMaxVolume(getStreamType(type));
	}

	public int getVolume(StreamType type) {
		return mAmgr.getStreamVolume(getStreamType(type));
	}

	public RingerMode getRingerMode() {
		switch (mAmgr.getRingerMode()) {
		case AudioManager.RINGER_MODE_NORMAL:
			return RingerMode.NORMAL;
		case AudioManager.RINGER_MODE_VIBRATE:
			return RingerMode.VIBRATE;
		case AudioManager.RINGER_MODE_SILENT:
			return RingerMode.SIRENT;
		}
		return RingerMode.NORMAL;
	}

	public void setRingerMode(RingerMode mode) {
		switch (mode) {
		case NORMAL:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case VIBRATE:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case SIRENT:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			break;
		}
	}

	public void setVolume(StreamType type, int volume) {
		mAmgr.setStreamVolume(getStreamType(type), volume, 0);
	}

}
