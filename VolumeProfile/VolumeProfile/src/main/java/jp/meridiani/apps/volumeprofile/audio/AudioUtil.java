package jp.meridiani.apps.volumeprofile.audio;

import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class AudioUtil {
	private static Object sLock = new Object();

	private Context      mContext;
	private AudioManager mAmgr;
	private ClearAudioPlus mClearAudioPlus;

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
		SYSTEM,
	}

	public static int[] supportStreams = {
		AudioManager.STREAM_ALARM,
		AudioManager.STREAM_MUSIC,
		AudioManager.STREAM_RING,
		AudioManager.STREAM_NOTIFICATION,
		AudioManager.STREAM_VOICE_CALL,
		AudioManager.STREAM_SYSTEM,
	};

	public AudioUtil(Context context) {
		mContext = context;
		mAmgr    = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
		mClearAudioPlus = new ClearAudioPlus(context, mAmgr);
		
	}
	
	public void applyProfile(VolumeProfile profile) {
		synchronized (sLock) {
			Prefs prefs = Prefs.getInstance(mContext);
			setRingerMode(profile.getRingerMode());
			RingerMode r = getRingerMode();
			for (StreamType streamType : StreamType.values()) {
				int volume = profile.getVolume(streamType);
				switch (streamType) {
				case RING:
					if (r == RingerMode.SILENT || r == RingerMode.VIBRATE) {
						continue;
					}
					break;
				case NOTIFICATION:
					if (prefs.isVolumeLinkNotification()) {
						continue;
					}
					break;
				case SYSTEM:
					if (prefs.isVolumeLinkSystem()) {
						continue;
					}
					break;
				default:
					break;
				}
				setVolume(streamType,  volume, false);
			}
		}
	}

	public VolumeProfile getVolumes(VolumeProfile profile) {
		profile.setRingerMode(getRingerMode());
		for (StreamType streamType : StreamType.values() ) {
			profile.setVolume(streamType, getVolume(streamType));
		}
		return profile;
	}

	public boolean detectVolumeLinkNotification() {
		int prevRingerMode = mAmgr.getRingerMode();
		int prevRinger = mAmgr.getStreamVolume(AudioManager.STREAM_RING);
		int prevNotification = mAmgr.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

		try {
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, 1, 0);
			mAmgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 1, 0);
	
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, 2, 0);
			if (mAmgr.getStreamVolume(AudioManager.STREAM_RING) == 
					mAmgr.getStreamVolume(AudioManager.STREAM_NOTIFICATION)) {
				return true;
			}
			return false;
		}
		finally {
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, prevRinger, 0);
			mAmgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, prevNotification, 0);
			mAmgr.setRingerMode(prevRingerMode);
		}
	}

	public boolean detectVolumeLinkSystem() {
		int prevRingerMode = mAmgr.getRingerMode();
		int prevRinger = mAmgr.getStreamVolume(AudioManager.STREAM_RING);
		int prevSystem = mAmgr.getStreamVolume(AudioManager.STREAM_SYSTEM);

		try {
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, 1, 0);
			mAmgr.setStreamVolume(AudioManager.STREAM_SYSTEM, 1, 0);
	
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, 2, 0);
			if (mAmgr.getStreamVolume(AudioManager.STREAM_RING) == 
					mAmgr.getStreamVolume(AudioManager.STREAM_SYSTEM)) {
				return true;
			}
			return false;
		}
		finally {
			mAmgr.setStreamVolume(AudioManager.STREAM_RING, prevRinger, 0);
			mAmgr.setStreamVolume(AudioManager.STREAM_SYSTEM, prevSystem, 0);
			mAmgr.setRingerMode(prevRingerMode);
		}
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
		case SYSTEM:
			return AudioManager.STREAM_SYSTEM;
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
		case AudioManager.STREAM_SYSTEM:
			return StreamType.SYSTEM;
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
		if (getRingerMode() == mode) {
			return;
		}
		int ringerMode = 0;
		switch (mode) {
		case NORMAL:
			ringerMode = AudioManager.RINGER_MODE_NORMAL;
			break;
		case VIBRATE:
			ringerMode = AudioManager.RINGER_MODE_VIBRATE;
			break;
		case SILENT:
			if (Prefs.getInstance(mContext).isVolumeLinkRingermode()) {
				mAmgr.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
			}
			ringerMode = AudioManager.RINGER_MODE_SILENT;
			break;
		}
		mAmgr.setRingerMode(ringerMode);
		if (mAmgr.getRingerMode() != ringerMode) {
			Log.d("setRingerMode", "can't set ringermode");
		}
	}

	public void setVolume(StreamType type, int volume, boolean playsound) {
		int curVol = getVolume(type);
		if (curVol == volume) {
			return;
		}
		// check ringer mode and do not set ringer volume
		if (type == StreamType.RING || type == StreamType.NOTIFICATION) {
			RingerMode curRingerMode = getRingerMode();
			switch (curRingerMode) {
			case NORMAL:
				break;
			case SILENT:
			case VIBRATE:
				return;
			}
		}
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

	public boolean isSupportedClearAudioPlus() {
		return mClearAudioPlus.isSupported();
	}

	public boolean getClearAudioPlusState() {
		return mClearAudioPlus.getState();
	}

	public void setClearAudioPlusState(boolean enabled) {
		mClearAudioPlus.setState(enabled);
	}
}
