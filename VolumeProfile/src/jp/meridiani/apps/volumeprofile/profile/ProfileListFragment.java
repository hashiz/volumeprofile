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
import android.widget.ArrayAdapter;
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
		View rootView = inflater.inflate(R.layout.fragment_main_profileedit,
				container, false);
		ListView profileListView = (ListView)rootView.findViewById(R.id.profile_edit);
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

	public void updateProfileList() {
		Context context = getActivity();

		ArrayAdapter<VolumeProfile> adapter = new ArrayAdapter<VolumeProfile>(getActivity(),
				android.R.layout.simple_list_item_single_choice);
		ListView profileListView = (ListView)getView().findViewById(R.id.profile_edit);
		profileListView.setAdapter(adapter);
		
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
		VolumeProfile profile = (VolumeProfile)parent.getAdapter().getItem(pos);
		new AudioUtil(parent.getContext()).applyProfile(profile);
		ProfileStore.getInstance(getActivity()).setCurrentProfile(profile.getUuid());
		((VolumeProfileActivity)getActivity()).updateVolumeEdit();
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
		ListView profileListView = (ListView)getView().findViewById(R.id.profile_edit);
		VolumeProfile profile = (VolumeProfile)profileListView.getAdapter().getItem(pos);
		switch (item.getItemId()) {
		case R.id.action_rename_profile:
			ProfileNameDialog dialog = ProfileNameDialog.newInstance(null, profile, null, null);
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
