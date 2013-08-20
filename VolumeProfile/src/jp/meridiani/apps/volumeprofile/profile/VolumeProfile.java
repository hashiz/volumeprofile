package jp.meridiani.apps.volumeprofile.profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import android.os.Parcel;
import android.os.Parcelable;

public class VolumeProfile implements Parcelable {
	private UUID mUuid;
	private String mName;
	private int mDisplayOrder;

	private RingerMode mRingerMode;
	private boolean mRingerModeLock;
	private int mAlarmVolume;
	private boolean mAlarmVolumeLock;
	private int mMusicVolume;
	private boolean mMusicVolumeLock;
	private int mRingVolume;
	private boolean mRingVolumeLock;
	private int mVoiceCallVolume;
	private boolean mVoiceCallVolumeLock;

	private static final String[] BACKUP_KEYS = new String[] {
		ProfileStore.KEY_UUID                ,
		ProfileStore.KEY_DISPLAYORDER        ,
		ProfileStore.KEY_PROFILENAME         ,
		ProfileStore.KEY_RINGERMODE          ,
		ProfileStore.KEY_RINGERMODELOCK      ,
		ProfileStore.KEY_ALARMVOLUME         ,
		ProfileStore.KEY_ALARMVOLUMELOCK     ,
		ProfileStore.KEY_MUSICVOLUME         ,
		ProfileStore.KEY_MUSICVOLUMELOCK     ,
		ProfileStore.KEY_RINGVOLUME          ,
		ProfileStore.KEY_RINGVOLUMELOCK      ,
		ProfileStore.KEY_VOICECALLVALUME     ,
		ProfileStore.KEY_VOICECALLVALUMELOCK ,
	};
	
	
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
		mRingerModeLock = false;
		mAlarmVolume = 0;
		mAlarmVolumeLock = false;
		mMusicVolume = 0;
		mMusicVolumeLock = false;
		mRingVolume = 0;
		mRingVolumeLock = false;
		mVoiceCallVolume = 0;
		mVoiceCallVolumeLock = false;
	}

	String getValue(String key) {
		if (key.equals(ProfileStore.KEY_UUID)) {
			return this.getUuid().toString();
		}
		else if (key.equals(ProfileStore.KEY_DISPLAYORDER)) {
			return Integer.toString(this.getDisplayOrder());
		}
		else if (key.equals(ProfileStore.KEY_PROFILENAME)) {
			return this.getName();
		}
		else if (key.equals(ProfileStore.KEY_RINGERMODE)) {
			return this.getRingerMode().name();
		}
		else if (key.equals(ProfileStore.KEY_RINGERMODELOCK)) {
			return Boolean.toString(this.getRingerModeLock());
		}
		else if (key.equals(ProfileStore.KEY_ALARMVOLUME)) {
			return Integer.toString(this.getAlarmVolume());
		}
		else if (key.equals(ProfileStore.KEY_ALARMVOLUMELOCK)) {
			return Boolean.toString(this.getAlarmVolumeLock());
		}
		else if (key.equals(ProfileStore.KEY_MUSICVOLUME)) {
			return Integer.toString(this.getMusicVolume());
		}
		else if (key.equals(ProfileStore.KEY_MUSICVOLUMELOCK)) {
			return Boolean.toString(this.getMusicVolumeLock());
		}
		else if (key.equals(ProfileStore.KEY_RINGVOLUME)) {
			return Integer.toString(this.getRingVolume());
		}
		else if (key.equals(ProfileStore.KEY_RINGVOLUMELOCK)) {
			return Boolean.toString(this.getRingVolumeLock());
		}
		else if (key.equals(ProfileStore.KEY_VOICECALLVALUME)) {
			return Integer.toString(this.getVoiceCallVolume());
		}
		else if (key.equals(ProfileStore.KEY_VOICECALLVALUMELOCK)) {
			return Boolean.toString(this.getVoiceCallVolumeLock());
		}
		return null;
	}

	void setValue(String key, String value) {
		if (key.equals(ProfileStore.KEY_UUID)) {
			this.setUuid(UUID.fromString(value)) ;
		}
		else if (key.equals(ProfileStore.KEY_DISPLAYORDER)) {
			this.setDisplayOrder(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_PROFILENAME)) {
			this.setName(value);
		}
		else if (key.equals(ProfileStore.KEY_RINGERMODE)) {
			this.setRingerMode(RingerMode.valueOf(value));
		}
		else if (key.equals(ProfileStore.KEY_RINGERMODELOCK)) {
			this.setRingerModeLock(Boolean.parseBoolean(value));
		}
		else if (key.equals(ProfileStore.KEY_ALARMVOLUME)) {
			this.setAlarmVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_ALARMVOLUMELOCK)) {
			this.setAlarmVolumeLock(Boolean.parseBoolean(value));
		}
		else if (key.equals(ProfileStore.KEY_MUSICVOLUME)) {
			this.setMusicVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_MUSICVOLUMELOCK)) {
			this.setMusicVolumeLock(Boolean.parseBoolean(value));
		}
		else if (key.equals(ProfileStore.KEY_RINGVOLUME)) {
			this.setRingVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_RINGVOLUMELOCK)) {
			this.setRingVolumeLock(Boolean.parseBoolean(value));
		}
		else if (key.equals(ProfileStore.KEY_VOICECALLVALUME)) {
			this.setVoiceCallVolume(Integer.parseInt(value));
		}
		else if (key.equals(ProfileStore.KEY_VOICECALLVALUMELOCK)) {
			this.setVoiceCallVolumeLock(Boolean.parseBoolean(value));
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
		case VOICE_CALL:
			setVoiceCallVolume(volume);
			break;
		}
	}

	public boolean getVolumeLock(StreamType type) {
		switch (type) {
		case ALARM:
			return getAlarmVolumeLock();
		case MUSIC:
			return getMusicVolumeLock();
		case RING:
			return getRingVolumeLock();
		case VOICE_CALL:
			return getVoiceCallVolumeLock();
		}
		return false;
	}

	public void setVolumeLock(StreamType type, boolean lock) {
		switch (type) {
		case ALARM:
			setAlarmVolumeLock(lock);
			break;
		case MUSIC:
			setMusicVolumeLock(lock);
			break;
		case RING:
			setRingVolumeLock(lock);
			break;
		case VOICE_CALL:
			setVoiceCallVolumeLock(lock);
			break;
		}
	}

	public UUID getUuid() {
		return mUuid;
	}

	public void setUuid(UUID uuid) {
		mUuid = uuid;
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

	public boolean getRingerModeLock() {
		return mRingerModeLock;
	}

	public void setRingerModeLock(boolean lock) {
		mRingerModeLock = lock;
	}

	public int getAlarmVolume() {
		return mAlarmVolume;
	}

	public void setAlarmVolume(int volume) {
		mAlarmVolume = volume;
	}

	public boolean getAlarmVolumeLock() {
		return mAlarmVolumeLock;
	}

	public void setAlarmVolumeLock(boolean lock) {
		mAlarmVolumeLock = lock;
	}

	public int getMusicVolume() {
		return mMusicVolume;
	}

	public void setMusicVolume(int volume) {
		mMusicVolume = volume;
	}

	public boolean getMusicVolumeLock() {
		return mMusicVolumeLock;
	}

	public void setMusicVolumeLock(boolean lock) {
		mMusicVolumeLock = lock;
	}

	public int getRingVolume() {
		return mRingVolume;
	}

	public void setRingVolume(int volume) {
		mRingVolume = volume;
	}

	public boolean getRingVolumeLock() {
		return mRingVolumeLock;
	}

	public void setRingVolumeLock(boolean lock) {
		mRingVolumeLock = lock;
	}

	public int getVoiceCallVolume() {
		return mVoiceCallVolume;
	}

	public void setVoiceCallVolume(int volume) {
		mVoiceCallVolume = volume;
	}

	public boolean getVoiceCallVolumeLock() {
		return mVoiceCallVolumeLock;
	}

	public void setVoiceCallVolumeLock(boolean lock) {
		mVoiceCallVolumeLock = lock;
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
    	out.writeInt(mRingerModeLock ? 1 : 0);
    	out.writeInt(mAlarmVolume);
    	out.writeInt(mAlarmVolumeLock ? 1 : 0);
    	out.writeInt(mMusicVolume);
    	out.writeInt(mMusicVolumeLock ? 1 : 0);
    	out.writeInt(mRingVolume);
    	out.writeInt(mRingVolumeLock ? 1 : 0);
    	out.writeInt(mVoiceCallVolume);
    	out.writeInt(mVoiceCallVolumeLock ? 1 : 0);
	}

	public VolumeProfile(Parcel in) {
		mUuid                = UUID.fromString(in.readString());
    	mName                = in.readString();
    	mDisplayOrder        = in.readInt();
    	mRingerMode          = RingerMode.valueOf(in.readString());
    	mRingerModeLock      = in.readInt() != 0;
    	mAlarmVolume         = in.readInt();
    	mAlarmVolumeLock     = in.readInt() != 0;
    	mMusicVolume         = in.readInt();
    	mMusicVolumeLock     = in.readInt() != 0;
    	mRingVolume          = in.readInt();
    	mRingVolumeLock      = in.readInt() != 0;
    	mVoiceCallVolume     = in.readInt();
    	mVoiceCallVolumeLock = in.readInt() != 0;
	}

    public static final Parcelable.Creator<VolumeProfile> CREATOR = new Parcelable.Creator<VolumeProfile>() {
		public VolumeProfile createFromParcel(Parcel in) {
		    return new VolumeProfile(in);
		}
		
		public VolumeProfile[] newArray(int size) {
		    return new VolumeProfile[size];
		}
    };

    // for backup
    public void writeToText(BufferedWriter out) throws IOException {
    	out.write("<profile>");
    	out.newLine();
    	for (String key : BACKUP_KEYS) {
    		String value = getValue(key);
    		if (value != null) {
    			out.write(key + '=' + value );
    			out.newLine();
    		}
    	}
    	out.write("</profile>");
    	out.newLine();
    }

    public static VolumeProfile createFromText(BufferedReader in) throws IOException {
    	VolumeProfile profile = null;
    	boolean inProfile = false;
    	String line;
		while ((line = in.readLine()) != null) {
			if (inProfile ) {
				if (line.equals("</profile>")) {
					break;
				}
				String[] tmp = line.split("=", 2);
				if (tmp.length < 2) {
					continue;
				}
				profile.setValue(tmp[0], tmp[1]);
			}
			else {
				if (line.equals("<profile>")) {
					inProfile = true;
					profile = new VolumeProfile();
				}
			}
		}
    	return profile;
    }
}
