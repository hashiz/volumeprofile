package jp.meridiani.apps.volumeprofile.main;

public interface ProfileEditCallback {
	public void onProfileEditPositive(VolumeProfile newProfile);
	public void onProfileEditNegative();
}
