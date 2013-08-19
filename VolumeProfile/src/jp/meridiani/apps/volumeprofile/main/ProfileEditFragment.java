package jp.meridiani.apps.volumeprofile.main;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import jp.meridiani.apps.volumeprofile.R;
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		View rootView = getView();

		// lock checkbox listner
		OnCheckedChangeListener listner = new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				switch (view.getId()) {
				case R.id.ringer_mode_lock_checkbox:
					onRingerModeLockChanged(checked);
					break;
				case R.id.alarm_volume_lock_checkbox:
					onVolumeLockChanged(StreamType.ALARM, checked);
					break;
				case R.id.music_volume_lock_checkbox:
					onVolumeLockChanged(StreamType.MUSIC, checked);
					break;
				case R.id.ring_volume_lock_checkbox:
					onVolumeLockChanged(StreamType.RING, checked);
					break;
				case R.id.voicecall_volume_lock_checkbox:
					onVolumeLockChanged(StreamType.VOICE_CALL, checked);
					break;
				}
			}
		};

		// Enable Lock View
		for (int id : new int[] {
				R.id.ringer_mode_lock,
				R.id.alarm_volume_lock,
				R.id.music_volume_lock,
				R.id.ring_volume_lock,
				R.id.voicecall_volume_lock,
		}) {
			View lockView = rootView.findViewById(id);
			if (lockView != null) {
				lockView.setVisibility(View.VISIBLE);
				lockView.setEnabled(true);
				// Set Listener for Lock Checkbox
				CheckBox cbox = (CheckBox)lockView.findViewWithTag(getString(R.string.lock_checkbox_tag));
				if (cbox != null) {
					cbox.setOnCheckedChangeListener(listner);
				}
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();

		updateLocks();
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
		switch (mode) {
		case SILENT:
		case VIBRATE:
			mProfile.setVolume(StreamType.RING, 0);
			break;
		case NORMAL:
			break;
		}
	}

	@Override
	protected boolean getRingerModeLock() {
		return mProfile.getRingerModeLock();
	}

	@Override
	protected void setRingerModeLock(boolean lock) {
		mProfile.setRingerModeLock(lock);
	}

	@Override
	protected int getVolume(StreamType type) {
		return mProfile.getVolume(type);
	}

	@Override
	protected void setVolume(StreamType type, int volume) {
		mProfile.setVolume(type, volume);
		if (type == StreamType.RING && volume == 0) {
			mProfile.setRingerMode(RingerMode.VIBRATE);
		}
	}

	@Override
	protected boolean getVolumeLock(StreamType type) {
		return mProfile.getVolumeLock(type);
	}

	@Override
	protected void setVolumeLock(StreamType type, boolean lock) {
		mProfile.setVolumeLock(type, lock);
	}

	@Override
	protected int getMaxVolume(StreamType type) {
		return mAudio.getMaxVolume(type);
	}

	private void onRingerModeLockChanged(boolean checked) {
		setRingerModeLock(checked);
	}

	private void onVolumeLockChanged(StreamType type, boolean checked) {
		setVolumeLock(type, checked);
	}

	private CheckBox findLock(StreamType type) {
		View rootView = getView();
		if (rootView == null) {
			return null;
		}
		int id;
		switch (type) {
		case ALARM:
			id = R.id.alarm_volume_lock;
			break;
		case MUSIC:
			id = R.id.music_volume_lock;
			break;
		case RING:
			id = R.id.ring_volume_lock;
			break;
		case VOICE_CALL:
			id = R.id.voicecall_volume_lock;
			break;
		default:
			return null;
		}
		return (CheckBox)rootView.findViewById(id).findViewWithTag(getString(R.string.lock_checkbox_tag));
	}

	private void updateLocks() {
		updateRingerModeLock();

		for (StreamType streamType : new StreamType[] {
				StreamType.ALARM,
				StreamType.MUSIC,
				StreamType.RING,
				StreamType.VOICE_CALL}) {

			updateVolumeLock(streamType);
		}
	}

	private void updateRingerModeLock() {
		View rootView = getView();
		if (rootView == null) {
			return;
		}
		int id = R.id.ringer_mode_lock;
		CheckBox cbox = (CheckBox)getView().findViewById(id).findViewWithTag(getString(R.string.lock_checkbox_tag));
		cbox.setChecked(mProfile.getRingerModeLock());
	}

	private void updateVolumeLock(StreamType type) {
		CheckBox cbox = findLock(type);
		cbox.setChecked(mProfile.getVolumeLock(type));
	}

}
