package jp.meridiani.apps.volumeprofile.audio;

import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.Context;
import android.media.AudioManager;

public class AudioUtil {
	private Context      mContext;
	private AudioManager mAmgr;

	public static enum RingerMode {
		NORMAL,
		VIBRATE,
		SILENT
	}

	public static enum StreamType {
		ALARM,
		MUSIC,
		RING,
		NOTIFICATION,
		VOICE_CALL,
	}

	public static int[] supportStreams = {
		AudioManager.STREAM_ALARM,
		AudioManager.STREAM_MUSIC,
		AudioManager.STREAM_RING,
		AudioManager.STREAM_NOTIFICATION,
		AudioManager.STREAM_VOICE_CALL,
	};

	public AudioUtil(Context context) {
		mContext = context;
		mAmgr    = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void applyProfile(VolumeProfile profile) {
		for (int streamType : supportStreams ) {
			int volume = profile.getVolume(getStreamType(streamType));
			if (streamType == AudioManager.STREAM_NOTIFICATION &&
					Prefs.getInstance(mContext).isVolumeLinkNotification()) {
				volume = profile.getRingVolume();
			}
			mAmgr.setStreamVolume(streamType, volume, 0);
		}
		switch (profile.getRingerMode()) {
		case NORMAL:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case VIBRATE:
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case SILENT:
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}
	}

	public VolumeProfile getVolumes(VolumeProfile profile) {
		switch (mAmgr.getRingerMode()) {
		case AudioManager.RINGER_MODE_NORMAL:
			profile.setRingerMode(RingerMode.NORMAL);
			break;
		case AudioManager.RINGER_MODE_VIBRATE:
			profile.setRingerMode(RingerMode.VIBRATE);
			break;
		case AudioManager.RINGER_MODE_SILENT:
			profile.setRingerMode(RingerMode.SILENT);
		}

		for (int streamType : supportStreams ) {
			profile.setVolume(getStreamType(streamType), mAmgr.getStreamVolume(streamType));
		}
		return profile;
	}

	public static int getStreamType(StreamType type) {
		switch (type) {
		case ALARM:
			return AudioManager.STREAM_ALARM;
		case MUSIC:
			return AudioManager.STREAM_MUSIC;
		case RING:
			return AudioManager.STREAM_RING;
		case NOTIFICATION:
			return AudioManager.STREAM_NOTIFICATION;
		case VOICE_CALL:
			return AudioManager.STREAM_VOICE_CALL;
		}
		return AudioManager.STREAM_RING;
	}

	public static StreamType getStreamType(int streamType) {
		switch (streamType) {
		case AudioManager.STREAM_ALARM:
			return StreamType.ALARM;
		case AudioManager.STREAM_MUSIC:
			return StreamType.MUSIC;
		case AudioManager.STREAM_RING:
			return StreamType.RING;
		case AudioManager.STREAM_NOTIFICATION:
			return StreamType.NOTIFICATION;
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
			return RingerMode.SILENT;
		}
		return RingerMode.NORMAL;
	}

	public void setRingerMode(RingerMode mode) {
		switch (mode) {
		case NORMAL:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case VIBRATE:
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case SILENT:
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			break;
		}
	}

	public void setVolume(StreamType type, int volume, boolean playsound) {
		int flag = 0;
		if (playsound) {
			flag = AudioManager.FLAG_PLAY_SOUND;
		}
		mAmgr.setStreamVolume(getStreamType(type), volume, flag);
	}

	public static boolean isSupportedType(int streamType) {
		for (int supported : supportStreams) {
			if (streamType == supported) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSupportedMode(int ringerMode) {
		switch (ringerMode) {
		case AudioManager.RINGER_MODE_NORMAL:
		case AudioManager.RINGER_MODE_VIBRATE:
		case AudioManager.RINGER_MODE_SILENT:
			return true;
		}
		return false;
	}

	public static RingerMode getRingerMode(int ringerMode) {
		switch (ringerMode) {
		case AudioManager.RINGER_MODE_NORMAL:
			return RingerMode.NORMAL;
		case AudioManager.RINGER_MODE_VIBRATE:
			return RingerMode.VIBRATE;
		case AudioManager.RINGER_MODE_SILENT:
			return RingerMode.SILENT;
		}
		return RingerMode.NORMAL;
	}
}
