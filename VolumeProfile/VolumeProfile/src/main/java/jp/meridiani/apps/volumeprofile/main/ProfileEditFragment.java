package jp.meridiani.apps.volumeprofile.main;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProfileEditFragment extends VolumeEditFragmentBase {

	private static final String BUNDLE_PROFILE = "profile";

	private static final int[] LOCK_IDS = new int [] {
		R.id.ringer_mode_lock,
		R.id.alarm_volume_lock,
		R.id.music_volume_lock,
		R.id.ring_volume_lock,
		R.id.notification_volume_lock,
		R.id.voicecall_volume_lock,
		R.id.system_volume_lock,
	};

	private static final StreamType[] STREAM_TYPES = new StreamType[] {
		StreamType.ALARM,
		StreamType.MUSIC,
		StreamType.RING,
		StreamType.NOTIFICATION,
		StreamType.VOICE_CALL,
		StreamType.SYSTEM,
	};


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
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		View rootView = getView();

		// lock checkbox listener
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
				case R.id.notification_volume_lock_checkbox:
					onVolumeLockChanged(StreamType.NOTIFICATION, checked);
					break;
				case R.id.voicecall_volume_lock_checkbox:
					onVolumeLockChanged(StreamType.VOICE_CALL, checked);
					break;
				case R.id.system_volume_lock_checkbox:
					onVolumeLockChanged(StreamType.SYSTEM, checked);
					break;
				}
			}
		};

		View linkContainer = rootView.findViewById(R.id.link_notification_volume_container);
		linkContainer.setVisibility(View.GONE);

		// Enable Lock View
		for (int id : LOCK_IDS) {
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.profile_edit, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save_profile:
			ProfileStore.getInstance(getActivity()).storeProfile(getVolumeProfile());
		case R.id.action_cancel_edit_profile:
			getActivity().finish();
			return true;
		case R.id.action_lock_all:
			setLockAll(true);
			return true;
		case R.id.action_unlock_all:
			setLockAll(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setLockAll(boolean lock) {
		// check/uncheck Lock View
		for (int id : LOCK_IDS) {
			View lockView = getView().findViewById(id);
			if (lockView != null) {
				// Set Listener for Lock Checkbox
				CheckBox cbox = (CheckBox)lockView.findViewWithTag(getString(R.string.lock_checkbox_tag));
				if (cbox != null) {
					cbox.setChecked(lock);
				}
			}
		}
	}

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
		case NOTIFICATION:
			id = R.id.notification_volume_lock;
			break;
		case VOICE_CALL:
			id = R.id.voicecall_volume_lock;
			break;
		case SYSTEM:
			id = R.id.system_volume_lock;
			break;
		default:
			return null;
		}
		return (CheckBox)rootView.findViewById(id).findViewWithTag(getString(R.string.lock_checkbox_tag));
	}

	private void updateLocks() {
		updateRingerModeLock();

		for (StreamType streamType : STREAM_TYPES) {
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
