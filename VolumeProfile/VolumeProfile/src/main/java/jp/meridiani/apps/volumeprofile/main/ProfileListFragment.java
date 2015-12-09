package jp.meridiani.apps.volumeprofile.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.main.DragDropListView.OnSortedListener;
import jp.meridiani.apps.volumeprofile.profile.CurrentProfile;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ProfileListFragment extends Fragment implements OnItemClickListener, ProfileEditCallback, OnSortedListener {

	ProfileListAdapter mAdapter = null;
	DragDropListView   mProfileListView = null;
	ProfileStore       mProfileStore = null;
	ProfileStore.OnProfileSwitchedListener mListener = null;
	
	public static ProfileListFragment newInstance() {
    	return new ProfileListFragment();
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_profilelist,
				container, false);
		mAdapter = new ProfileListAdapter(getActivity(),
				R.layout.profile_list_item, R.id.profile_list_item_chekedtext);
		mProfileListView = (DragDropListView)rootView.findViewById(R.id.profile_list);
		mProfileListView.setAdapter(mAdapter);
		mProfileListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mProfileListView.setOnItemClickListener(this);
		mProfileListView.setOnSortedListener(this);
		registerForContextMenu(mProfileListView);
		mProfileStore = ProfileStore.getInstance(getActivity());
		mListener = new ProfileStore.OnProfileSwitchedListener() {
			@Override
			public void onProfileSwitched(UUID newId, UUID prevId) {
				int pos = mAdapter.getPosition(newId);
				if (pos >= 0) {
					mProfileListView.setItemChecked(pos, true);
				}
			}
		};
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateProfileList();
		mProfileStore.registerOnProfileSwitchedListener(mListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		mProfileStore.unregisterOnProfileSwitchedListener(mListener);
	}

	public void updateProfileList() {
		Context context = getActivity();

		mAdapter.clear();

		ArrayList<VolumeProfile> plist = ProfileStore.getInstance(context).listProfiles();
		int selPos = -1;
		UUID curId = ProfileStore.getInstance(context).getCurrentProfile() ;
		for ( VolumeProfile profile : plist) {
			mAdapter.add(profile);
			if (curId != null && curId.equals(profile.getUuid())) {
				selPos = mAdapter.getCount() - 1;
			}
		}
		if (selPos >= 0) {
			mProfileListView.setItemChecked(selPos, true);
		}

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		ProfileListAdapter adapter = (ProfileListAdapter)parent.getAdapter();
		VolumeProfile profile = adapter.getItem(pos);
		CurrentProfile.setCurrentProfile(parent.getContext(), profile.getUuid());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.profile, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int pos = info.position;
		ListView profileListView = (ListView)getView().findViewById(R.id.profile_list);
		VolumeProfile profile = (VolumeProfile)profileListView.getAdapter().getItem(pos);
		switch (item.getItemId()) {
		case R.id.action_rename_profile:
			ProfileNameDialog dialog = ProfileNameDialog.newInstance(profile, this, null, getString(R.string.input_dialog_rename_button), null);
			dialog.show(getFragmentManager(), dialog.getClass().getCanonicalName());
			return true;
		case R.id.action_edit_profile:
			Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
			intent.putExtra(ProfileEditActivity.EXTRA_PROFILE, profile);
			startActivity(intent);
			return true;
		case R.id.action_delete_profile:
			ProfileStore.getInstance(getActivity()).deleteProfile(profile.getUuid());
			updateProfileList();
			return true;
		}
		return false;
	}

	@Override
	public void onProfileEditPositive(VolumeProfile newProfile) {
		ProfileStore.getInstance(getActivity()).storeProfile(newProfile);
		updateProfileList();
	}

	@Override
	public void onProfileEditNegative() {
	}

	@Override
	public void onSorted(List<VolumeProfile> list) {
		ProfileStore.getInstance(getActivity()).updateDisplayOrder(list);
	}
}
