package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import jp.meridiani.apps.volumeprofile.settings.Prefs;

public class VolumeEditFragment extends VolumeEditFragmentBase {

	protected RingerMode getRingerMode() {
		return new AudioUtil(getActivity()).getRingerMode();
	}

	protected void setRingerMode(RingerMode mode) {
		new AudioUtil(getActivity()).setRingerMode(mode);
	}

	protected int getVolume(StreamType type) {
		return new AudioUtil(getActivity()).getVolume(type);
	}

	protected void setVolume(StreamType type, int volume) {
		new AudioUtil(getActivity()).setVolume(type, volume, Prefs.getInstance(getActivity()).isPlaySoundOnVolumeChange());
	}

	protected int getMaxVolume(StreamType type) {
		return new AudioUtil(getActivity()).getVolume(type);
	}
}
