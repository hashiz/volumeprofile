package jp.meridiani.apps.volumeprofile.prefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings.System;

public class Prefs implements OnSharedPreferenceChangeListener {
	private static Prefs mInstance = null;
	static final String KEY_DISPLAYTOASTONPROFILECHANGE = "display_toast_on_profile_change";
	static final String KEY_VIBRATEONPROFILECHANGE      = "vibrate_on_profile_change";
	static final String KEY_PLAYSOUNDONVOLUMECHANGE     = "play_sound_on_volume_change";
	static final String KEY_SOUNDLEVELALERTHACK         = "sound_level_alert_hack";
	static final String KEY_DISPLAYTOASTONVOLUMELOCK    = "display_toast_on_volume_lock";

	static final String KEY_DETECT_DEVICE_FUNCTION      = "detect_device_function";
	static final String KEY_VOLUME_LINK_NOTIFICATION    = "volume_link_notification";
	static final String KEY_VOLUME_LINK_SYSTEM          = "volume_link_system";
	static final String KEY_VOLUME_LINK_RINGERMODE      = "volume_link_ringermode";

	// backup restore
	private static final String PREFS_START = "<preferences>";
	private static final String PREFS_END   = "</preferences>";

	// for cyanogenmod
	private static final String VOLUME_LINK_NOTIFICATION = "VOLUME_LINK_NOTIFICATION";
	private String  mKeyVOLUME_LINK_NOTIFICATION = null;
	private boolean mHasVOLUME_LINK_NOTIFICATION = false;

	private Context mContext;
	private SharedPreferences mSharedPrefs;

	private Prefs(Context context) {
		mContext = context;
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		PreferenceManager.setDefaultValues(context, R.xml.prefs, false);
		mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
		Class<System> c = System.class;
		try {
			Field f = c.getField(VOLUME_LINK_NOTIFICATION);
			mKeyVOLUME_LINK_NOTIFICATION = (String)f.get(null);
			mHasVOLUME_LINK_NOTIFICATION = true;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		// detect device function
		detectDeviceFunction(false);
	}

	void detectDeviceFunction(boolean force) {
		AudioUtil audio = new AudioUtil(mContext);
		Editor editor = mSharedPrefs.edit();
		if (!mHasVOLUME_LINK_NOTIFICATION) {
			if (!mSharedPrefs.contains(KEY_VOLUME_LINK_NOTIFICATION) || force) {
				editor.putBoolean(KEY_VOLUME_LINK_NOTIFICATION, audio.detectVolumeLinkNotification());
			}
		}
		if (!mSharedPrefs.contains(KEY_VOLUME_LINK_SYSTEM) || force) {
			editor.putBoolean(KEY_VOLUME_LINK_SYSTEM, audio.detectVolumeLinkSystem());
		}
		if (!mSharedPrefs.contains(KEY_VOLUME_LINK_RINGERMODE) || force) {
			if (Build.DEVICE.equals("SO-05D") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				editor.putBoolean(KEY_VOLUME_LINK_RINGERMODE, true);
			}
			else {
				editor.putBoolean(KEY_VOLUME_LINK_RINGERMODE, false);
			}
		}
		editor.apply();
	}

	public static synchronized Prefs getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new Prefs(context);
		}
		return mInstance;
	}

	public void finalize() throws Throwable {
		try {
			if (mSharedPrefs != null) {
				mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
			}
		}
		finally {
			super.finalize();
		}
	}

	private void setValue(String key, String value) {
		setBooleanValue(key, Boolean.parseBoolean(value));
	}

	private void setBooleanValue(String key, boolean value) {
		Editor editor = mSharedPrefs.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public boolean isDisplayToastOnProfileChange() {
		return mSharedPrefs.getBoolean(KEY_DISPLAYTOASTONPROFILECHANGE, true);
	}
	public void setDisplayToastOnProfileChange(boolean value) {
		setBooleanValue(KEY_DISPLAYTOASTONPROFILECHANGE, value);
	}

	public boolean isVibrateOnProfileChange() {
		return mSharedPrefs.getBoolean(KEY_VIBRATEONPROFILECHANGE, true);
	}
	public void setVibrateOnProfileChange(boolean value) {
		setBooleanValue(KEY_VIBRATEONPROFILECHANGE, value);
	}

	public boolean isPlaySoundOnVolumeChange() {
		return mSharedPrefs.getBoolean(KEY_PLAYSOUNDONVOLUMECHANGE, true);
	}
	public void setPlaySoundOnVolumeChange(boolean value) {
		setBooleanValue(KEY_PLAYSOUNDONVOLUMECHANGE, value);
	}

	public boolean isSoundLevelAlertHack() {
		return mSharedPrefs.getBoolean(KEY_SOUNDLEVELALERTHACK, true);
	}
	public void setSoundLevelAlertHack(boolean value) {
		setBooleanValue(KEY_SOUNDLEVELALERTHACK, value);
	}

	public boolean isDisplayToastOnVolumeLock() {
		return mSharedPrefs.getBoolean(KEY_DISPLAYTOASTONVOLUMELOCK, false);
	}
	public void setDisplayToastOnVolumeLock(boolean value) {
		setBooleanValue(KEY_DISPLAYTOASTONVOLUMELOCK, value);
	}

	public void writeToText(BufferedWriter wtr) throws IOException {
		Map <String, ?> map = mSharedPrefs.getAll();
		wtr.write(PREFS_START); wtr.newLine();
		for (String key : map.keySet()) {
			if (key.equals(KEY_VOLUME_LINK_NOTIFICATION)) {
				// do not save this
				continue;
			}
			wtr.write(key + "=" + map.get(key)); wtr.newLine();
		}
		wtr.write(PREFS_END); wtr.newLine();
	}

	public boolean hasVolumeLinkNotification() {
		return mHasVOLUME_LINK_NOTIFICATION;
	}

	public void setVolumeLinkNotification(boolean link) {
		if (! hasVolumeLinkNotification()) {
			return;
		}
		int value = 0;
		if (link) {
			value = 1;
		}
		android.provider.Settings.System.putInt(mContext.getContentResolver(), mKeyVOLUME_LINK_NOTIFICATION, value);
	}

	public boolean isVolumeLinkNotification() {
		if (hasVolumeLinkNotification()) {
			int linkNotification = 	android.provider.Settings.System.getInt(mContext.getContentResolver(), mKeyVOLUME_LINK_NOTIFICATION, 1);
			return linkNotification == 1;
		}
		else {
			return mSharedPrefs.getBoolean(KEY_VOLUME_LINK_NOTIFICATION, true);
		}
	}

	public boolean isVolumeLinkSystem() {
		return mSharedPrefs.getBoolean(KEY_VOLUME_LINK_SYSTEM, true);
	}

	public boolean isVolumeLinkRingermode() {
		return mSharedPrefs.getBoolean(KEY_VOLUME_LINK_RINGERMODE, true);
	}

	public void setFromText(BufferedReader rdr) throws IOException {
    	String line;
    	boolean started = false;
		while ((line = rdr.readLine()) != null) {
			if (started) {
				if (PREFS_END.equals(line)) {
					break;
				}
				String[] tmp = line.split("=", 2);
				if (tmp.length < 2) {
					continue;
				}
				setValue(tmp[0], tmp[1]);
			}
			else {
				if (PREFS_START.equals(line)) {
					started = true;
				}
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// request backup
		BackupManager.dataChanged(mContext.getPackageName());
	}
}
