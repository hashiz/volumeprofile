package jp.meridiani.apps.volumeprofile.event;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import java.util.ArrayList;

import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusStateItem;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockState;
import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockItem;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusStateAdapter;
import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusState;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockAdapter;

public class EventEditActivity extends Activity {

	public static final String EXTRA_EVENT = "jp.meridiani.apps.volumeprofile.extra.EVENT";
	private static final String SAVE_EVENT = "SAVE_EVENT";

	// values
	private Event mEvent = new Event();

	// widgets
	// Context
	private CheckBox mBTDeviceCheckBox = null;
	private Spinner mBTDeviceSelectView = null;
	private ArrayAdapter<BluetoothDevice> mBTDeviceListAdapter = null;

	private Spinner mBTProfileSelectView = null;
	private ArrayAdapter<EventContext.BTProfile> mBTProfileListAdapter = null;

	private Spinner mBTStateSelectView = null;
	private ArrayAdapter<EventContext.BTState> mBTStateListAdapter = null;

	// Action
	private CheckBox mChangeProfileCheckBox = null;
	private Spinner mProfileSelectView = null;
	private ArrayAdapter<VolumeProfile> mProfileListAdapter = null;

	private CheckBox mChangeVolumeLockCheckBox = null;
	private Spinner mVolumeLockView = null;
	private VolumeLockAdapter mVolumeLockAdapter = null;

	private CheckBox mChangeClearAudioPlusStateCheckBox = null;
	private Spinner mClearAudioPlusStateView = null;
	private ClearAudioPlusStateAdapter mClearAudioPlusStateAdapter = null;

	private Button mSaveButton = null;
	private Button mCancelButton = null;

	private boolean mCanceled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ProfileStore store = ProfileStore.getInstance(this);

		if (savedInstanceState != null) {
			mEvent = savedInstanceState.getParcelable(SAVE_EVENT);
		}
		else {
			// initial value
			mEvent.Context().setBTDevice(null);
			mEvent.Context().setBTProfile(EventContext.BTProfile.ANY);
			mEvent.Context().setBTState(EventContext.BTState.ANY);
			mEvent.Action().setChangeVolumeProfile(false);
			mEvent.Action().setChangeVolumeLockState(false);
			mEvent.Action().setChangeClearAudioPlusState(false);
			mEvent.Action().setVolumeProfileId(null);
			mEvent.Action().setVolumeLockState(VolumeLockState.LOCK);
			mEvent.Action().setClearAudioPlusState(ClearAudioPlusState.ON);
		}

		mCanceled = false;

		//-----------------------------------------
		// set view
		//-----------------------------------------
		setContentView(R.layout.activity_event_edit);

