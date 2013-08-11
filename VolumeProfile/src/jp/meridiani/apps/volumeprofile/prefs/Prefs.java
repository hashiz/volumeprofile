package jp.meridiani.apps.volumeprofile.prefs;

import jp.meridiani.apps.volumeprofile.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Prefs {
	private static Prefs mInstance = null;
	private SharedPreferences mPrefs;
	private static final String KEY_VIBRATEONPROFILECHANGE      = "vibrate_on_profile_change";
	private static final String KEY_DISPLAYTOASTONPROFILECHANGE = "display_toast_on_profile_change";
	private static final String KEY_PLAYSOUNDONVOLUMECHANGE     = "play_sound_on_volume_change";
	private static final String KEY_SOUNDLEVELALERTHACK         = "sound_level_alert_hack";

	private Prefs(Context context) {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		PreferenceManager.setDefaultValues(context, R.xml.prefs, false);
	}

	public static synchronized Prefs getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new Prefs(context);
		}
		return mInstance;
	}

	private void setValue(String key, boolean value) {
		Editor editor = mPrefs.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public boolean isVibrateOnProfileChange() {
		return mPrefs.getBoolean(KEY_VIBRATEONPROFILECHANGE, true);
	}
	public void setVibrateOnProfileChange(boolean value) {
		setValue(KEY_VIBRATEONPROFILECHANGE, value);
	}

	public boolean isDisplayToastOnProfileChange() {
		return mPrefs.getBoolean(KEY_DISPLAYTOASTONPROFILECHANGE, true);
	}
	public void setDisplayToastOnProfileChange(boolean value) {
		setValue(KEY_DISPLAYTOASTONPROFILECHANGE, value);
	}

	public boolean isPlaySoundOnVolumeChange() {
		return mPrefs.getBoolean(KEY_PLAYSOUNDONVOLUMECHANGE, true);
	}
	public void setPlaySoundOnVolumeChange(boolean value) {
		setValue(KEY_PLAYSOUNDONVOLUMECHANGE, value);
	}

	public boolean isSoundLevelAlertHack() {
		return mPrefs.getBoolean(KEY_SOUNDLEVELALERTHACK, true);
	}
	public void setSoundLevelAlertHack(boolean value) {
		setValue(KEY_SOUNDLEVELALERTHACK, value);
	}
}
