package jp.meridiani.apps.volumeprofile.main;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;

public class VolumeEditFragment extends VolumeEditFragmentBase {
	
	private static final String VOLUME_CHANGED_ACTION     = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE  = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    private static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
//    private static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";

	private AudioUtil mAudio = null;
	private BroadcastReceiver mReceiver = null;
	private IntentFilter      mFilter   = null;

	public static VolumeEditFragment newInstance() {
		return new VolumeEditFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAudio = new AudioUtil(getActivity());
		mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (VOLUME_CHANGED_ACTION.equals(action)) {
				    int type = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
				    if (!AudioUtil.isSupportedType(type)) {
				    	return;
				    }
				    int volume = intent.getIntExtra(EXTRA_VOLUME_STREAM_VALUE, -1);
				    if (volume < 0) {
				    	return;
				    }
				    updateVolume(AudioUtil.getStreamType(type));
				}
				else if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(action)) {
				    int mode = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1);
				    if (!AudioUtil.isSupportedMode(mode)) {
				    	return;
				    }
				    updateRingerMode();
				}
			}
		};
		mFilter = new IntentFilter();
		mFilter.addAction(VOLUME_CHANGED_ACTION);
		mFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);

	}

	@Override
	public void onResume() {
		super.onResume();

		View rootView = getView();
		final SeekBar notificationSeekBar = (SeekBar)rootView.findViewById(R.id.notification_volume_seekBar);
		final SeekBar ringSeekBar = (SeekBar)rootView.findViewById(R.id.ring_volume_seekBar);
		final Prefs prefs = Prefs.getInstance(getActivity());
		if (prefs.isVolumeLinkNotification()) {
			notificationSeekBar.setEnabled(false);
		}
		else {
			notificationSeekBar.setEnabled(true);
		}

		View linkContainer = rootView.findViewById(R.id.link_notification_volume_container);
		if (prefs.hasVolumeLinkNotification()) {
			CheckBox linkCheckbox = (CheckBox)linkContainer.findViewById(R.id.link_notification_volume_checkbox);
			linkCheckbox.setChecked(prefs.isVolumeLinkNotification());
			linkCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
					if (isChecked) {
						notificationSeekBar.setProgress(ringSeekBar.getProgress());
						setVolume(StreamType.NOTIFICATION, getVolume(StreamType.RING));
						notificationSeekBar.setEnabled(false);
						prefs.setVolumeLinkNotification(true);
					}
					else {
						notificationSeekBar.setEnabled(true);
						prefs.setVolumeLinkNotification(false);
					}
				}
			});
		}

		getActivity().registerReceiver(mReceiver, mFilter);
	}

	@Override
	public void onPause(){
		super.onPause();

		getActivity().unregisterReceiver(mReceiver);
	};

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
		Prefs prefs = Prefs.getInstance(getActivity());
		mAudio.setVolume(type, volume, prefs.isPlaySoundOnVolumeChange());
		if (type == StreamType.RING && prefs.isVolumeLinkNotification()) {
			mAudio.setVolume(StreamType.NOTIFICATION, volume, false);
		}
	}

	protected int getMaxVolume(StreamType type) {
		return mAudio.getMaxVolume(type);
	}

}
