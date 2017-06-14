package jp.meridiani.apps.volumeprofile.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.event.Event;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;

public class EventEditActivity extends FragmentActivity {
	public static final String EXTRA_EVENT = "jp.meridiani.apps.volumeprofile.EXTRA_EVENT";
	public static final String TAG_EVENTEDIT = "event_edit_fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_edit);

		Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		Event event = (Event)intent.getParcelableExtra(EXTRA_EVENT);
		if (event == null) {
			finish();
			return;
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.activity_event_edit, EventEditFragment.newInstance(event), TAG_EVENTEDIT);
		transaction.commit();
	}
}
