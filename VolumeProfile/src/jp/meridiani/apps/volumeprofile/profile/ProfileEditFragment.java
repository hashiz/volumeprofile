package jp.meridiani.apps.volumeprofile.profile;

import android.os.Bundle;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;

public class ProfileEditFragment extends VolumeEditFragmentBase {

	private static final String BUNDLE_PROFILE = "profile";
	private VolumeProfile mProfile = null;
	private AudioUtil mAudio = null;

	public static ProfileEditFragment newInstance(VolumeProfile profile) {
		ProfileEditFragment instance = new ProfileEditFragment();
		Bundle args = new Bundle();
		args.putParcelable(BUNDLE_PROFILE, profile);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mProfile = savedInstanceState.getParcelable(BUNDLE_PROFILE);
		}
		else {
			mProfile = getArguments().getParcelable(BUNDLE_PROFILE);
		}
		mAudio = new AudioUtil(getActivity());
	}

	public VolumeProfile getVolumeProfile() {
		return mProfile;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(BUNDLE_PROFILE, mProfile);
	}

	@Override
	protected RingerMode getRingerMode() {
		return mProfile.getRingerMode();
	}

	@Override
	protected void setRingerMode(RingerMode mode) {
		mProfile.setRingerMode(mode);
	}

	@Override
	protected int getVolume(StreamType type) {
		return mProfile.getVolume(type);
	}

	@Override
	protected void setVolume(StreamType type, int volume) {
		mProfile.setVolume(type, volume);
	}

	@Override
	protected int getMaxVolume(StreamType type) {
		return mAudio.getMaxVolume(type);
	}
}
