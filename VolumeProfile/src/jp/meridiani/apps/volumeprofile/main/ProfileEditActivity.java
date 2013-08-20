package jp.meridiani.apps.volumeprofile.main;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class ProfileEditActivity extends FragmentActivity {
	public static final String EXTRA_PROFILE = "jp.meridiani.apps.volumeprofile.EXTRA_PROFILE";
	public static final String TAG_PROFILEEDIT = "profile_edit_fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_edit);

		Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		VolumeProfile profile = (VolumeProfile)intent.getParcelableExtra(EXTRA_PROFILE);
		if (profile == null) {
			finish();
			return;
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.activity_profile_edit, ProfileEditFragment.newInstance(profile), TAG_PROFILEEDIT);
		transaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save_profile:
			ProfileEditFragment fragment = (ProfileEditFragment)getSupportFragmentManager().findFragmentByTag(TAG_PROFILEEDIT);
			ProfileStore.getInstance(this).storeProfile(fragment.getVolumeProfile());
		case R.id.action_cancel_edit_profile:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
