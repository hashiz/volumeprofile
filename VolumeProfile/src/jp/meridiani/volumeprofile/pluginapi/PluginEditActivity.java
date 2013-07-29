package jp.meridiani.volumeprofile.pluginapi;

import java.util.ArrayList;

import jp.meridiani.volumeprofile.R;
import jp.meridiani.volumeprofile.profile.ProfileStore;
import jp.meridiani.volumeprofile.profile.VolumeProfile;
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

    public static final String BUNDLE_PROFILEID   = "jp.meridiani.apps.volumeprofile.extra.INTEGER_PROFILEID";

	private int mSelectedProfileId;
	private ListView mProfileListView;
	private Button mSelectButton;
	private Button mCancelButton;
	private boolean mCanceled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// receive intent and extra data
		Intent intent = getIntent();
		if (!com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING.equals(intent.getAction())) {
			super.finish();
			return;
		}

		Bundle bundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);

		mSelectedProfileId = -1;
		if (bundle != null) {
			mSelectedProfileId = bundle.getInt(BUNDLE_PROFILEID);
		}

		mCanceled = false;

		// set view
		setContentView(R.layout.activity_plugin_edit);
		mProfileListView = (ListView)findViewById(R.id.ProfileList);
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

		updateProfileList();
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
        if (! mCanceled && profile != null && profile.getProfileId() >= 0) {
            Bundle resultBundle = new Bundle();
            resultBundle.putInt(BUNDLE_PROFILEID, profile.getProfileId());

            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, profile.getProfileName());
            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);

            setResult(RESULT_OK, resultIntent);
        }
        else {
            setResult(RESULT_CANCELED, resultIntent);
        }
    	super.finish();
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	private void updateProfileList() {
		ProfileStore pstore = new ProfileStore(this);

		ArrayList<VolumeProfile> plist = pstore.listProfiles();

		ArrayAdapter<VolumeProfile> adapter = new ArrayAdapter<VolumeProfile>(this,
							android.R.layout.simple_list_item_single_choice);
		int selPos = 0;
		for ( VolumeProfile profile : plist) {
			adapter.add(profile);
			if (mSelectedProfileId == profile.getProfileId()) {
				selPos = adapter.getCount() - 1;
			}
		}

		ListView profileListView = (ListView)findViewById(R.id.ProfileList);
		profileListView.setAdapter(adapter);
		profileListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		profileListView.setItemChecked(selPos, true);
		profileListView.setOnItemSelectedListener(this);
		profileListView.setOnItemClickListener(this);
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
