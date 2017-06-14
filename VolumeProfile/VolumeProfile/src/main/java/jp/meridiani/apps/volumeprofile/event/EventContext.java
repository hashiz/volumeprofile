package jp.meridiani.apps.volumeprofile.event;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */

public class EventContext implements Parcelable {
    private static final String TAG_START = "<event_context>";
    private static final String TAG_END = "</event_context>";

    public static enum BTProfile {
        ANY,
        A2DP,
        HEADSET;
    }

    public static enum BTState {
        ANY,
        CONNECTED,
        DISCONNECTED;
    }

    public static enum Key {
        DEVICEADDR,
        BTPROFILE,
        BTSTATE;

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

    private BluetoothDevice mBTDevice;
    private BTProfile mBTProfile;
    private BTState mBTState;

    EventContext() {
    }

    EventContext(Parcel in) {
        mBTDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(in.readString());
        mBTProfile = BTProfile.valueOf(in.readString());
        mBTState = BTState.valueOf(in.readString());
    }

    public static final Creator<EventContext> CREATOR = new Creator<EventContext>() {
        public EventContext createFromParcel(Parcel in) {
            return new EventContext(in);
        }

        public EventContext[] newArray(int size) {
            return new EventContext[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mBTDevice.getAddress());
        out.writeString(mBTProfile.toString());
        out.writeString(mBTState.toString());
    }

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
}
