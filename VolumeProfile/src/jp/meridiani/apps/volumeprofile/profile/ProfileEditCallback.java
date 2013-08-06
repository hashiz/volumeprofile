package jp.meridiani.apps.volumeprofile.profile;

public interface ProfileEditCallback {
	public void onProfileEditPositive(VolumeProfile newProfile);
	public void onProfileEditNegative();
}
