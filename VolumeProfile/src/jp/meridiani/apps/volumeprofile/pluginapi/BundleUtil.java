package jp.meridiani.apps.volumeprofile.pluginapi;

import android.os.Bundle;

public class BundleUtil {
    public static final String BUNDLE_PROFILEID   = "jp.meridiani.apps.volumeprofile.extra.INTEGER_PROFILEID";
    public static final String BUNDLE_PROFILENAME = "jp.meridiani.apps.volumeprofile.extra.INTEGER_PROFILENAME";

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
		if (!mBundle.containsKey(BUNDLE_PROFILEID)) {
			throw new InvalidBundleException();
		}
		if (!mBundle.containsKey(BUNDLE_PROFILENAME)) {
			throw new InvalidBundleException();
		}
	}

	public void setProfileId (int id) {
		mBundle.putInt(BUNDLE_PROFILEID, id);
	}

	public void setProfileName(String name) {
		mBundle.putString(BUNDLE_PROFILENAME, name);
	}

	public int getProfileId () {
		return mBundle.getInt(BUNDLE_PROFILEID);
	}

	public String getProfileName() {
		return mBundle.getString(BUNDLE_PROFILENAME);
	}

	public void clear() {
		mBundle.clear();
	}

	public Bundle getBundle() {
		return mBundle;
	}
}
