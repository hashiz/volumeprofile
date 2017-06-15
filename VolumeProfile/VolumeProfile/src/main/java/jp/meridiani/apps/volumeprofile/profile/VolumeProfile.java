package jp.meridiani.apps.volumeprofile.profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.DataHolder;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import android.os.Parcel;
import android.os.Parcelable;

public class VolumeProfile extends DataHolder {

	private static final String TAG_START = "<profile>";
	private static final String TAG_END = "</profile>";
	private static final String PROFILEID = "PROFILEID";
	private static final String DISPLAYORDER = "DISPLAYORDER";
	static final String PROFILENAME = "PROFILENAME";
	private static final String RINGERMODE = "RINGERMODE";
	private static final String RINGERMODELOCK = "RINGERMODELOCK";
	private static final String ALARMVOLUME = "ALARMVOLUME";
	private static final String ALARMVOLUMELOCK = "ALARMVOLUMELOCK";
	private static final String MUSICVOLUME = "MUSICVOLUME";
	private static final String MUSICVOLUMELOCK = "MUSICVOLUMELOCK";
	private static final String RINGVOLUME = "RINGVOLUME";
	private static final String RINGVOLUMELOCK = "RINGVOLUMELOCK";
	private static final String NOTIFICATIONVOLUME = "NOTIFICATIONVOLUME";
	private static final String NOTIFICATIONVOLUMELOCK = "NOTIFICATIONVOLUMELOCK";
	static final String VOICECALLVOLUME = "VOICECALLVOLUME";
	static final String VOICECALLVOLUMELOCK = "VOICECALLVOLUMELOCK";
	static final String SYSTEMVOLUME = "SYSTEMVOLUME";
	static final String SYSTEMVOLUMELOCK = "SYSTEMVOLUMELOCK";
	private static final String[] KEYS = {
			PROFILEID,
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
			NOTIFICATIONVOLUME,
			NOTIFICATIONVOLUMELOCK,
			VOICECALLVOLUME,
			VOICECALLVOLUMELOCK,
			SYSTEMVOLUME,
			SYSTEMVOLUMELOCK,
	};

	protected String[] getKeys() {
		return KEYS;
	}
	protected String TAG_START() {
		return TAG_START;
	}

	protected String TAG_END() {
		return TAG_END;
	}

	private UUID mProfileId;
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
	private int mNotificationVolume;
	private boolean mNotificationVolumeLock;
	private int mVoiceCallVolume;
	private boolean mVoiceCallVolumeLock;
	private int mSystemVolume;
	private boolean mSystemVolumeLock;
	
	VolumeProfile() {
		this((UUID)null);
	}

	VolumeProfile(UUID uuid) {
		if (uuid == null) {
			uuid = UUID.randomUUID();
		}
		
		mProfileId = uuid;
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

	protected String getValue(String key) {
		switch (key) {
		case PROFILEID:
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
		case NOTIFICATIONVOLUME:
			return Integer.toString(getNotificationVolume());
		case NOTIFICATIONVOLUMELOCK:
			return Boolean.toString(getNotificationVolumeLock());
		case VOICECALLVOLUME:
			return Integer.toString(getVoiceCallVolume());
		case VOICECALLVOLUMELOCK:
			return Boolean.toString(getVoiceCallVolumeLock());
		case SYSTEMVOLUME:
			return Integer.toString(getSystemVolume());
		case SYSTEMVOLUMELOCK:
			return Boolean.toString(getSystemVolumeLock());
		}
		return null;
	}

	protected void setValue(String key, String value) {
		switch (key) {
		case PROFILEID:
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
		case NOTIFICATIONVOLUME:
			setNotificationVolume(Integer.parseInt(value));
			break;
		case NOTIFICATIONVOLUMELOCK:
			setNotificationVolumeLock(Boolean.parseBoolean(value));
			break;
		case VOICECALLVOLUME:
			setVoiceCallVolume(Integer.parseInt(value));
			break;
		case VOICECALLVOLUMELOCK:
			setVoiceCallVolumeLock(Boolean.parseBoolean(value));
			break;
		case SYSTEMVOLUME:
			setSystemVolume(Integer.parseInt(value));
			break;
		case SYSTEMVOLUMELOCK:
			setSystemVolumeLock(Boolean.parseBoolean(value));
			break;
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
		case NOTIFICATION:
			return getNotificationVolume();
		case VOICE_CALL:
			return getVoiceCallVolume();
		case SYSTEM:
			return getSystemVolume();
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
		case NOTIFICATION:
			setNotificationVolume(volume);
			break;
		case VOICE_CALL:
			setVoiceCallVolume(volume);
			break;
		case SYSTEM:
			setSystemVolume(volume);
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
		case NOTIFICATION:
			return getNotificationVolumeLock();
		case VOICE_CALL:
			return getVoiceCallVolumeLock();
		case SYSTEM:
			return getSystemVolumeLock();
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
		case NOTIFICATION:
			setNotificationVolumeLock(lock);
			break;
		case VOICE_CALL:
			setVoiceCallVolumeLock(lock);
			break;
		case SYSTEM:
			setSystemVolumeLock(lock);
			break;
		}
	}

	public UUID getUuid() {
		return mProfileId;
	}

	public void setUuid(UUID uuid) {
		mProfileId = uuid;
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

	public int getNotificationVolume() {
		return mNotificationVolume;
	}

	public void setNotificationVolume(int volume) {
		mNotificationVolume = volume;
	}

	public boolean getNotificationVolumeLock() {
		return mNotificationVolumeLock;
	}

	public void setNotificationVolumeLock(boolean lock) {
		mNotificationVolumeLock = lock;
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

	public int getSystemVolume() {
		return mSystemVolume;
	}

	public void setSystemVolume(int volume) {
		mSystemVolume = volume;
	}

	public boolean getSystemVolumeLock() {
		return mSystemVolumeLock;
	}

	public void setSystemVolumeLock(boolean lock) {
		mSystemVolumeLock = lock;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public VolumeProfile(Parcel in) {
		super(in);
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
