package jp.meridiani.apps.volumeprofile.event;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusStateItem;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockValue;
import jp.meridiani.apps.volumeprofile.MessageText;
import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.pluginapi.BundleUtil;
import jp.meridiani.apps.volumeprofile.pluginapi.InvalidBundleException;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockItem;
import jp.meridiani.apps.volumeprofile.profile.ProfileNotFoundException;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusStateAdapter;
import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusStateValue;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockAdapter;

public class EventEditActivity extends Activity {

	private static final String SAVE_BTDEVICE = "SAVE_BTDEVICE";
	private static final String SAVE_BTPROFILE = "SAVE_BTPROFILE";
	private static final String SAVE_BTSTATE = "SAVE_BTSTATE";
	private static final String SAVE_CHANGEPROFILE = "SAVE_CHANGEPROFILE";
	private static final String SAVE_CHANGEVOLUMELOCK = "SAVE_CHANGEVOLUMELOCK";
	private static final String SAVE_CHANGECLEARAUDIOPLUS = "SAVE_CHANGECLEARAUDIOPLUS";
	private static final String SAVE_VOLUMEPROFILE = "SAVE_VOLUMEPROFILE";
	private static final String SAVE_VOLUMELOCKSTATE = "SAVE_VOLUMELOCKSTATE";
	private static final String SAVE_CLEARAUDIOPLUSSTATE = "SAVE_CLEARAUDIOPLUSSTATE";

	// values
	private BluetoothDevice mBTDevice = null;
	private Event.BTProfile mBTProfile = Event.BTProfile.ANY;
	private Event.BTState mBTState = Event.BTState.CONNECTED;
	private boolean mChangeProfile = false;
	private boolean mChangeVolumeLock  = false;
	private boolean mChangeClearAudioPlus = false;
	private VolumeProfile mSelectedProfile = null;
	private VolumeLockValue mVolumeLockState = VolumeLockValue.LOCK;
	private ClearAudioPlusStateValue mClearAudioPlusState = ClearAudioPlusStateValue.ON;

	// widgets
	private Te
	private CheckBox mChangeProfileCheckBox = null;
	private CheckBox mChangeVolumeLockCheckBox = null;
	private CheckBox mChangeClearAudioPlusStateCheckBox = null;
	private Spinner mProfileSelectView = null;
	private Spinner mVolumeLockView = null;
	private Spinner mClearAudioPlusStateView = null;
	private ArrayAdapter<VolumeProfile> mProfileListAdapter = null;
	private VolumeLockAdapter mVolumeLockAdapter = null;
	private ClearAudioPlusStateAdapter mClearAudioPlusStateAdapter = null;
	private Button mSaveButton = null;
	private Button mCancelButton = null;
	private boolean mCanceled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ProfileStore store = ProfileStore.getInstance(this);

		if (savedInstanceState != null) {
			mBTDevice = savedInstanceState.getParcelable(SAVE_BTDEVICE);
			mBTProfile = Event.BTProfile.valueOf(savedInstanceState.getString(SAVE_BTPROFILE));
			mBTState = Event.BTState.valueOf(savedInstanceState.getString(SAVE_BTSTATE));
			mChangeProfile = savedInstanceState.getBoolean(SAVE_CHANGEPROFILE);
			mChangeVolumeLock  = savedInstanceState.getBoolean(SAVE_CHANGEVOLUMELOCK);
			mChangeClearAudioPlus = savedInstanceState.getBoolean(SAVE_CHANGECLEARAUDIOPLUS);
			mSelectedProfile = savedInstanceState.getParcelable(SAVE_VOLUMEPROFILE);
			mVolumeLockState = VolumeLockValue.valueOf(savedInstanceState.getString(SAVE_VOLUMELOCKSTATE));
			mClearAudioPlusState = ClearAudioPlusStateValue.valueOf(savedInstanceState.getString(SAVE_CLEARAUDIOPLUSSTATE));
		}
		else {
			// initial value
			mBTDevice = null;
			mBTProfile = Event.BTProfile.ANY;
			mBTState = Event.BTState.ANY;
			mChangeProfile = false;
			mChangeVolumeLock = false;
			mChangeClearAudioPlus = false;
			mSelectedProfile = null;
			mVolumeLockState = VolumeLockValue.LOCK;
			mClearAudioPlusState = ClearAudioPlusStateValue.ON;
		}

		mCanceled = false;

		// set view
		setContentView(R.layout.activity_event_edit);

