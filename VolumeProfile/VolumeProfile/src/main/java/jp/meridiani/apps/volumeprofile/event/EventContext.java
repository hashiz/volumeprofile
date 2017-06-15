package jp.meridiani.apps.volumeprofile.event;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jp.meridiani.apps.volumeprofile.DataHolder;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */

public class EventContext extends DataHolder {
    private static final String TAG_START = "<event_context>";
    private static final String TAG_END = "</event_context>";
    private static final String CHECKBTDEVICE = "CHECKBTDEVICE";
    private static final String BTDEVICEADDR = "BTDEVICEADDR";
    private static final String BTPROFILE = "BTPROFILE";
    private static final String BTSTATE = "BTSTATE";
    private static final String[] KEYS = {
            CHECKBTDEVICE,
            BTDEVICEADDR,
            BTPROFILE,
            BTSTATE
    };

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

    private boolean mCheckBTDevice = false;
    private BluetoothDevice mBTDevice = null;
    private BTProfile mBTProfile = BTProfile.ANY;
    private BTState mBTState = BTState.CONNECTED;

    EventContext() {
    }

    EventContext(Parcel in) {
        super(in);
    }

    public boolean getCheckBTDevice() {
        return mCheckBTDevice;
    }
    public void setCheckBTDevice(boolean value) {
        mCheckBTDevice = value;
    }

    public BluetoothDevice getBTDevice() {
        return mBTDevice;
    }
    public void setBTDevice(BluetoothDevice value) {
        mBTDevice = value;
    }

    public BTProfile getBTProfile() {
        return mBTProfile;
    }
    public void setBTProfile(BTProfile value) {
        mBTProfile = value;
    }

    public BTState getBTState() {
        return mBTState;
    }
    public void setBTState(BTState value) {
        mBTState = value;
    }

    @Override
    protected String TAG_START() {
        return TAG_START;
    }

    @Override
    protected String TAG_END() {
        return TAG_END;
    }

    @Override
    protected String[] getKeys() {
        return KEYS;
    }

    @Override
    protected void setValue(String key, String value) {
        switch (key) {
            case CHECKBTDEVICE:
                mCheckBTDevice = Boolean.parseBoolean(value);
                break;
            case BTDEVICEADDR:
                if (value.equals("null")) {
                    mBTDevice = null;
                }
                else {
                    mBTDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(value);
                }
                break;
            case BTPROFILE:
                mBTProfile = BTProfile.valueOf(value);
                break;
            case BTSTATE:
                mBTState = BTState.valueOf(value);
                break;
        }
    }

    protected String getValue(String key) {
        switch (key) {
            case CHECKBTDEVICE:
                return Boolean.toString(mCheckBTDevice);
            case BTDEVICEADDR:
                if (mBTDevice != null) {
                    return mBTDevice.getAddress();
                }
                return "null";
            case BTPROFILE:
                return mBTProfile.toString();
            case BTSTATE:
                return mBTState.toString();
        }
        return null;
    }

    public static final Creator<EventContext> CREATOR = new Creator<EventContext>() {
        public EventContext createFromParcel(Parcel in) {
            return new EventContext(in);
        }

        public EventContext[] newArray(int size) {
            return new EventContext[size];
        }
    };
}
