package jp.meridiani.apps.volumeprofile.main;

import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;

public interface ProfileEditCallback {
	public void onProfileEditPositive(VolumeProfile newProfile);
	public void onProfileEditNegative();
}
