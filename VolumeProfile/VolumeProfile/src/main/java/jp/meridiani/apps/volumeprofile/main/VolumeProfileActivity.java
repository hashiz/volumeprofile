package jp.meridiani.apps.volumeprofile.main;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.prefs.PreferencesActivity;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore.OnVolumeLockChangedListener;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

public class VolumeProfileActivity extends FragmentActivity implements
		ActionBar.TabListener, ProfileEditCallback, OnVolumeLockChangedListener {

	private static final String LASTVIEWEDPAGE = "last_viewed_page";
	private static final int STARTUPPAGE = 1;
	private int mLastViewedPage = -1;
	private ProfileStore mProfileStore = null;
	private MenuItem mVolumeLockMenuItem = null;

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
						mLastViewedPage = position;
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

		if (savedInstanceState != null) {
			mLastViewedPage = savedInstanceState.getInt(LASTVIEWEDPAGE);
		}

		mProfileStore = ProfileStore.getInstance(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (mLastViewedPage < 0) {
			mViewPager.setCurrentItem(STARTUPPAGE);
		}
		else {
			mViewPager.setCurrentItem(mLastViewedPage);
		}
		if (mVolumeLockMenuItem != null) {
			setVolumeLockMenuItem(mVolumeLockMenuItem, mProfileStore.isVolumeLocked());
		}
		mProfileStore.registerOnVolumeLockChangedListener(this);
	};

	@Override
	protected void onPause() {
		super.onPause();
		mProfileStore.unregisterOnVolumeLockChangedListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(LASTVIEWEDPAGE, mLastViewedPage);
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setVolumeLockMenuItem(MenuItem item, boolean locked) {
		if (locked) {
			item.setIcon(R.drawable.ic_action_secure);
			item.setTitle(R.string.action_unlock_volume);
		}
		else {
			item.setIcon(R.drawable.ic_action_not_secure);
			item.setTitle(R.string.action_lock_volume);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem item = menu.findItem(R.id.action_lock_volume);
		setVolumeLockMenuItem(item, mProfileStore.isVolumeLocked());
		mVolumeLockMenuItem = item;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, PreferencesActivity.class));
			return true;
		case R.id.action_lock_volume:
			// toggle lock
			mProfileStore.setVolumeLocked(!mProfileStore.isVolumeLocked());
			// set icon/title
			setVolumeLockMenuItem(item, mProfileStore.isVolumeLocked());
			return true;
		case R.id.action_save_as_new_profile:
			VolumeProfile profile = mProfileStore.newProfile();
			new AudioUtil(getApplicationContext()).getVolumes(profile);
			ProfileNameDialog dialog = ProfileNameDialog.newInstance(profile, this);
			dialog.show(getSupportFragmentManager(), dialog.getClass().getCanonicalName());
			return true;
		case R.id.action_about:
			startActivity(new Intent(this, AboutActivity.class));
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

		private String mTags[] = new String[POS_NUMPAGES];

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = (Fragment)super.instantiateItem(container, position);
			
			mTags[position] = fragment.getTag();
			return fragment;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			mTags[position] = null;
		};

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
		
		public String getTag(int position) {
			return mTags[position];
		}
	}

	public void updateProfileList() {
		String tag = (String)mSectionsPagerAdapter.getTag(SectionsPagerAdapter.POS_PROFILE_LIST);
		if (tag == null) {
			return;
		}
		ProfileListFragment fragment = (ProfileListFragment)getSupportFragmentManager().findFragmentByTag(tag);
		if (fragment != null) {
			fragment.updateProfileList();
		}
	}

	@Override
	public void onProfileEditPositive(VolumeProfile newProfile) {
		mProfileStore.storeProfile(newProfile);
		updateProfileList();
	}

	@Override
	public void onProfileEditNegative() {
	}

	@Override
	public void onVolumeLockChanged(boolean isLocked) {
		if (mVolumeLockMenuItem != null) {
			setVolumeLockMenuItem(mVolumeLockMenuItem, isLocked);
		}
	}
}
