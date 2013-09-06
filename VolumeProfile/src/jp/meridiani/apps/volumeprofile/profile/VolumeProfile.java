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

	private static final String PROFILE_START = "<profile>";
	private static final String PROFILE_END   = "</profile>";

	public static enum Key {
		UUID,
		DISPLAYORDER,
		PROFILENAME,
		RINGERMODE,
		RINGERMODELOCK,
		ALARMVOLUME,
		ALARMVOLUMELOCK,
		MUSICVOLUME,
		MUSICVOLUMELOCK,
		RINGVOLUME,
		RINGVOLUMELOCK,
		VOICECALLVALUME,
		VOICECALLVALUMELOCK;

		private final static Key[] sKeys;
		private final static Key[] sDataKeys;
		private final static int sSkip = 2;
		static {
			Key[] values = values();
			sKeys = new Key[values.length];
			sDataKeys = new Key[values.length-sSkip];
			for (int i = 0; i < values.length; i++) {
				Key key = values[i];
				sKeys[i] = key;
				if (i >= sSkip) {
					sDataKeys[i-sSkip] = key;
				}
			}
		};

		public static Key[] getKeys() {
			return sKeys;
		}

		public static Key[] getDataKeys() {
			return sDataKeys;
		}
	}

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

	String getValue(Key key) {
		switch (key) {
		case UUID:
			return getUuid().toString();
		case DISPLAYORDER:
			return Integer.toString(getDisplayOrder());
		case PROFILENAME:
			return getName();
		case RINGERMODE:
			return getRingerMode().name();
		case RINGERMODELOCK:
			return Boolean.toString(getRingerModeLock());
		case ALARMVOLUME:
			return Integer.toString(getAlarmVolume());
		case ALARMVOLUMELOCK:
			return Boolean.toString(getAlarmVolumeLock());
		case MUSICVOLUME:
			return Integer.toString(getMusicVolume());
		case MUSICVOLUMELOCK:
			return Boolean.toString(getMusicVolumeLock());
		case RINGVOLUME:
			return Integer.toString(getRingVolume());
		case RINGVOLUMELOCK:
			return Boolean.toString(getRingVolumeLock());
		case VOICECALLVALUME:
			return Integer.toString(getVoiceCallVolume());
		case VOICECALLVALUMELOCK:
			return Boolean.toString(getVoiceCallVolumeLock());
		}
		return null;
	}

	void setValue(String key, String value) {
		Key k;
		try {
			k = Key.valueOf(key);
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			return;
		}
		setValue(k, value);
	}

	void setValue(Key key, String value) {
		switch (key) {
		case UUID:
			setUuid(UUID.fromString(value)) ;
			break;
		case DISPLAYORDER:
			setDisplayOrder(Integer.parseInt(value));
			break;
		case PROFILENAME:
			setName(value);
			break;
		case RINGERMODE:
			setRingerMode(RingerMode.valueOf(value));
			break;
		case RINGERMODELOCK:
			setRingerModeLock(Boolean.parseBoolean(value));
			break;
		case ALARMVOLUME:
			setAlarmVolume(Integer.parseInt(value));
			break;
		case ALARMVOLUMELOCK:
			setAlarmVolumeLock(Boolean.parseBoolean(value));
			break;
		case MUSICVOLUME:
			setMusicVolume(Integer.parseInt(value));
			break;
		case MUSICVOLUMELOCK:
			setMusicVolumeLock(Boolean.parseBoolean(value));
			break;
		case RINGVOLUME:
			setRingVolume(Integer.parseInt(value));
			break;
		case RINGVOLUMELOCK:
			setRingVolumeLock(Boolean.parseBoolean(value));
			break;
		case VOICECALLVALUME:
			setVoiceCallVolume(Integer.parseInt(value));
			break;
		case VOICECALLVALUMELOCK:
			setVoiceCallVolumeLock(Boolean.parseBoolean(value));
			break;
		}
	}

	static Key[] listDataKeys() {
		return Key.getDataKeys();
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
    	out.write(PROFILE_START);
    	out.newLine();
    	for (Key key : Key.getKeys()) {
    		String value = getValue(key);
    		if (value != null) {
    			out.write(key.name() + '=' + value );
    			out.newLine();
    		}
    	}
    	out.write(PROFILE_END);
    	out.newLine();
    }

    public static VolumeProfile createFromText(BufferedReader rdr) throws IOException {
    	VolumeProfile profile = null;
    	boolean started = false;
    	String line;
		while ((line = rdr.readLine()) != null) {
			if (started) {
				if (PROFILE_END.equals(line)) {
					break;
				}
				String[] tmp = line.split("=", 2);
				if (tmp.length < 2) {
					continue;
				}
				profile.setValue(tmp[0], tmp[1]);
			}
			else {
				if (PROFILE_START.equals(line)) {
					started = true;
					profile = new VolumeProfile();
				}
			}
		}
    	return profile;
    }
}