		// profile list
		mChangeProfileCheckBox = (CheckBox)findViewById(R.id.plugin_profile_select_checkbox);
		mChangeProfileCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mChangeProfile = isChecked;
				mProfileSelectView.setEnabled(isChecked);
			}
		});
		mProfileSelectView = (Spinner)findViewById(R.id.plugin_profile_select);
		mProfileListAdapter = new ArrayAdapter<VolumeProfile>(this, android.R.layout.simple_dropdown_item_1line);
		mProfileSelectView.setAdapter(mProfileListAdapter);
		mProfileSelectView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View item, int pos, long id) {
				mSelectedProfile = ((VolumeProfile)parent.getAdapter().getItem(pos));
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mSelectedProfile = null;
			}
		});
		mProfileSelectView.setEnabled(mChangeProfile);

		// volume lock
		mChangeVolumeLockCheckBox = (CheckBox)findViewById(R.id.plugin_volumelock_select_checkbox);
		mChangeVolumeLockCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mChangeVolumeLock = isChecked;
				mVolumeLockView.setEnabled(isChecked);
			}
		});
		mVolumeLockView = (Spinner)findViewById(R.id.plugin_volumelock_select);
		mVolumeLockAdapter = new VolumeLockAdapter(this, android.R.layout.simple_dropdown_item_1line);
		mVolumeLockAdapter.add(new VolumeLockItem(this, VolumeLockValue.LOCK));
		mVolumeLockAdapter.add(new VolumeLockItem(this, VolumeLockValue.UNLOCK));
		mVolumeLockAdapter.add(new VolumeLockItem(this, VolumeLockValue.TOGGLE));
		mVolumeLockView.setAdapter(mVolumeLockAdapter);
		mVolumeLockView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View item, int pos, long id) {
				mVolumeLockState = ((VolumeLockItem)parent.getAdapter().getItem(pos)).getValue();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mVolumeLockState = null;
			}
		});
		mVolumeLockView.setEnabled(mChangeVolumeLock);

		// ClearAudio+ state
		mChangeClearAudioPlusStateCheckBox = (CheckBox)findViewById(R.id.plugin_clearaudioplus_changestate);
		mChangeClearAudioPlusStateCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mChangeClearAudioPlus = isChecked;
				mClearAudioPlusStateView.setEnabled(isChecked);
			}
		});
		mClearAudioPlusStateView = (Spinner)findViewById(R.id.plugin_clearaudioplus_state);
		mClearAudioPlusStateAdapter = new ClearAudioPlusStateAdapter(this, android.R.layout.simple_dropdown_item_1line);
		mClearAudioPlusStateAdapter.add(new ClearAudioPlusStateItem(this, ClearAudioPlusStateValue.ON));
		mClearAudioPlusStateAdapter.add(new ClearAudioPlusStateItem(this, ClearAudioPlusStateValue.OFF));
		mClearAudioPlusStateAdapter.add(new ClearAudioPlusStateItem(this, ClearAudioPlusStateValue.TOGGLE));
		mClearAudioPlusStateView.setAdapter(mClearAudioPlusStateAdapter);
		mClearAudioPlusStateView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View item, int pos, long id) {
				mClearAudioPlusState = ((ClearAudioPlusStateItem)parent.getAdapter().getItem(pos)).getValue();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mClearAudioPlusState = null;
			}
		});
		mClearAudioPlusStateView.setEnabled(mChangeClearAudioPlus);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plugin_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_launch_application:
			startActivity(new Intent(getPackageManager().getLaunchIntentForPackage(getPackageName())));
			return true;
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		mChangeProfileCheckBox.setChecked(mChangeProfile);
		updateProfileList();
		mChangeVolumeLockCheckBox.setChecked(mChangeVolumeLock);
		mVolumeLockView.setSelection(mVolumeLockAdapter.getPosition(mVolumeLockState));
		mChangeClearAudioPlusStateCheckBox.setChecked(mChangeClearAudioPlus);
		mClearAudioPlusStateView.setSelection(mClearAudioPlusStateAdapter.getPosition(mClearAudioPlusState));
	}

	private void updateProfileList() {
		int selPos = -1;
		mProfileListAdapter.clear();

		ArrayList<VolumeProfile> plist = ProfileStore.getInstance(this).listProfiles();
		for ( VolumeProfile profile : plist) {
			mProfileListAdapter.add(profile);
			if (mSelectedProfile != null && mSelectedProfile.getUuid().equals(profile.getUuid())) {
				selPos = mProfileListAdapter.getCount() - 1;
			}
		}
		mProfileSelectView.setSelection(selPos);
		mProfileListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVE_CHANGEPROFILE, mChangeProfile);
		if (mSelectedProfile != null) {
			outState.putString(SAVE_SELECTEDPROFILEID, mSelectedProfile.getUuid().toString());
		}
		outState.putBoolean(SAVE_CHANGEVOLUMELOCK, mChangeVolumeLock);
		if (mVolumeLockState != null) {
			outState.putString(SAVE_SELECTEDVOLUMELOCK, mVolumeLockState.name());
		}
		outState.putBoolean(SAVE_CHANGECLEARAUDIOPLUSSTATE, mChangeClearAudioPlus);
		if (mClearAudioPlusState != null) {
			outState.putString(SAVE_SELECTEDCLEARAUDIOPLUSSTATE, mClearAudioPlusState.name());
		}
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

		BundleUtil resultBundle = new BundleUtil();
		MessageText blurb = new MessageText(",");
        if (mChangeProfile && mSelectedProfile != null) {
            resultBundle.setProfileId(mSelectedProfile.getUuid());
            resultBundle.setProfileName(mSelectedProfile.getName());
            blurb.addText(mSelectedProfile.getName());
        }
        if (mChangeVolumeLock && mVolumeLockState != null) {
            resultBundle.setVolumeLock(mVolumeLockState);
            blurb.addText(getString(mVolumeLockState.getResource()));
        }
        if (mChangeClearAudioPlus && mClearAudioPlusState != null) {
            resultBundle.setClearAudioPlusState(mClearAudioPlusState);
            blurb.addText(getString(mClearAudioPlusState.getResource()));
        }
        	
        resultIntent.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB, blurb.toString());
        resultIntent.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE, resultBundle.getBundle());

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
