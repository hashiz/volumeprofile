package jp.meridiani.apps.volumeprofile.main;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class VolumeEditFragmentBase extends Fragment {

	private class RingerModeItem {
		private RingerMode mRingerMode = null;
		private String     mString = null;

		public RingerModeItem(Context context, RingerMode mode) {
			mRingerMode = mode;
			switch (mRingerMode) {
			case NORMAL:
				mString = context.getString(R.string.ringer_mode_normal);
				break;
			case VIBRATE:
				mString = context.getString(R.string.ringer_mode_vibrate);
				break;
			case SILENT:
				mString = context.getString(R.string.ringer_mode_sirent);
				break;
			}
		}

		@Override
		public String toString() {
			return mString;
		}

		public RingerMode getValue() {
			return mRingerMode;
		}
	}

	private class RingerModeAdapter extends ArrayAdapter<RingerModeItem> {

		public RingerModeAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		public int getPosition(RingerMode mode) {
			for (int i = 0; i < this.getCount(); i++ ) {
				if (this.getItem(i).getValue() == mode) {
					return i;
				}
			}
			return -1;
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_volumeedit,
				container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		View rootView = getView();
		Activity activity = getActivity();

		// Ringer Mode Item
		Spinner ringerModeView = (Spinner)rootView.findViewById(R.id.ringer_mode_value);

		RingerModeAdapter adapter = new RingerModeAdapter(activity,
				android.R.layout.simple_spinner_item);
		adapter.add(new RingerModeItem(activity, RingerMode.NORMAL));
		adapter.add(new RingerModeItem(activity, RingerMode.VIBRATE));
		adapter.add(new RingerModeItem(activity, RingerMode.SILENT));
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		ringerModeView.setAdapter(adapter);

	};

	@Override
	public void onResume() {
		super.onResume();

		// update values

		// ringer mode
		OnItemSelectedListener ringerModeListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				RingerModeItem item =(RingerModeItem)parent.getAdapter().getItem(pos);
				if (getRingerMode() != item.getValue()) {
					setRingerMode(item.getValue());
				}
				switch (item.getValue()) {
				case VIBRATE:
				case SILENT:
					try {
						findSeekBar(StreamType.RING).setProgress(getVolume(StreamType.RING));
					} catch (ViewNotAvailableException e) {
						return;
					}
					break;
				case NORMAL:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// nop
			}
		};
		updateRingerMode(ringerModeListener);

		// Volumes
		// set listener
		OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
 				StreamType type = findStreamType(seekBar.getId());
				if (type == null) {
					return;
				}
				TextView textView;
				try {
					textView = findValueView(type);
				} catch (ViewNotAvailableException e) {
					return;
				}
				textView.setText(String.format("%2d/%2d", progress, seekBar.getMax()));
				if (fromUser) {
					setVolume(type, progress);
					int volume = getVolume(type);
					if (volume != progress) {
						seekBar.setProgress(volume);
						textView.setText(String.format("%2d/%2d", volume, seekBar.getMax()));
					}
					if (type == StreamType.RING && progress == 0) {
						updateRingerMode(null);
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		};
		updateVolumes(seekBarListener);
	}

	protected View getRootView() throws ViewNotAvailableException {
		View rootView = getView();
		if (rootView == null) {
			throw new ViewNotAvailableException();
		}
		return rootView;
	};

	private StreamType findStreamType(int resid) {
		switch (resid) {
		case R.id.aloarm_volume_value:
		case R.id.alarm_volume_seekBar:
			return StreamType.ALARM;
		case R.id.music_volume_value:
		case R.id.music_volume_seekBar:
			return StreamType.MUSIC;
		case R.id.ring_volume_value:
		case R.id.ring_volume_seekBar:
			return StreamType.RING;
		case R.id.notification_volume_value:
		case R.id.notification_volume_seekBar:
			return StreamType.NOTIFICATION;
		case R.id.voicecall_volume_value:
		case R.id.voicecall_volume_seekBar:
			return StreamType.VOICE_CALL;
		default:
			return null;
		}
	}

	private TextView findValueView(StreamType type) throws ViewNotAvailableException {
		View rootView = getRootView();
		int id = -1;
		switch (type) {
		case ALARM:
			id = R.id.aloarm_volume_value;
			break;
		case MUSIC:
			id = R.id.music_volume_value;
			break;
		case RING:
			id = R.id.ring_volume_value;
			break;
		case NOTIFICATION:
			id = R.id.notification_volume_value;
			break;
		case VOICE_CALL:
			id = R.id.voicecall_volume_value;
			break;
		}
		return (TextView)rootView.findViewById(id);
	}

	private SeekBar findSeekBar(StreamType type) throws ViewNotAvailableException {
		View rootView = getRootView();
		if (rootView == null) {
			return null;
		}
		int id = -1;
		switch (type) {
		case ALARM:
			id = R.id.alarm_volume_seekBar;
			break;
		case MUSIC:
			id = R.id.music_volume_seekBar;
			break;
		case RING:
			id = R.id.ring_volume_seekBar;
			break;
		case NOTIFICATION:
			id = R.id.notification_volume_seekBar;
			break;
		case VOICE_CALL:
			id = R.id.voicecall_volume_seekBar;
			break;
		}
		return (SeekBar)rootView.findViewById(id);
	}

	private void updateRingerMode(OnItemSelectedListener newListener) {
		try {
			View rootView;
			rootView = getRootView();
			Spinner ringerModeView = (Spinner)rootView.findViewById(R.id.ringer_mode_value);
	
			// init value
			RingerModeAdapter adapter = (RingerModeAdapter)ringerModeView.getAdapter();
	
			if (((RingerModeItem)ringerModeView.getSelectedItem()).getValue() != getRingerMode()) {
				ringerModeView.setSelection(adapter.getPosition(getRingerMode()));
			}
	
			// set listener
			if (newListener != null) {
				ringerModeView.setOnItemSelectedListener(newListener);
			}
		} catch (ViewNotAvailableException e) {
			return;
		}
	}

	public void updateRingerMode() {
		updateRingerMode(null);
	}

	private void updateVolumes(OnSeekBarChangeListener newListener) {

		for (StreamType streamType : new StreamType[] {
				StreamType.ALARM,
				StreamType.MUSIC,
				StreamType.RING,
				StreamType.NOTIFICATION,
				StreamType.VOICE_CALL}) {

			updateVolume(streamType, newListener);
		}
	}

	public void updateVolumes() {
		updateVolumes(null);
	}

	public void updateVolumeEdit() {
		updateRingerMode();
		updateVolumes();
	}

	private void updateVolume(StreamType streamType, OnSeekBarChangeListener newListener) {
		try {
			SeekBar volumeBar;
			volumeBar = findSeekBar(streamType);

			// init value
			int volume = getVolume(streamType);
			int maxVolume = getMaxVolume(streamType);
	
			volumeBar.setMax(maxVolume);
			if (volumeBar.getProgress() != volume) {
				volumeBar.setProgress(volume);
			}
			TextView textView = findValueView(streamType);
			textView.setText(String.format("%2d/%2d", volume, maxVolume));
	
			if (newListener != null) {
				volumeBar.setOnSeekBarChangeListener(newListener);
			}
		} catch (ViewNotAvailableException e) {
			return;
		}
	}

	public void updateVolume(StreamType streamType) {
		updateVolume(streamType, null);
		if (Prefs.getInstance(getActivity()).isVolumeLinkNotification() && streamType == StreamType.RING) {
			updateVolume(StreamType.NOTIFICATION, null);
		}
	}

	abstract protected RingerMode getRingerMode();

	abstract protected void setRingerMode(RingerMode mode);

	protected boolean getRingerModeLock() {
		return false;
	}

	protected void setRingerModeLock(boolean lock) {
	}

	abstract protected int getVolume(StreamType type);

	abstract protected void setVolume(StreamType type, int volume);

	protected boolean getVolumeLock(StreamType type) {
		return false;
	}

	protected void setVolumeLock(StreamType type, boolean lock) {
	}

	abstract protected int getMaxVolume(StreamType type);
}
