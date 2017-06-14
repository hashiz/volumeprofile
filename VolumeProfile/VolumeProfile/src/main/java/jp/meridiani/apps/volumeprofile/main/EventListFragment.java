package jp.meridiani.apps.volumeprofile.main;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.event.Event;
import jp.meridiani.apps.volumeprofile.event.EventStore;
import jp.meridiani.apps.volumeprofile.main.DragDropListView.OnSortedListener;
import jp.meridiani.apps.volumeprofile.profile.CurrentProfile;
import jp.meridiani.apps.volumeprofile.profile.ProfileNotFoundException;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;

public class EventListFragment extends Fragment implements OnItemClickListener, EventEditCallback, OnSortedListener {

	EventListAdapter mAdapter = null;
	DragDropListView mEventListView = null;
	EventStore mEventStore = null;

	public static EventListFragment newInstance() {
    	return new EventListFragment();
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_eventlist,
				container, false);
		mAdapter = new EventListAdapter(getActivity(),
				R.layout.event_list_item, R.id.event_list_item_text);
		mEventListView = (DragDropListView)rootView.findViewById(R.id.event_list);
		mEventListView.setAdapter(mAdapter);
		mEventListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		mEventListView.setOnItemClickListener(this);
		mEventListView.setOnSortedListener(this);
		registerForContextMenu(mEventListView);
		mEventStore = EventStore.getInstance(getActivity());
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateEventList();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void updateEventList() {
		Context context = getActivity();

		mAdapter.clear();

		ArrayList<Event> eventList = EventStore.getInstance(context).listEvents();
		int selPos = -1;
		for (Event event : eventList) {
			mAdapter.add(event);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		EventListAdapter adapter = (EventListAdapter)parent.getAdapter();
		Event event = adapter.getItem(pos);
		try {
			CurrentProfile.setCurrentProfile(parent.getContext(), event.getId());
		}
		catch (ProfileNotFoundException e){
			// ignore
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.event, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int pos = info.position;
		ListView eventListView = (ListView)getView().findViewById(R.id.event_list);
		Event event = (Event)eventListView.getAdapter().getItem(pos);
		switch (item.getItemId()) {
		case R.id.action_edit_event:
			Intent intent = new Intent(getActivity(), EventEditActivity.class);
			intent.putExtra(EventEditActivity.EXTRA_EVENT, event);
			startActivity(intent);
			return true;
		case R.id.action_delete_event:
			EventStore.getInstance(getActivity()).deleteEvent(event.getId());
			updateEventList();
			return true;
		}
		return false;
	}

	@Override
	public void onEventEditPositive(Event newEvent) {
		EventStore.getInstance(getActivity()).storeEvent(newEvent);
		updateEventList();
	}

	@Override
	public void onEventEditNegative() {
	}

	@Override
	public void onSorted(List<VolumeProfile> list) {
		ProfileStore.getInstance(getActivity()).updateDisplayOrder(list);
	}
}
