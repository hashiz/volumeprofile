package jp.meridiani.apps.volumeprofile.ui;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */
public class VolumeLockAdapter extends ArrayAdapter<VolumeLockItem> {

    public VolumeLockAdapter(Context context, int resource) {
        super(context, resource);
    }

    public int getPosition(VolumeLockValue value) {
        for (int pos = 0; pos < getCount(); pos++) {
            if (value == getItem(pos).getValue()) {
                return pos;
            }
        }
        return 0;
    }
}
