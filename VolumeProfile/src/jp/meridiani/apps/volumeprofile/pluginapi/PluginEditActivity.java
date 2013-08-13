package jp.meridiani.apps.volumeprofile.pluginapi;

import java.util.ArrayList;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class PluginEditActivity extends Activity implements OnItemSelectedListener, OnItemClickListener {

	private static final String SAVE_SELECTEDPROFILEID = "SAVE_SELECTEDPROFILEID";

	private UUID mInitialProfileId;
	private UUID mSelectedProfileId;
	private ListView mProfileListView;
	private ArrayAdapter<VolumeProfile> mAdapter;
	private Button mSelectButton;
	private Button mCancelButton;
	private boolean mCanceled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String uuid = savedInstanceState.getString(SAVE_SELECTEDPROFILEID);
		if (uuid != null) {
			mSelectedProfileId = UUID.fromString(uuid);
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
		mProfileListView = (ListView)findViewById(R.id.plugin_profile_list);
		mProfileListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mAdapter = new ArrayAdapter<VolumeProfile>(this,
				android.R.layout.simple_list_item_single_choice);
		mProfileListView.setAdapter(mAdapter);
		mProfileListView.setOnItemSelectedListener(this);
		mProfileListView.setOnItemClickListener(this);

		mSelectButton = (Button)findViewById(R.id.select_button);
		mCancelButton = (Button)findViewById(R.id.cancel_button);

		mSelectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSelectClick((Button)v);
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
	public void onStart() {
		updateProfileList();
	}

	private void updateProfileList() {
		int selPos = -1;
		mAdapter.clear();

		ArrayList<VolumeProfile> plist = ProfileStore.getInstance(this).listProfiles();
		for ( VolumeProfile profile : plist) {
			mAdapter.add(profile);
			if (mSelectedProfileId != null && mSelectedProfileId.equals(profile.getUuid())) {
				selPos = mAdapter.getCount() - 1;
			}
		}
		if (selPos >= 0) {
			mProfileListView.setItemChecked(selPos, true);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mSelectedProfileId != null) {
			outState.putString(SAVE_SELECTEDPROFILEID, mSelectedProfileId.toString());
		}
	}

	@Override
    public void finish()
    {
		int selPos = -1;
		if (mProfileListView.getCount() > 0) {
			selPos = mProfileListView.getCheckedItemPosition();
		}
		VolumeProfile profile = null;
		if (selPos >= 0) {
			profile = (VolumeProfile)mProfileListView.getAdapter().getItem(selPos);
		}

        Intent resultIntent = new Intent();
        if (! mCanceled && profile != null && profile.getUuid() != null) {
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

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		mSelectedProfileId = ((VolumeProfile)parent.getAdapter().getItem(position)).getUuid();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		mSelectedProfileId = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	private void onSelectClick(Button b) {
		mCanceled = false;
		finish();
	}

	private void onCancelClick(Button b) {
		mCanceled = true;
		finish();
	}
}
