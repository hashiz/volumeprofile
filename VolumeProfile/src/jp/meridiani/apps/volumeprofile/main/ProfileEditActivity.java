package jp.meridiani.apps.volumeprofile.main;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

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
}
