package jp.meridiani.apps.volumeprofile.event;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

public class Event implements Parcelable {

	private static final String TAG_START = "<event>";
	private static final String TAG_END = "</event>";

	public static enum Key {
		EVENTID,
		ORDER;

		private final static Key[] sKeys;
		private final static Key[] sDataKeys;
		private final static int sSkip = 2;
		static {
			Key[] values = values();
			sKeys = new Key[values.length];
			sDataKeys = new Key[values.length-sSkip];
			for (int i = 0; i < values.length; i++) {
				Key key = values[i];
				sKeys[i] = key;
				if (i >= sSkip) {
					sDataKeys[i-sSkip] = key;
				}
			}
		};

		public static Key[] getKeys() {
			return sKeys;
		}

		public static Key[] getDataKeys() {
			return sDataKeys;
		}
	}

	private UUID mEventId;
	private int mOrder;
	private EventContext mEventContext;
	private EventAction mAction;

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
		mEventId = UUID.fromString(in.readString());
		mOrder = in.readInt();
	}

	String getValue(Key key) {
		switch (key) {
			case EVENTID:
				return getId().toString();
			case ORDER:
				return Integer.toString(getOrder());
		}
		return null;
	}

	void setValue(String key, String value) {
		Key k;
		try {
			k = Key.valueOf(key);
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			return;
		}
		setValue(k, value);
	}

	void setValue(Key key, String value) {
		switch (key) {
			case EVENTID:
				setId(UUID.fromString(value));
				break;
			case ORDER:
				setOrder(Integer.parseInt(value));
				break;
		}
	}

	static Key[] listDataKeys() {
		return Key.getDataKeys();
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

	@Override
	public String toString() {
		return getId().toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

    @Override
	public void writeToParcel(Parcel out, int flags) {
    	out.writeString(mEventId.toString());
    	out.writeInt(mOrder);
	}

    public static final Creator<Event> CREATOR = new Creator<Event>() {
		public Event createFromParcel(Parcel in) {
		    return new Event(in);
		}
		
		public Event[] newArray(int size) {
		    return new Event[size];
		}
    };

    // for backup
    public void writeToText(BufferedWriter out) throws IOException {
    	out.write(TAG_START);
    	out.newLine();
    	for (Key key : Key.getKeys()) {
    		String value = getValue(key);
    		if (value != null) {
    			out.write(key.name() + '=' + value );
    			out.newLine();
    		}
    	}
    	out.write(TAG_END);
    	out.newLine();
    }

    public static Event createFromText(BufferedReader rdr) throws IOException {
    	Event event = null;
    	boolean started = false;
    	String line;
		while ((line = rdr.readLine()) != null) {
			if (started) {
				if (TAG_END.equals(line)) {
					break;
				}
				String[] tmp = line.split("=", 2);
				if (tmp.length < 2) {
					continue;
				}
				event.setValue(tmp[0], tmp[1]);
			}
			else {
				if (TAG_START.equals(line)) {
					started = true;
					event = new Event();
				}
			}
		}
    	return event;
    }
}
