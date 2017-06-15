package jp.meridiani.apps.volumeprofile.event;

import android.os.Parcel;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.DataHolder;

public class Event extends DataHolder {

	private static final String TAG_START = "<event>";
	private static final String TAG_END = "</event>";
	private static final String EVENTID = "EVENTID";
	private static final String ORDER = "ORDER";
	private static final String[] KEYS = {
			EVENTID,
			ORDER
	};

	private UUID mEventId;
	private int mOrder;
	private EventContext mEventContext = new EventContext();
	private EventAction mEventAction = new EventAction();

	Event() {
		this((UUID)null);
	}

	Event(UUID eventId) {
		if (eventId == null) {
			eventId = UUID.randomUUID();
		}
		mEventId = eventId;
	}

	public Event(Parcel in) {
		super(in);
		mEventContext = new EventContext(in);
		mEventAction = new EventAction(in);
	}

	@Override
	protected String[] getKeys() {
		return KEYS;
	}
	@Override
	protected String TAG_START() {
		return TAG_START;
	}

	@Override
	protected String TAG_END() {
		return TAG_END;
	}

	public UUID getId() {
		return mEventId;
	}

	public void setId(UUID eventId) {
		mEventId = eventId;
	}

	public int getOrder() {
		return mOrder;
	}

	public void setOrder(int order) {
		mOrder = order;
	}

	public EventContext Context() {
		return mEventContext;
	}

	public EventAction Action() {
		return mEventAction;
	}

	@Override
	public String toString() {
		return getId().toString();
	}

	@Override
	protected String getValue(String key) {
		switch (key) {
			case EVENTID:
				return getId().toString();
			case ORDER:
				return Integer.toString(getOrder());
		}
		return null;
	}

	@Override
	protected void setValue(String key, String value) {
		switch (key) {
			case EVENTID:
				setId(UUID.fromString(value));
				break;
			case ORDER:
				setOrder(Integer.parseInt(value));
				break;
		}
	}

    public static final Creator<Event> CREATOR = new Creator<Event>() {
		public Event createFromParcel(Parcel in) {
		    return new Event(in);
		}
		public Event[] newArray(int size) {
		    return new Event[size];
		}
    };

	@Override
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
		mEventContext.writeToParcel(out, flags);
		mEventAction.writeToParcel(out, flags);
	}
}