		//-----------------------------------------
		// Context
		//-----------------------------------------
		// BT Device
		mBTDeviceCheckBox = (CheckBox)findViewById(R.id.event_btdevice_select_checkbox);
		mBTDeviceCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mEvent.Context().setCheckBTDevice(isChecked);
				mBTDeviceSelectView.setEnabled(isChecked);
			}
		});
		mBTDeviceSelectView = (Spinner)findViewById(R.id.event_btdevice_select);
		mBTDeviceListAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_dropdown_item_1line);
		mBTDeviceSelectView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mEvent.Context().setBTDevice((BluetoothDevice)parent.getAdapter().getItem(position));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});
		mBTDeviceSelectView.setEnabled(mBTDeviceCheckBox.isChecked());

		// BT Profile
		mBTProfileSelectView = (Spinner)findViewById(R.id.event_btprofile_select);
		mBTProfileSelectView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mEvent.Context().setBTProfile((EventContext.BTProfile)parent.getAdapter().getItem(position));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {	}
		});

		// BT Connection State
		mBTStateSelectView = (Spinner)findViewById(R.id.event_connectionstate_select);
		mBTStateSelectView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mEvent.Context().setBTState((EventContext.BTState)parent.getAdapter().getItem(position));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});

		//-----------------------------------------
		// Action
		//-----------------------------------------
		// Volume Profile
		mChangeProfileCheckBox = (CheckBox)findViewById(R.id.event_profile_select_checkbox);
		mChangeProfileCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mEvent.Action().setChangeVolumeProfile(isChecked);
				mProfileSelectView.setEnabled(isChecked);
			}
		});
		mChangeProfileCheckBox.setChecked(mEvent.Action().getChangeVolumeProfile());
		mProfileSelectView = (Spinner)findViewById(R.id.event_profile_select);
		mProfileListAdapter = new ArrayAdapter<VolumeProfile>(this, android.R.layout.simple_dropdown_item_1line);
		mProfileSelectView.setAdapter(mProfileListAdapter);
		mProfileSelectView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View item, int pos, long id) {
				mEvent.Action().setVolumeProfileId(((VolumeProfile)parent.getAdapter().getItem(pos)).getUuid());
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {	}
		});
		mProfileSelectView.setEnabled(mChangeProfileCheckBox.isChecked());

		// Volume Lock
		mChangeVolumeLockCheckBox = (CheckBox)findViewById(R.id.event_volumelock_select_checkbox);
		mChangeVolumeLockCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mEvent.Action().setChangeVolumeLockState(isChecked);
				mVolumeLockView.setEnabled(isChecked);
			}
		});
		mChangeVolumeLockCheckBox.setChecked(mEvent.Action().getChangeVolumeLockState());
		mVolumeLockView = (Spinner)findViewById(R.id.event_volumelock_select);
		mVolumeLockAdapter = new VolumeLockAdapter(this, android.R.layout.simple_dropdown_item_1line);
		mVolumeLockAdapter.add(new VolumeLockItem(this, VolumeLockState.LOCK));
		mVolumeLockAdapter.add(new VolumeLockItem(this, VolumeLockState.UNLOCK));
		mVolumeLockAdapter.add(new VolumeLockItem(this, VolumeLockState.TOGGLE));
		mVolumeLockView.setAdapter(mVolumeLockAdapter);
		mVolumeLockView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View item, int pos, long id) {
				mEvent.Action().setVolumeLockState(((VolumeLockItem)parent.getAdapter().getItem(pos)).getValue());
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});
		mVolumeLockView.setEnabled(mChangeVolumeLockCheckBox.isChecked());

		// ClearAudio+ state
		mChangeClearAudioPlusStateCheckBox = (CheckBox)findViewById(R.id.event_clearaudioplus_changestate);
		mChangeClearAudioPlusStateCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mEvent.Action().setChangeClearAudioPlusState(isChecked);
				mClearAudioPlusStateView.setEnabled(isChecked);
			}
		});
		mClearAudioPlusStateView = (Spinner)findViewById(R.id.event_clearaudioplus_state);
		mClearAudioPlusStateAdapter = new ClearAudioPlusStateAdapter(this, android.R.layout.simple_dropdown_item_1line);
		mClearAudioPlusStateAdapter.add(new ClearAudioPlusStateItem(this, ClearAudioPlusState.ON));
		mClearAudioPlusStateAdapter.add(new ClearAudioPlusStateItem(this, ClearAudioPlusState.OFF));
		mClearAudioPlusStateAdapter.add(new ClearAudioPlusStateItem(this, ClearAudioPlusState.TOGGLE));
		mClearAudioPlusStateView.setAdapter(mClearAudioPlusStateAdapter);
		mClearAudioPlusStateView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View item, int pos, long id) {
				mEvent.Action().setClearAudioPlusState(((ClearAudioPlusStateItem)parent.getAdapter().getItem(pos)).getValue());
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {	}
		});
		mClearAudioPlusStateView.setEnabled(mChangeClearAudioPlusStateCheckBox.isChecked());
		if (new AudioUtil(getApplicationContext()).isSupportedClearAudioPlus()) {
			mChangeClearAudioPlusStateCheckBox.setVisibility(View.VISIBLE);
			mClearAudioPlusStateView.setVisibility(View.VISIBLE);
		}
		else {
			mChangeClearAudioPlusStateCheckBox.setVisibility(View.GONE);
			mClearAudioPlusStateView.setVisibility(View.GONE);
		}

		// button
		mSaveButton = (Button)findViewById(R.id.save_button);
		mCancelButton = (Button)findViewById(R.id.cancel_button);

		mSaveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSaveClick((Button)v);
			}
		});
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onCancelClick((Button)v);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// Context
		mBTDeviceCheckBox.setChecked(mEvent.Context().getCheckBTDevice());
		mBTDeviceSelectView.setSelection(mBTDeviceListAdapter.getPosition(mEvent.Context().getBTDevice()));
		updateBTDeviceList();
		mBTDeviceSelectView.setEnabled(mBTDeviceCheckBox.isChecked());
		mBTProfileSelectView.setSelection(mBTProfileListAdapter.getPosition(mEvent.Context().getBTProfile()));
		mBTStateSelectView.setSelection(mBTStateListAdapter.getPosition(mEvent.Context().getBTState()));

		// Action
		mChangeProfileCheckBox.setChecked(mEvent.Action().getChangeVolumeProfile());
		updateProfileList();
		mChangeVolumeLockCheckBox.setChecked(mEvent.Action().getChangeVolumeLockState());
		mVolumeLockView.setSelection(mVolumeLockAdapter.getPosition(mEvent.Action().getVolumeLockState()));
		mVolumeLockView.setEnabled(mChangeVolumeLockCheckBox.isChecked());
		mChangeClearAudioPlusStateCheckBox.setChecked(mEvent.Action().getChangeClearAudioPlusState());
		mClearAudioPlusStateView.setSelection(mClearAudioPlusStateAdapter.getPosition(mEvent.Action().getClearAudioPlusState()));
		mClearAudioPlusStateView.setEnabled(mChangeClearAudioPlusStateCheckBox.isChecked());
	}

	private void updateBTDeviceList() {
		int selPos = -1;
		mBTDeviceListAdapter.clear();

		for ( BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
			mBTDeviceListAdapter.add(device);
			if (mEvent.Context().getBTDevice() != null && mEvent.Context().getBTDevice().getAddress().equals(device.getAddress())) {
				selPos = mBTDeviceListAdapter.getCount() - 1;
			}
		}
		mBTDeviceSelectView.setSelection(selPos);
		mBTDeviceListAdapter.notifyDataSetChanged();
	}

	private void updateProfileList() {
		int selPos = -1;
		mProfileListAdapter.clear();

		ArrayList<VolumeProfile> plist = ProfileStore.getInstance(this).listProfiles();
		for ( VolumeProfile profile : plist) {
			mProfileListAdapter.add(profile);
			if (mEvent.Action().getVolumeProfileId() != null && mEvent.Action().getVolumeProfileId().equals(profile.getUuid())) {
				selPos = mProfileListAdapter.getCount() - 1;
			}
		}
		mProfileSelectView.setSelection(selPos);
		mProfileListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(SAVE_EVENT, mEvent);
	}

	@Override
    public void finish()
    {
        Intent resultIntent = new Intent();

        if (mCanceled) {
            setResult(RESULT_CANCELED, resultIntent);
            super.finish();
            return;
        }

        EventStore store = EventStore.getInstance(this);
		store.storeEvent(mEvent);

        setResult(RESULT_OK, resultIntent);
    	super.finish();
    }

	private void onSaveClick(Button b) {
		mCanceled = false;
		finish();
	}

	private void onCancelClick(Button b) {
		mCanceled = true;
		finish();
	}
}
