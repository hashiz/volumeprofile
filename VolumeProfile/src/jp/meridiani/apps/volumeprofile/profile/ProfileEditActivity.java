package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class ProfileEditActivity extends FragmentActivity {
	public static final String EXTRA_PROFILE = "jp.meridiani.apps.volumeprofile.EXTRA_PROFILE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_edit);
		// Show the Up button in the action bar.
		setupActionBar();

		Intent intent = getIntent();
		if (intent == null) {
			finish();
		}
		VolumeProfile profile = (VolumeProfile)intent.getParcelableExtra(EXTRA_PROFILE);
		if (profile == null) {
			finish();
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(ProfileEditFragment.newInstance(profile), ProfileEditFragment.class.getCanonicalName());
		transaction.commit();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

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
			ProfileEditFragment fragment = (ProfileEditFragment)getSupportFragmentManager().findFragmentByTag(ProfileEditFragment.class.getCanonicalName());
			ProfileStore.getInstance(this).storeProfile(fragment.getVolumeProfile());
		case R.id.action_cancel_edit_profile:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
