package jp.meridiani.apps.volumeprofile.pluginapi;

import java.util.ArrayList;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusStateAdapter;
import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusStateItem;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockAdapter;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockItem;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockState;
import jp.meridiani.apps.volumeprofile.MessageText;
import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.profile.ProfileNotFoundException;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusState;

import android.app.Activity;
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

public class PluginEditActivity extends Activity {

	private static final String SAVE_CHANGEPROFILE     = "SAVE_CHANGEPROFILE";
	private static final String SAVE_SELECTEDPROFILEID = "SAVE_SELECTEDPROFILEID";
	private static final String SAVE_CHANGEVOLUMELOCK  = "SAVE_CHANGEVOLUMELOCK";
	private static final String SAVE_SELECTEDVOLUMELOCK= "SAVE_SELECTEDVOLUMELOCK";
	private static final String SAVE_CHANGECLEARAUDIOPLUSSTATE  = "SAVE_CHANGECLEARAUDIOPLUSSTATE";
	private static final String SAVE_SELECTEDCLEARAUDIOPLUSSTATE = "SAVE_SELECTEDCLEARAUDIOPLUSSTATE";

	// values
	private VolumeProfile mSelectedProfile = null;
	private boolean mChangeProfile = false;
	private boolean mChangeVolumeLock  = false;
	private boolean mChangeClearAudioPlusState  = false;
	private VolumeLockState mVolumeLock = VolumeLockState.LOCK;
	private ClearAudioPlusState mClearAudioPlusState = ClearAudioPlusState.ON;

	// widgets
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
			mChangeProfile = savedInstanceState.getBoolean(SAVE_CHANGEPROFILE, true);
			String uuid = savedInstanceState.getString(SAVE_SELECTEDPROFILEID);
			if (uuid != null) {
				try {
					mSelectedProfile = store.loadProfile(UUID.fromString(uuid));
				}
				catch (ProfileNotFoundException e) {
					// ignore
				}
			}
			mChangeVolumeLock = savedInstanceState.getBoolean(SAVE_CHANGEVOLUMELOCK, false);
			String volumelock = savedInstanceState.getString(SAVE_SELECTEDVOLUMELOCK);
			if (volumelock != null) {
				mVolumeLock = VolumeLockState.valueOf(volumelock);
			}
			mChangeClearAudioPlusState = savedInstanceState.getBoolean(SAVE_CHANGECLEARAUDIOPLUSSTATE, false);
			String capState = savedInstanceState.getString(SAVE_SELECTEDCLEARAUDIOPLUSSTATE);
			if (capState != null) {
				mClearAudioPlusState = ClearAudioPlusState.valueOf(capState);
			}
		}
		else {
			// initial value
			mChangeProfile = false;
			mSelectedProfile = null;
			mChangeVolumeLock = false;
			mVolumeLock = null;
			mChangeClearAudioPlusState = false;
			mClearAudioPlusState = null;

			// receive intent and extra data
			Intent intent = getIntent();
			if (!com.twofortyfouram.locale.api.Intent.ACTION_EDIT_SETTING.equals(intent.getAction())) {
				super.finish();
				return;
			}
	
			BundleUtil bundle;
	
			try {
				bundle = new BundleUtil(getIntent().getBundleExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE));
				UUID uuid = bundle.getProfileId();
				if (uuid != null) {
					mChangeProfile = true;
					try {
						mSelectedProfile = store.loadProfile(uuid);
					}
					catch (ProfileNotFoundException e) {
						// search profile by name if uuid not found.
						String name = bundle.getProfileName();
						if (name != null) {
							try {
								mSelectedProfile = store.loadProfile(name);
							}
							catch (ProfileNotFoundException e2) {
								mSelectedProfile = null;
							}
						}
					}
				}
				VolumeLockState lock = bundle.getVolumeLock();
				if (lock != null) {
					mChangeVolumeLock = true;
					mVolumeLock = lock;
				}
				ClearAudioPlusState state = bundle.getClearAudioPlusState();
				if (state != null) {
					mChangeClearAudioPlusState = true;
					mClearAudioPlusState = state;
				}
			} catch (InvalidBundleException e) {
				// new configuration, set default value
				mChangeProfile = true;
				mSelectedProfile = null;
				mChangeVolumeLock = false;
				mVolumeLock = VolumeLockState.LOCK;
				mChangeClearAudioPlusState = false;
				mClearAudioPlusState = ClearAudioPlusState.ON;
			}
		}

		mCanceled = false;

		// set view
		setContentView(R.layout.activity_plugin_edit);

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
		mVolumeLockAdapter.add(new VolumeLockItem(this, VolumeLockState.LOCK));
		mVolumeLockAdapter.add(new VolumeLockItem(this, VolumeLockState.UNLOCK));
		mVolumeLockAdapter.add(new VolumeLockItem(this, VolumeLockState.TOGGLE));
		mVolumeLockView.setAdapter(mVolumeLockAdapter);
		mVolumeLockView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View item, int pos, long id) {
				mVolumeLock = ((VolumeLockItem)parent.getAdapter().getItem(pos)).getValue();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mVolumeLock = null;
			}
		});
		mVolumeLockView.setEnabled(mChangeVolumeLock);

		// ClearAudio+ state
		mChangeClearAudioPlusStateCheckBox = (CheckBox)findViewById(R.id.plugin_clearaudioplus_changestate);
		mChangeClearAudioPlusStateCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mChangeClearAudioPlusState = isChecked;
				mClearAudioPlusStateView.setEnabled(isChecked);
			}
		});
		mClearAudioPlusStateView = (Spinner)findViewById(R.id.plugin_clearaudioplus_state);
		mClearAudioPlusStateAdapter = new ClearAudioPlusStateAdapter(this, android.R.layout.simple_dropdown_item_1line);
		mClearAudioPlusStateAdapter.add(new ClearAudioPlusStateItem(this, ClearAudioPlusState.ON));
		mClearAudioPlusStateAdapter.add(new ClearAudioPlusStateItem(this, ClearAudioPlusState.OFF));
		mClearAudioPlusStateAdapter.add(new ClearAudioPlusStateItem(this, ClearAudioPlusState.TOGGLE));
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
		mClearAudioPlusStateView.setEnabled(mChangeClearAudioPlusState);
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
		mVolumeLockView.setSelection(mVolumeLockAdapter.getPosition(mVolumeLock));
		mChangeClearAudioPlusStateCheckBox.setChecked(mChangeClearAudioPlusState);
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
		if (mVolumeLock != null) {
			outState.putString(SAVE_SELECTEDVOLUMELOCK, mVolumeLock.name());
		}
		outState.putBoolean(SAVE_CHANGECLEARAUDIOPLUSSTATE, mChangeClearAudioPlusState);
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
        if (mChangeVolumeLock && mVolumeLock != null) {
            resultBundle.setVolumeLock(mVolumeLock);
            blurb.addText(getString(mVolumeLock.getResource()));
        }
        if (mChangeClearAudioPlusState && mClearAudioPlusState != null) {
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
