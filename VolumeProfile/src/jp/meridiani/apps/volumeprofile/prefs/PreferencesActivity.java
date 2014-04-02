package jp.meridiani.apps.volumeprofile.prefs;

import jp.meridiani.apps.volumeprofile.R;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;

public class PreferencesActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

    }

	public static class PrefsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.prefs);

			Preference detectDevice = findPreference(Prefs.KEY_DETECT_DEVICE_FUNCTION);
			detectDevice.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference pref) {
					Prefs.getInstance(getActivity()).detectDeviceFunction(true);
					getActivity().finish();
					return true;
				}
			});
		}
	}
}
