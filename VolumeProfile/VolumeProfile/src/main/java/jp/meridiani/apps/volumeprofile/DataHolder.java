package jp.meridiani.apps.volumeprofile;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by kjcs_hashi on 2017/06/15.
 */

public abstract class DataHolder implements Parcelable {

    protected DataHolder() {
    }

    protected DataHolder(Parcel in) {
        for (String key : getKeys()) {
            setValue(key, in.readString());
        }
    }

    protected abstract String TAG_START();
    protected abstract String TAG_END();

    protected abstract String[] getKeys();
    protected abstract void setValue(String key, String value);
    protected abstract String getValue(String key);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        for (String key : getKeys()) {
            out.writeString(getValue(key));
        }
    }

    // for backup
    public void writeToText(BufferedWriter out) throws IOException {
        out.write(TAG_START());
        out.newLine();
        for (String key : getKeys()) {
            String value = getValue(key);
            if (value != null) {
                out.write(key + '=' + value);
                out.newLine();
            }
        }
        out.write(TAG_END());
        out.newLine();
    }

    public static <H extends DataHolder> H createFromText(Class<H> type, BufferedReader rdr) throws IOException, IllegalAccessException, InstantiationException {
        H holder = type.newInstance();
        boolean started = false;
        String line;
        while ((line = rdr.readLine()) != null) {
            if (started) {
                if (holder.TAG_END().equals(line)) {
                    break;
                }
                String[] tmp = line.split("=", 2);
                if (tmp.length < 2) {
                    continue;
                }
                holder.setValue(tmp[0], tmp[1]);
            }
            else {
                if (holder.TAG_START().equals(line)) {
                    started = true;
                }
            }
        }
        return holder;
    }
}
