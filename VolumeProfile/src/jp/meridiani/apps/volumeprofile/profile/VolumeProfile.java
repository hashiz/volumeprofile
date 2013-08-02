package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;

public class VolumeProfile {
	private int mProfileId;

	private String mProfileName;
	private RingerMode mRingerMode;
	private int mAlarmVolume;
	private int mDTMFVolume;
	private int mMusicVolume;
	private int mNotificationVolume;
	private int mRingVolume;
	private int mSystemVolume;
	private int mVoiceCallVolume;

	VolumeProfile() {
		this(-1);
	}

	VolumeProfile(int id) {
		mProfileId = id;
		mProfileName = "";

		mRingerMode = RingerMode.NORMAL;
		mAlarmVolume = 0;
		mDTMFVolume = 0;
		mMusicVolume = 0;
		mNotificationVolume = 0;
		mRingVolume = 0;
		mSystemVolume = 0;
		mVoiceCallVolume = 0;
	}

	String getValue(String key) {
		if (key.equals(ProfileStore.KEY_PROFILENAME)) {
			return this.getProfileName();
		}
		else if (key.equals(ProfileStore.KEY_RINGERMODE)) {
			return this.getRingerMode().name();
		}
		else if (key.equals(ProfileStore.KEY_ALARMVOLUME)) {
			return Integer.toString(this.getAlarmVolume());
		}
		else if (key.equals(ProfileStore.KEY_DTMFVOLUME)) {
			return Integer.toString(this.getDTMFVolume());
		}
		else if (key.equals(ProfileStore.KEY_MUSICVOLUME)) {
			return Integer.toString(this.getMusicVolume());
		}
		else if (key.equals(ProfileStore.KEY_NOTIFICATIONVOLUME)) {
			return Integer.toString(this.getNotificationVolume());
		}
		else if (key.equals(ProfileStore.KEY_RINGVOLUME)) {
			return Integer.toString(this.getRingVolume());
		}
		else if (key.equals(ProfileStore.KEY_SYSTEMVOLUME)) {
			return Integer.toString(this.getSystemVolume());
		}
		else if (key.equals(ProfileStore.KEY_VOICECALLVALUME)) {
			return Integer.toString(this.getVoiceCallVolume());
		}
		return "";
	}

	void setValue(String key, String value) {
		if (key.equals(ProfileStore.KEY_PROFILENAME)) {
			this.setProfileName(value);
		}
		else if (key.equals(ProfileStore.KEY_RINGERMODE)) {
			this.setRingerMode(RingerMode.valueOf(value));
		}
		else if (key.equals(ProfileStore.KEY_ALARMVOLUME)) {
			this.setAlarmVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_DTMFVOLUME)) {
			this.setDTMFVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_MUSICVOLUME)) {
			this.setMusicVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_NOTIFICATIONVOLUME)) {
			this.setNotificationVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_RINGVOLUME)) {
			this.setRingVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_SYSTEMVOLUME)) {
			this.setSystemVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_VOICECALLVALUME)) {
			this.setVoiceCallVolume(Integer.parseInt(value));
		}
	}

	public int getVolume(StreamType type) {
		switch (type) {
		case ALARM:
			return getAlarmVolume();
		case DTMF:
			return getDTMFVolume();
		case MUSIC:
			return getMusicVolume();
		case NOTIFICATION:
			return getNotificationVolume();
		case RING:
			return getRingVolume();
		case SYSTEM:
			return getSystemVolume();
		case VOICE_CALL:
			return getVoiceCallVolume();
		}
		return 0;
	}

	public void setVolume(StreamType type, int volume) {
		switch (type) {
		case ALARM:
			setAlarmVolume(volume);
			break;
		case DTMF:
			setDTMFVolume(volume);
			break;
		case MUSIC:
			setMusicVolume(volume);
			break;
		case NOTIFICATION:
			setNotificationVolume(volume);
			break;
		case RING:
			setRingVolume(volume);
			break;
		case SYSTEM:
			setSystemVolume(volume);
			break;
		case VOICE_CALL:
			setVoiceCallVolume(volume);
			break;
		}
	}

	public int getProfileId() {
		return mProfileId;
	}

	public String getProfileName() {
		return mProfileName;
	}

	public void setProfileName(String name) {
		mProfileName = name;
	}

	public RingerMode getRingerMode() {
		return mRingerMode;
	}

	public void setRingerMode(RingerMode mode) {
		mRingerMode = mode;
	}

	public int getAlarmVolume() {
		return mAlarmVolume;
	}

	public void setAlarmVolume(int volume) {
		mAlarmVolume = volume;
	}

	public int getDTMFVolume() {
		return mDTMFVolume;
	}

	public void setDTMFVolume(int volume) {
		mDTMFVolume = volume;
	}

	public int getMusicVolume() {
		return mMusicVolume;
	}

	public void setMusicVolume(int volume) {
		mMusicVolume = volume;
	}

	public int getNotificationVolume() {
		return mNotificationVolume;
	}

	public void setNotificationVolume(int volume) {
		mNotificationVolume = volume;
	}

	public int getRingVolume() {
		return mRingVolume;
	}

	public void setRingVolume(int volume) {
		mRingVolume = volume;
	}

	public int getSystemVolume() {
		return mSystemVolume;
	}

	public void setSystemVolume(int volume) {
		mSystemVolume = volume;
	}

	public int getVoiceCallVolume() {
		return mVoiceCallVolume;
	}

	public void setVoiceCallVolume(int volume) {
		mVoiceCallVolume = volume;
	}

	@Override
	public String toString() {
		return this.getProfileName();
	}
}
