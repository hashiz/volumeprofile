package jp.meridiani.apps.volumeprofile.main;

import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;

public interface VolumeUpdateNotify {
	public void updateAll();
	public void updateVolume(StreamType streamType);
	public void updateRingerMode();
}
