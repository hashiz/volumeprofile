package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import jp.meridiani.apps.volumeprofile.settings.Prefs;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public abstract class VolumeEditFragmentBase extends Fragment {

	private class RingerModeItem {
		private RingerMode mRingerMode = null;
		private String     mString = null;

		public RingerModeItem(Context context, RingerMode mode) {
			mRingerMode = mode;
			switch (mRingerMode) {
			case NORMAL:
				mString = context.getString(R.string.ringer_mode_normal);
			case VIBRATE:
				mString = context.getString(R.string.ringer_mode_vibrate);
			case SILENT:
				mString = context.getString(R.string.ringer_mode_sirent);
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Activity activity = getActivity();

		// Ringer Mode
		Spinner ringerModeView = (Spinner)activity.findViewById(R.id.ringer_mode_value);

		RingerModeAdapter adapter = new RingerModeAdapter(activity,
				android.R.layout.simple_list_item_single_choice);
		RingerModeItem[] itemList = new RingerModeItem[] {
				new RingerModeItem(activity, RingerMode.NORMAL),
				new RingerModeItem(activity, RingerMode.VIBRATE),
				new RingerModeItem(activity, RingerMode.SILENT),
		};

		for (RingerModeItem item : itemList ) {
			adapter.add(item);
		}
		ringerModeView.setAdapter(adapter);
		ringerModeView.setSelection(adapter.getPosition(getRingerMode()));
		ringerModeView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				RingerModeItem item =(RingerModeItem)parent.getAdapter().getItem(pos);
				setRingerMode(item.getValue());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// nop
			}
		});

		// Volumes
		OnSeekBarChangeListener listner = new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				StreamType type = findStreamType(seekBar.getId());
				if (type == null) {
					return;
				}
				TextView textView = findValueView(type);
				setVolume(type, progress);
				if (!fromUser) {
					return;
				}
				int volume = getVolume(type);
				seekBar.setProgress(volume);
				textView.setText(String.format("%2d/%2d", volume, seekBar.getMax()));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		};

		for (StreamType streamType : new StreamType[] {
				StreamType.ALARM,
				StreamType.MUSIC,
				StreamType.RING,
				StreamType.SYSTEM,
				StreamType.VOICE_CALL}) {

			findSeekBar(streamType).setOnSeekBarChangeListener(listner); 
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_volumeedit,
				container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		// update values

		//  ringer mode
		Spinner ringerModeView = (Spinner)getView().findViewById(R.id.ringer_mode_value);
		RingerModeAdapter adapter = (RingerModeAdapter)ringerModeView.getAdapter();
		ringerModeView.setSelection(adapter.getPosition(getRingerMode()));

		// Volumes
		for (StreamType streamType : new StreamType[] {
				StreamType.ALARM,
				StreamType.MUSIC,
				StreamType.RING,
				StreamType.SYSTEM,
				StreamType.VOICE_CALL}) {
			int volume = getVolume(streamType);
			int maxVolume = getMaxVolume(streamType);
			updateVolumeView(streamType, volume, maxVolume);
		}
	}

	private void updateVolumeView(StreamType type, int volume, int max) {
		SeekBar seekBar = findSeekBar(type);
		TextView textView = findValueView(type);

		seekBar.setProgress(volume);
		seekBar.setMax(max);

		textView.setText(String.format("%2d/%2d", volume, max));
	}

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
		case R.id.system_volume_value:
		case R.id.system_volume_seekBar:
			return StreamType.SYSTEM;
		case R.id.voicecall_volume_value:
		case R.id.voicecall_volume_seekBar:
			return StreamType.VOICE_CALL;
		default:
			return null;
		}
	}

	private TextView findValueView(StreamType type) {
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
		case SYSTEM:
			id = R.id.system_volume_value;
			break;
		case VOICE_CALL:
			id = R.id.voicecall_volume_value;
			break;
		default:
			return null;
		}
		return (TextView)getActivity().findViewById(id);
	}

	private SeekBar findSeekBar(StreamType type) {
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
		case SYSTEM:
			id = R.id.system_volume_seekBar;
			break;
		case VOICE_CALL:
			id = R.id.voicecall_volume_seekBar;
			break;
		default:
			return null;
		}
		return (SeekBar)getActivity().findViewById(id);
	}

	abstract protected RingerMode getRingerMode();

	abstract protected void setRingerMode(RingerMode mode);

	abstract protected int getVolume(StreamType type);

	abstract protected void setVolume(StreamType type, int volume);

	abstract protected int getMaxVolume(StreamType type);
}
