package jp.meridiani.apps.volumeprofile.profile;

import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;

public class VolumeProfile implements Parcelable {
	private UUID mUuid;
	private String mName;
	private int mDisplayOrder;

	private RingerMode mRingerMode;
	private int mAlarmVolume;
	private int mMusicVolume;
	private int mRingVolume;
	private int mSystemVolume;
	private int mVoiceCallVolume;

	VolumeProfile() {
		this((UUID)null);
	}

	VolumeProfile(UUID uuid) {
		if (uuid == null) {
			uuid = UUID.randomUUID();
		}
		
		mUuid = uuid;
		mName = "";

		mRingerMode = RingerMode.NORMAL;
		mAlarmVolume = 0;
		mMusicVolume = 0;
		mRingVolume = 0;
		mSystemVolume = 0;
		mVoiceCallVolume = 0;
	}

	String getValue(String key) {
		if (key.equals(ProfileStore.KEY_PROFILENAME)) {
			return this.getName();
		}
		else if (key.equals(ProfileStore.KEY_RINGERMODE)) {
			return this.getRingerMode().name();
		}
		else if (key.equals(ProfileStore.KEY_ALARMVOLUME)) {
			return Integer.toString(this.getAlarmVolume());
		}
		else if (key.equals(ProfileStore.KEY_MUSICVOLUME)) {
			return Integer.toString(this.getMusicVolume());
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
			this.setName(value);
		}
		else if (key.equals(ProfileStore.KEY_RINGERMODE)) {
			this.setRingerMode(RingerMode.valueOf(value));
		}
		else if (key.equals(ProfileStore.KEY_ALARMVOLUME)) {
			this.setAlarmVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_MUSICVOLUME)) {
			this.setMusicVolume(Integer.parseInt(value));
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
		case MUSIC:
			return getMusicVolume();
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
		case MUSIC:
			setMusicVolume(volume);
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

	public UUID getUuid() {
		return mUuid;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public int getDisplayOrder() {
		return mDisplayOrder;
	}

	public void setDisplayOrder(int order) {
		mDisplayOrder = order;
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

	public int getMusicVolume() {
		return mMusicVolume;
	}

	public void setMusicVolume(int volume) {
		mMusicVolume = volume;
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
		return this.getName();
	}

	@Override
	public int describeContents() {
		return 0;
	}

    @Override
	public void writeToParcel(Parcel out, int flags) {
    	out.writeString(mUuid.toString());
    	out.writeString(mName);
    	out.writeInt(mDisplayOrder);
    	out.writeString(mRingerMode.name());
    	out.writeInt(mAlarmVolume);
    	out.writeInt(mMusicVolume);
    	out.writeInt(mRingVolume);
    	out.writeInt(mSystemVolume);
    	out.writeInt(mVoiceCallVolume);
	}

	public VolumeProfile(Parcel in) {
		mUuid            = UUID.fromString(in.readString());
    	mName            = in.readString();
    	mDisplayOrder    = in.readInt();
    	mRingerMode      = RingerMode.valueOf(in.readString());
    	mAlarmVolume     = in.readInt();
    	mMusicVolume     = in.readInt();
    	mRingVolume      = in.readInt();
    	mSystemVolume    = in.readInt();
    	mVoiceCallVolume = in.readInt();
	}

    public static final Parcelable.Creator<VolumeProfile> CREATOR = new Parcelable.Creator<VolumeProfile>() {
		public VolumeProfile createFromParcel(Parcel in) {
		    return new VolumeProfile(in);
		}
		
		public VolumeProfile[] newArray(int size) {
		    return new VolumeProfile[size];
		}
    };
}
