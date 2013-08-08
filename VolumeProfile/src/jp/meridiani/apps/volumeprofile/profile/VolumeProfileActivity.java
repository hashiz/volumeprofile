package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import jp.meridiani.apps.volumeprofile.settings.PreferencesActivity;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class VolumeProfileActivity extends FragmentActivity implements
		ActionBar.TabListener, ProfileEditCallback {

	private static final String VOLUME_CHANGED_ACTION     = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE  = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    private static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
    private static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";

	private BroadcastReceiver mReceiver = null;
	private IntentFilter      mFilter   = null;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		// create Broadcast receiver
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(VOLUME_CHANGED_ACTION);
		mFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);

		mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (VOLUME_CHANGED_ACTION.equals(action)) {
				    int type = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
				    if (!AudioUtil.isSupportedType(type)) {
				    	return;
				    }
				    int volume = intent.getIntExtra(EXTRA_VOLUME_STREAM_VALUE, -1);
				    if (volume < 0) {
				    	return;
				    }
				    int prevVolume = intent.getIntExtra(EXTRA_PREV_VOLUME_STREAM_VALUE, -1);
				    onVolumeChanged(AudioUtil.getStreamType(type), volume, prevVolume);
				}
				else if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(action)) {
				    int mode = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1);
				    if (!AudioUtil.isSupportedMode(mode)) {
				    	return;
				    }
				    onRingerModeChanged(AudioUtil.getRingerMode(mode));
				}
			}

		};
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mReceiver, mFilter);
	}

	@Override
	protected void onPause(){
		super.onPause();

		unregisterReceiver(mReceiver);
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, PreferencesActivity.class));
			return true;
		case R.id.action_save_as_new_profile:
			VolumeProfile profile = ProfileStore.getInstance(getApplicationContext()).newProfile();
			new AudioUtil(getApplicationContext()).getVolumes(profile);
			ProfileNameDialog dialog = ProfileNameDialog.newInstance(null, profile, null, null);
			dialog.show(getSupportFragmentManager(), dialog.getClass().getCanonicalName());
			return true;
		}
		return false;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public static final int POS_VOLUME_EDIT  = 0;
		public static final int POS_PROFILE_LIST = 1;
		public static final int POS_NUMPAGES     = 2;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case POS_VOLUME_EDIT:
				fragment = VolumeEditFragment.newInstance();
				break;
			case POS_PROFILE_LIST:
				fragment = ProfileListFragment.newInstance();
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return POS_NUMPAGES;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case POS_VOLUME_EDIT:
				return getString(R.string.page_title_volumeedit);
			case POS_PROFILE_LIST:
				return getString(R.string.page_title_profileedit);
			}
			return null;
		}
	}

	public void updateProfileList() {
		ProfileListFragment fragment = (ProfileListFragment)mSectionsPagerAdapter.getItem(SectionsPagerAdapter.POS_PROFILE_LIST);
		if (fragment != null) {
			fragment.updateProfileList();
		}
	}

	public void updateVolumeEdit() {
		VolumeEditFragment fragment = (VolumeEditFragment)mSectionsPagerAdapter.getItem(SectionsPagerAdapter.POS_VOLUME_EDIT);
		if (fragment != null) {
			fragment.updateVolumeEdit();
		}
	}

	@Override
	public void onProfileEditPositive(VolumeProfile newProfile) {
		ProfileStore.getInstance(getApplicationContext()).storeProfile(newProfile);
		updateProfileList();
	}

	@Override
	public void onProfileEditNegative() {
	}
	
	private void onRingerModeChanged(RingerMode mode) {
		VolumeEditFragment fragment = (VolumeEditFragment)mSectionsPagerAdapter.getItem(SectionsPagerAdapter.POS_VOLUME_EDIT);
		if (fragment != null) {
			fragment.updateRingerMode();
		}
	}

	private void onVolumeChanged(StreamType type, int volume, int prevVolume) {
		VolumeEditFragment fragment = (VolumeEditFragment)mSectionsPagerAdapter.getItem(SectionsPagerAdapter.POS_VOLUME_EDIT);
		if (fragment != null) {
			fragment.updateVolume(type);
		}
	}
}
