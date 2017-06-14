package jp.meridiani.apps.volumeprofile.main;

import jp.meridiani.apps.volumeprofile.event.Event;

public interface EventEditCallback {
	public void onEventEditPositive(Event newEvent);
	public void onEventEditNegative();
}
