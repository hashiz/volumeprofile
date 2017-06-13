package jp.meridiani.apps.volumeprofile.pluginapi;

import java.util.UUID;

import android.os.Bundle;

public class BundleUtil {
    public static final String BUNDLE_PROFILEID   = "jp.meridiani.apps.volumeprofile.extra.STRING_PROFILEID";
    public static final String BUNDLE_PROFILENAME = "jp.meridiani.apps.volumeprofile.extra.STRING_PROFILENAME";
    public static final String BUNDLE_VOLUMELOCK  = "jp.meridiani.apps.volumeprofile.extra.STRING_VOLUMELOCK";
    public static final String BUNDLE_CLEARAUDIOPLUSSTATE = "jp.meridiani.apps.volumeprofile.extra.STRING_CLEARAUDIOPLUSSTATE";

	private Bundle mBundle;

	public BundleUtil() {
		mBundle = new Bundle();
	}

	public BundleUtil(Bundle bundle) throws InvalidBundleException {
		mBundle = bundle;
		if (mBundle == null) {
			throw new InvalidBundleException();
		}
		if (mBundle.containsKey(null)) {
			throw new InvalidBundleException();
		}
	}

	public void setProfileId(UUID uuid) {
		mBundle.putString(BUNDLE_PROFILEID, uuid.toString());
	}

	public void setProfileName(String name) {
		mBundle.putString(BUNDLE_PROFILENAME, name);
	}

	public void setVolumeLock(PluginEditActivity.VolumeLockValue lock) {
		mBundle.putString(BUNDLE_VOLUMELOCK,lock.name());
	}

	public void setClearAudioPlusState(PluginEditActivity.ClearAudioPlusStateValue state) {
		mBundle.putString(BUNDLE_CLEARAUDIOPLUSSTATE,state.name());
	}

	public UUID getProfileId () {
		String uuid = mBundle.getString(BUNDLE_PROFILEID);
		if (uuid == null) {
			return null;
		}
		return UUID.fromString(mBundle.getString(BUNDLE_PROFILEID));
	}

	public String getProfileName () {
		return mBundle.getString(BUNDLE_PROFILENAME);
	}

	public PluginEditActivity.VolumeLockValue getVolumeLock() {
		String name = mBundle.getString(BUNDLE_VOLUMELOCK);
		if (name == null) {
			return null;
		}
		return PluginEditActivity.VolumeLockValue.valueOf(name);
	}

	public PluginEditActivity.ClearAudioPlusStateValue getClearAudioPlusState() {
		String name = mBundle.getString(BUNDLE_CLEARAUDIOPLUSSTATE);
		if (name == null) {
			return null;
		}
		return PluginEditActivity.ClearAudioPlusStateValue.valueOf(name);
	}

	public void clear() {
		mBundle.clear();
	}

	public Bundle getBundle() {
		return mBundle;
	}
}
