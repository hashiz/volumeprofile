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
		SILENT
	}

	public static enum StreamType {
		ALARM,
		MUSIC,
		RING,
		VOICE_CALL,
	}

	public static int[] supportStreams = {
		AudioManager.STREAM_ALARM,
		AudioManager.STREAM_MUSIC,
		AudioManager.STREAM_RING,
		AudioManager.STREAM_VOICE_CALL,
	};

	public AudioUtil(Context context) {
		mContext = context;
		mAmgr    = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void applyProfile(VolumeProfile profile) {
		for (int streamType : supportStreams ) {
			mAmgr.setStreamVolume(streamType, profile.getVolume(getStreamType(streamType)), 0);
		}
		switch (profile.getRingerMode()) {
		case NORMAL:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case VIBRATE:
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case SILENT:
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

	private int getStreamType(StreamType type) {
		switch (type) {
		case ALARM:
			return AudioManager.STREAM_ALARM;
		case MUSIC:
			return AudioManager.STREAM_MUSIC;
		case RING:
			return AudioManager.STREAM_RING;
		case VOICE_CALL:
			return AudioManager.STREAM_VOICE_CALL;
		}
		return AudioManager.STREAM_RING;
	}

	private StreamType getStreamType(int streamType) {
		switch (streamType) {
		case AudioManager.STREAM_ALARM:
			return StreamType.ALARM;
		case AudioManager.STREAM_MUSIC:
			return StreamType.MUSIC;
		case AudioManager.STREAM_RING:
			return StreamType.RING;
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
			mAmgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case SILENT:
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

}
