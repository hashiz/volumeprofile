package jp.meridiani.apps.volumeprofile.pluginapi;

import java.util.ArrayList;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Spinner;

public class PluginEditActivity extends Activity {

	private static final String SAVE_CHANGEPROFILE     = "SAVE_CHANGEPROFILE";
	private static final String SAVE_SELECTEDPROFILEID = "SAVE_SELECTEDPROFILEID";
	private static final String SAVE_CHANGEVOLUMELOCK  = "SAVE_CHANGEVOLUMELOCK";
	private static final String SAVE_SELECTEDVOLUMELOCK= "SAVE_SELECTEDVOLUMELOCK";

	// values
	private UUID mInitialProfileId = null;
	private UUID mSelectedProfileId = null;
	private boolean mChangeProfile = false;
	private boolean mChangeVolumeLock  = false;
	private VolumeLockValue mVolumeLock = null;

	private enum VolumeLockValue {
		LOCK,
		UNLOCK,
		TOGGLE;
	}

	private class VolumeLockItem {
		private VolumeLockValue mValue;

		public VolumeLockItem(VolumeLockValue value) {
			mValue = value;
		}

		VolumeLockValue getValue() {
			return mValue;
		}

		@Override
		public String toString() {
			int id = 0;
			switch (mValue) {
			case LOCK:
				id = R.string.volumelock_lock;
				break;
			case UNLOCK:
				id = R.string.volumelock_unlock;
				break;
			case TOGGLE:
				id = R.string.volumelock_toggle;
				break;
			default:
				return "";
			}
			return getString(id);
		}
	}
	
	private class VolumeLockAdapter<T> extends ArrayAdapter<T> {

		public VolumeLockAdapter(Context context, int resource) {
			super(context, resource);
		}

		public int getPosition(VolumeLockValue value) {
			for (int pos = 0; pos < getCount(); pos++) {
				if (value == getItem(pos)) {
					return pos;
				}
			}
			return 0;
		}
	}

	// widgets
	private CheckBox mChangeProfileCheckBox = null;
	private CheckBox mChangeVolumeLockCheckBox = null;
	private Spinner mProfileSelectView = null;
	private Spinner mVolumeLockView = null;
	private ArrayAdapter<VolumeProfile> mProfileListAdapter = null;
	private VolumeLockAdapter<VolumeLockItem> mVolumeLockAdapter = null;
	private Button mSaveButton = null;
	private Button mCancelButton = null;
	private boolean mCanceled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String uuid = null;
		if (savedInstanceState != null) {
			mChangeProfile = savedInstanceState.getBoolean(SAVE_CHANGEPROFILE, false);
			uuid = savedInstanceState.getString(SAVE_SELECTEDPROFILEID);
			if (uuid != null) {
				mSelectedProfileId = UUID.fromString(uuid);
			}
			mChangeVolumeLock = savedInstanceState.getBoolean(SAVE_CHANGEVOLUMELOCK, false);
			mVolumeLock = VolumeLockValue.valueOf(savedInstanceState.getString(SAVE_SELECTEDVOLUMELOCK));
		}

		// receive intent and extra data
		Intent intent = getIntent();
		if (!com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING.equals(intent.getAction())) {
			super.finish();
			return;
		}

		BundleUtil bundle;

		try {
			bundle = new BundleUtil(getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE));
			mInitialProfileId = bundle.getProfileId();
		} catch (InvalidBundleException e) {
			mInitialProfileId = null;
		}

		if (mSelectedProfileId == null && mInitialProfileId != null) {
			mSelectedProfileId = mInitialProfileId;
		}

		mCanceled = false;

		// set view
		setContentView(R.layout.activity_plugin_edit);

		// profile list
		mChangeProfileCheckBox = (CheckBox)findViewById(R.id.plugin_profile_select_checkbox);
		mProfileSelectView = (Spinner)findViewById(R.id.plugin_profile_select);
		mProfileListAdapter = new ArrayAdapter<VolumeProfile>(this,
				android.R.layout.simple_list_item_single_choice);
		mProfileSelectView.setAdapter(mProfileListAdapter);
		mProfileSelectView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View item, int pos, long id) {
				mSelectedProfileId = ((VolumeProfile)parent.getAdapter().getItem(pos)).getUuid();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mSelectedProfileId = null;
			}
		});

		// volume lock
		mChangeVolumeLockCheckBox = (CheckBox)findViewById(R.id.plugin_volumelock_select_checkbox);
		mVolumeLockView = (Spinner)findViewById(R.id.plugin_volumelock_select);
		mVolumeLockAdapter = new VolumeLockAdapter<VolumeLockItem>(this,
				android.R.layout.simple_list_item_single_choice);
		mVolumeLockAdapter.add(new VolumeLockItem(VolumeLockValue.LOCK));
		mVolumeLockAdapter.add(new VolumeLockItem(VolumeLockValue.UNLOCK));
		mVolumeLockAdapter.add(new VolumeLockItem(VolumeLockValue.TOGGLE));
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
	}

	private void updateProfileList() {
		int selPos = -1;
		mProfileListAdapter.clear();

		ArrayList<VolumeProfile> plist = ProfileStore.getInstance(this).listProfiles();
		for ( VolumeProfile profile : plist) {
			mProfileListAdapter.add(profile);
			if (mSelectedProfileId != null && mSelectedProfileId.equals(profile.getUuid())) {
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
		if (mSelectedProfileId != null) {
			outState.putString(SAVE_SELECTEDPROFILEID, mSelectedProfileId.toString());
		}
		outState.putBoolean(SAVE_CHANGEVOLUMELOCK, mChangeVolumeLock);
		outState.putString(SAVE_SELECTEDVOLUMELOCK, mVolumeLock.name());
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

        
        && mSelectedProfileId != null && mSelectedProfileId != null) {
            BundleUtil resultBundle = new BundleUtil();
            resultBundle.setProfileId(profile.getUuid());
            resultBundle.setProfileName(profile.getName());

            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, profile.getName());
            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle.getBundle());

            setResult(RESULT_OK, resultIntent);
        }
        else {
            setResult(RESULT_CANCELED, resultIntent);
        }
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
