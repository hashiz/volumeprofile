package jp.meridiani.apps.volumeprofile.profile;

import android.os.Bundle;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import jp.meridiani.apps.volumeprofile.settings.Prefs;

public class VolumeEditFragment extends VolumeEditFragmentBase {
	
	private AudioUtil mAudio = null;

	public static VolumeEditFragment newInstance() {
		return new VolumeEditFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAudio = new AudioUtil(getActivity());
	}

	protected RingerMode getRingerMode() {
		return mAudio.getRingerMode();
	}

	protected void setRingerMode(RingerMode mode) {
		mAudio.setRingerMode(mode);
	}

	protected int getVolume(StreamType type) {
		return mAudio.getVolume(type);
	}

	protected void setVolume(StreamType type, int volume) {
		mAudio.setVolume(type, volume, Prefs.getInstance(getActivity()).isPlaySoundOnVolumeChange());
	}

	protected int getMaxVolume(StreamType type) {
		return mAudio.getMaxVolume(type);
	}

}
