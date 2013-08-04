package jp.meridiani.apps.volumeprofile.profile;

import java.util.ArrayList;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.RingerMode;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil.StreamType;
import jp.meridiani.apps.volumeprofile.profile.ProfileNameDialog.ProfileNameDialogListner;
import jp.meridiani.apps.volumeprofile.settings.PreferencesActivity;
import jp.meridiani.apps.volumeprofile.settings.Prefs;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class VolumeProfileActivity extends FragmentActivity implements
		ActionBar.TabListener, ProfileNameDialogListner{

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
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
	}

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
			ProfileNameDialog dialog = ProfileNameDialog.newInstance(null, null, null);
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

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new VolumeEditFragment();
				break;
			case 1:
				fragment = new ProfileEditFragment();
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.page_title_volumeedit);
			case 1:
				return getString(R.string.page_title_profileedit);
			}
			return null;
		}
	}

	/************
	 * 
	 * @author hashiz
	 * Profile Edit
	 *
	 */
	public static class ProfileEditFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

		public ProfileEditFragment() {
		}

		@Override public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			updateProfileList();
		};

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_profileedit,
					container, false);
			
			return rootView;
		}

		private void updateProfileList() {
			Activity activity = getActivity();
			ArrayList<VolumeProfile> plist = ProfileStore.getInstance(getActivity()).listProfiles();

			ArrayAdapter<VolumeProfile> adapter = new ArrayAdapter<VolumeProfile>(getActivity(),
								android.R.layout.simple_list_item_single_choice);
			int selPos = 0;
			for ( VolumeProfile profile : plist) {
				adapter.add(profile);
				UUID curId = ProfileStore.getInstance(activity).getCurrentProfile() ;
				if (curId != null && curId.equals(profile.getUuid())) {
					selPos = adapter.getCount() - 1;
				}
			}

			ListView profileListView = (ListView)activity.findViewById(R.id.profile_edit);
			profileListView.setAdapter(adapter);
			profileListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			profileListView.setItemChecked(selPos, true);
			profileListView.setOnItemClickListener(this);
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			ListView listView = (ListView)view;
			VolumeProfile profile = (VolumeProfile)listView.getAdapter().getItem(pos);
			new AudioUtil(parent.getContext()).applyProfile(profile);
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
			return false;
		}
	}

	/************
	 * 
	 * @author hashiz
	 * Profile Edit
	 *
	 */
	public static class VolumeEditFragment extends Fragment {

		private class VolumeBarListener implements SeekBar.OnSeekBarChangeListener {
			private StreamType mStreamType ;
			private AudioUtil  mAudio ;
			private SeekBar    mSeekBar ;
			private TextView   mValueTextView;

			public VolumeBarListener(Context context, StreamType type) {
				mStreamType = type;
				mAudio = new AudioUtil(context);
				mSeekBar = findSeekBar(type);
				mValueTextView = findValueView(type);

				int volume = mAudio.getVolume(mStreamType);
				int maxVolume = mAudio.getMaxVolume(mStreamType);

				mSeekBar.setMax(mAudio.getMaxVolume(type));
				mSeekBar.setProgress(mAudio.getVolume(type));
				mValueTextView.setText(String.format("%2d/%2d", volume, maxVolume));
				mSeekBar.setOnSeekBarChangeListener(this);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mAudio.setVolume(mStreamType, progress, Prefs.getInstance(getActivity()).isPlaySoundOnVolumeChange());
				int volume = mAudio.getVolume(mStreamType);
				seekBar.setProgress(volume);
				mValueTextView.setText(String.format("%2d/%2d", volume, seekBar.getMax()));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO 自動生成されたメソッド・スタブ
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO 自動生成されたメソッド・スタブ
				
			}
			
		}

		private class RingerModeItem {
			private Context    mContext;
			private RingerMode mRingerMode;

			public RingerModeItem(Context context, RingerMode mode) {
				mContext    = context;
				mRingerMode = mode;
			}
			public String toString() {
				switch (mRingerMode) {
				case NORMAL:
					return mContext.getString(R.string.ringer_mode_normal);
				case VIBRATE:
					return mContext.getString(R.string.ringer_mode_vibrate);
				case SILENT:
					return mContext.getString(R.string.ringer_mode_sirent);
				}
				return null;
			}
			public RingerMode getValue() {
				return mRingerMode;
			}
		}

		public VolumeEditFragment() {
		}

		private class RingerModeAdapter extends ArrayAdapter<RingerModeItem> {

			public RingerModeAdapter(Context context, int textViewResourceId) {
				super(context, textViewResourceId);
				// TODO 自動生成されたコンストラクター・スタブ
			}

			public int getPosition(RingerMode mode) {
				for (int i = 0; i < this.getCount(); i++ ) {
					if (this.getItem(i).getValue() == mode) {
						return i;
					}
				}
				return -1;
			}
			
		}

		@Override public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			Activity activity = getActivity();
			final AudioUtil audio = new AudioUtil(getActivity());

			// Ringer Mode
			final Spinner ringerModeView = (Spinner)activity.findViewById(R.id.ringer_mode_value);

			final RingerModeAdapter adapter = new RingerModeAdapter(activity,
					android.R.layout.simple_list_item_single_choice);
			final RingerModeItem[] itemList = new RingerModeItem[] {
					new RingerModeItem(activity, RingerMode.NORMAL),
					new RingerModeItem(activity, RingerMode.VIBRATE),
					new RingerModeItem(activity, RingerMode.SILENT),
			};

			for (RingerModeItem item : itemList ) {
				adapter.add(item);
			}
			ringerModeView.setAdapter(adapter);
			ringerModeView.setSelection(adapter.getPosition(audio.getRingerMode()));
			ringerModeView.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					RingerModeItem item = adapter.getItem(pos);
					audio.setRingerMode(item.getValue());
					ringerModeView.setSelection(adapter.getPosition(audio.getRingerMode()));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// nop
				}
			});

			// Volumes
			for (StreamType streamType : new StreamType[] {
					StreamType.ALARM,
					StreamType.MUSIC,
					StreamType.RING,
					StreamType.SYSTEM,
					StreamType.VOICE_CALL}) {
				new VolumeBarListener(activity, streamType);
			}
		};

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_volumeedit,
					container, false);
			return rootView;
		}

		private TextView findValueView(StreamType type) {
			int id = -1;
			switch (type) {
			case ALARM:
				id = R.id.aloarm_volume_value;
				break;
			case MUSIC:
				id = R.id.media_volume_value;
				break;
			case RING:
				id = R.id.ring_volume_value;
				break;
			case SYSTEM:
				id = R.id.system_volume_value;
				break;
			case VOICE_CALL:
				id = R.id.voicecall_volume_value;
				break;
			default:
				return null;
			}
			return (TextView)getActivity().findViewById(id);
		}

		private SeekBar findSeekBar(StreamType type) {
			int id = -1;
			switch (type) {
			case ALARM:
				id = R.id.alarm_volume_seekBar;
				break;
			case MUSIC:
				id = R.id.media_volume_seekBar;
				break;
			case RING:
				id = R.id.ring_volume_seekBar;
				break;
			case SYSTEM:
				id = R.id.system_volume_seekBar;
				break;
			case VOICE_CALL:
				id = R.id.voicecall_volume_seekBar;
				break;
			default:
				return null;
			}
			return (SeekBar)getActivity().findViewById(id);
		}
	}

	@Override
	public void onInputDialogPositive(String profileName) {
		ProfileStore store = ProfileStore.getInstance(this);
		VolumeProfile profile = store.newProfile();
		profile.setName(profileName);
		new AudioUtil(this).getVolumes(profile);
		store.storeProfile(profile);
	}

	@Override
	public void onInputDialogNegative() {
		// NOP
	}
}
