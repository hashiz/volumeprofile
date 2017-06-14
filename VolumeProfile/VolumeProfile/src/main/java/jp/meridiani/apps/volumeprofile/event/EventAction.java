package jp.meridiani.apps.volumeprofile.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */

public class EventAction implements Parcelable {
    private static final String TAG_START = "<event_action>";
    private static final String TAG_END   = "</event_action>";


    public static final Creator<EventAction> CREATOR = new Creator<EventAction>() {
        public EventAction createFromParcel(Parcel in) {
            return new EventAction(in);
        }

        public EventAction[] newArray(int size) {
            return new EventAction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
