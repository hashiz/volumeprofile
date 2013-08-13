package jp.meridiani.apps.volumeprofile.profile;

import java.util.ArrayList;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
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

public class ProfileListFragment extends Fragment implements OnItemClickListener, ProfileEditCallback {

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
		ProfileListAdapter adapter = new ProfileListAdapter(getActivity(),
				R.layout.profile_list_item, R.id.profile_list_item_chekedtext);
		ListView profileListView = (ListView)rootView.findViewById(R.id.profile_list);
		profileListView.setAdapter(adapter);
		profileListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		profileListView.setOnItemClickListener(this);
		registerForContextMenu(profileListView);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateProfileList();
	}

	private View getRootView() throws ViewNotAvailableException {
		View rootView = getView();
		if (rootView == null) {
			throw new ViewNotAvailableException();
		}
		return rootView;
	}

	public void updateProfileList() {
		View rootView;
		try {
			rootView = getRootView();
		} catch (ViewNotAvailableException e) {
			return;
		}
		Context context = getActivity();

		ListView profileListView = (ListView)rootView.findViewById(R.id.profile_list);
		ProfileListAdapter adapter = (ProfileListAdapter)profileListView.getAdapter();
		adapter.clear();

		ArrayList<VolumeProfile> plist = ProfileStore.getInstance(context).listProfiles();
		int selPos = -1;
		UUID curId = ProfileStore.getInstance(context).getCurrentProfile() ;
		for ( VolumeProfile profile : plist) {
			adapter.add(profile);
			if (curId != null && curId.equals(profile.getUuid())) {
				selPos = adapter.getCount() - 1;
			}
		}
		if (selPos >= 0) {
			profileListView.setItemChecked(selPos, true);
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		ProfileListAdapter adapter = (ProfileListAdapter)parent.getAdapter();
		VolumeProfile profile = adapter.getItem(pos);
		new AudioUtil(parent.getContext()).applyProfile(profile);
		ProfileStore.getInstance(getActivity()).setCurrentProfile(profile.getUuid());
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
}
