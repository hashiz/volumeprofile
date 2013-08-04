package jp.meridiani.apps.volumeprofile.pluginapi;

import java.util.UUID;

import android.os.Bundle;

public class BundleUtil {
    public static final String BUNDLE_PROFILEID   = "jp.meridiani.apps.volumeprofile.extra.STRING_PROFILEID";
    public static final String BUNDLE_PROFILENAME = "jp.meridiani.apps.volumeprofile.extra.STRING_PROFILENAME";

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

	public void setProfileId (UUID uuid) {
		mBundle.putString(BUNDLE_PROFILEID, uuid.toString());
	}

	public void setProfileName(String name) {
		mBundle.putString(BUNDLE_PROFILENAME, name);
	}

	public UUID getProfileId () {
		return UUID.fromString(mBundle.getString(BUNDLE_PROFILEID));
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
